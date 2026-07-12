const express = require("express");
const { Types } = require("mongoose");
const { Order, OrderItem, Product, ProductImage, Cart, CartItem, OrderStatusHistory, User, ProductReview } = require("../models");
const { serialize } = require("../serialize");

const router = express.Router();
const oid = (v) => (Types.ObjectId.isValid(v) ? new Types.ObjectId(v) : null);
// Thuế VAT áp cho đơn hàng (8%). Đặt một chỗ để Checkout (client) và đơn (server) dùng chung một mức.
const TAX_RATE = 0.08;

// GET /api/orders?user_id=&status=&phone=  -> orders enriched with item_count + first item preview
router.get("/", async (req, res) => {
  try {
    const filter = {};
    if (req.query.user_id) filter.user_id = oid(req.query.user_id);
    if (req.query.status) filter.status = req.query.status;
    if (req.query.phone) {
      // Đơn của khách theo số điện thoại (join qua users.phone_number).
      const users = await User.find({ phone_number: req.query.phone }).select("_id").lean();
      filter.user_id = { $in: users.map((u) => u._id) };
    }
    const orders = await Order.find(filter).sort({ created_at: -1 }).lean();
    if (!orders.length) return res.json([]);

    // Batch mọi truy vấn phụ theo lô thay vì N+1 (mỗi đơn từng gọi 2-3 query tuần tự
    // -> chậm rõ rệt trên Render free tier). Gom hết id cần tra trước, mỗi loại tra 1 lần.
    const orderIds = orders.map((o) => o._id);
    const allItems = orderIds.length ? await OrderItem.find({ order_id: { $in: orderIds } }).lean() : [];
    const productIds = [...new Set(allItems.map((it) => String(it.product_id)))].map(oid).filter(Boolean);
    const products = productIds.length ? await Product.find({ _id: { $in: productIds } }).lean() : [];
    const productsById = new Map(products.map((p) => [String(p._id), p]));
    const missingThumbIds = products.filter((p) => !p.thumbnail_url).map((p) => p._id);
    const fallbackImages = missingThumbIds.length
      ? await ProductImage.find({ product_id: { $in: missingThumbIds } }).sort({ sort_order: 1 }).lean()
      : [];
    const fallbackImageByProduct = new Map();
    for (const img of fallbackImages) {
      const key = String(img.product_id);
      if (!fallbackImageByProduct.has(key)) fallbackImageByProduct.set(key, img.image_url);
    }

    const itemsByOrder = new Map();
    for (const it of allItems) {
      const key = String(it.order_id);
      if (!itemsByOrder.has(key)) itemsByOrder.set(key, []);
      itemsByOrder.get(key).push(it);
    }

    // Đơn nào đã được đánh giá đủ (mọi sản phẩm) -> ẩn nút "Đánh giá", chỉ còn "Mua lại".
    const reviewedKeys = new Set();
    if (filter.user_id && orderIds.length) {
      const reviews = await ProductReview.find({ user_id: filter.user_id, order_id: { $in: orderIds } }).lean();
      for (const r of reviews) reviewedKeys.add(`${r.order_id}|${r.product_id}`);
    }

    for (const o of orders) {
      const items = itemsByOrder.get(String(o._id)) || [];
      o.item_count = items.length;
      o.first_item_name = null;
      o.first_item_image = null;
      o.item_product_ids = items.map((it) => ({ product_id: it.product_id, quantity: it.quantity }));
      o.is_reviewed = items.length > 0 && items.every((it) => reviewedKeys.has(`${o._id}|${it.product_id}`));
      if (items.length) {
        const p = productsById.get(String(items[0].product_id));
        if (p) {
          o.first_item_name = p.name || null;
          o.first_item_image = p.thumbnail_url || fallbackImageByProduct.get(String(p._id)) || null;
        }
      }
    }
    res.json(orders.map(serialize));
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// GET /api/orders/meta  -> các hãng vận chuyển & phương thức thanh toán (distinct)
router.get("/meta", async (req, res) => {
  try {
    const carriers = (await Order.distinct("shipping_carrier")).filter(Boolean).sort();
    const paymentMethods = (await Order.distinct("payment_method")).filter(Boolean).sort();
    res.json({ carriers, payment_methods: paymentMethods });
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// GET /api/orders/counts?user_id=  -> { status: count }
router.get("/counts", async (req, res) => {
  const rows = await Order.aggregate([
    { $match: { user_id: oid(req.query.user_id) } },
    { $group: { _id: "$status", count: { $sum: 1 } } },
  ]);
  res.json(Object.fromEntries(rows.map((r) => [r._id, r.count])));
});

// GET /api/orders/:id?user_id=  -> order + line items (with product name/thumbnail/is_reviewed)
router.get("/:id", async (req, res) => {
  try {
    const order = await Order.findById(oid(req.params.id)).lean();
    if (!order) return res.status(404).json({ error: "Không tìm thấy đơn hàng" });
    const items = await OrderItem.find({ order_id: order._id }).lean();

    const userId = oid(req.query.user_id);
    const reviewedProductIds = new Set();
    if (userId) {
      const reviews = await ProductReview.find({ user_id: userId, order_id: order._id }).lean();
      for (const r of reviews) reviewedProductIds.add(String(r.product_id));
    }

    for (const it of items) {
      const p = await Product.findById(it.product_id).lean();
      it.product_name = p?.name || null;
      it.product_thumbnail = p?.thumbnail_url || null;
      if (p && !it.product_thumbnail) {
        const img = await ProductImage.findOne({ product_id: p._id }).sort({ sort_order: 1 }).lean();
        it.product_thumbnail = img?.image_url || null;
      }
      it.is_reviewed = reviewedProductIds.has(String(it.product_id));
    }
    order.items = items;
    order.is_reviewed = items.length > 0 && items.every((it) => it.is_reviewed);

    // Lịch sử trạng thái để dựng timeline theo dõi đơn (cũ -> mới).
    order.status_history = await OrderStatusHistory
      .find({ order_id: order._id })
      .sort({ created_at: 1 })
      .lean();

    res.json(serialize(order));
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// POST /api/orders/:id/cancel  { user_id } -> huỷ đơn (chỉ khi chưa giao cho vận chuyển)
router.post("/:id/cancel", async (req, res) => {
  try {
    const orderId = oid(req.params.id);
    const order = await Order.findById(orderId).lean();
    if (!order) return res.status(404).json({ error: "Không tìm thấy đơn hàng" });
    if (!["pending", "confirmed", "processing"].includes(order.status)) {
      return res.status(400).json({ error: "Không thể hủy đơn ở trạng thái này" });
    }
    // Dùng findByIdAndUpdate thay vì find + gán field + save(): document strict:false không
    // khai báo schema path nào nên Mongoose không tự track thay đổi qua gán trực tiếp + save().
    const updated = await Order.findByIdAndUpdate(
      orderId,
      { $set: { status: "cancelled" } },
      { new: true }
    ).lean();
    await OrderStatusHistory.create({
      order_id: orderId,
      status: "cancelled",
      note: "Khách hủy đơn",
      created_at: new Date(),
    });
    res.json(serialize(updated));
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// POST /api/orders  -> create an order.
// Body: { user_id, address_id?, payment_method?, shipping_fee?, discount_amount?, note?,
//         items?: [{product_id, variant_id?, quantity}], from_cart?: bool }
// If from_cart is true (or items omitted), lines are pulled from the user's cart and the cart is cleared.
router.post("/", async (req, res) => {
  try {
    const b = req.body || {};
    const uid = oid(b.user_id);
    if (!uid) return res.status(400).json({ error: "user_id không hợp lệ" });

    // Resolve the line items.
    let sourceItems = [];
    let cart = null;
    const useCart = b.from_cart === true || !Array.isArray(b.items) || b.items.length === 0;
    if (useCart) {
      cart = await Cart.findOne({ user_id: uid });
      if (cart) sourceItems = await CartItem.find({ cart_id: cart._id }).lean();
    } else {
      sourceItems = b.items.map((i) => ({
        product_id: oid(i.product_id),
        variant_id: i.variant_id ? oid(i.variant_id) : null,
        quantity: Math.max(1, parseInt(i.quantity, 10) || 1),
      }));
    }
    if (!sourceItems.length) return res.status(400).json({ error: "Không có sản phẩm để đặt" });

    // Price each line from the product and compute totals.
    let totalAmount = 0;
    const lines = [];
    for (const it of sourceItems) {
      const p = await Product.findById(it.product_id).lean();
      if (!p) continue;
      const price = p.sale_price && p.sale_price < p.price ? p.sale_price : p.price;
      const qty = it.quantity || 1;
      totalAmount += price * qty;
      lines.push({ product_id: it.product_id, variant_id: it.variant_id || null, quantity: qty, price });
    }
    if (!lines.length) return res.status(400).json({ error: "Sản phẩm không hợp lệ" });

    const shippingFee = Number(b.shipping_fee) || 0;
    const discount = Number(b.discount_amount) || 0;
    // VAT 8% tính trên tạm tính (server tự tính để khớp với số hiển thị ở Checkout).
    const taxAmount = Math.round(totalAmount * TAX_RATE);
    const finalAmount = Math.max(0, totalAmount + taxAmount + shippingFee - discount);
    const now = new Date();
    const orderNumber = "PP-ORD-" + now.getTime().toString(36).toUpperCase();

    const order = await Order.create({
      order_number: orderNumber,
      user_id: uid,
      session_id: null,
      address_id: b.address_id ? oid(b.address_id) : null,
      total_amount: totalAmount,
      tax_amount: taxAmount,
      shipping_fee: shippingFee,
      discount_amount: discount,
      final_amount: finalAmount,
      status: "pending",
      payment_method: b.payment_method || "COD",
      payment_status: "unpaid",
      shipping_carrier: b.shipping_carrier || null,
      tracking_number: null,
      note: b.note || "",
      created_at: now,
    });

    await OrderItem.insertMany(lines.map((l) => ({ ...l, order_id: order._id })));
    await OrderStatusHistory.create({ order_id: order._id, status: "pending", note: "Tạo đơn", created_at: now });

    // Clear the cart if the order came from it.
    if (useCart && cart) await CartItem.deleteMany({ cart_id: cart._id });

    const out = serialize(order.toObject());
    out.items = lines.length;
    res.status(201).json(out);
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

module.exports = router;
