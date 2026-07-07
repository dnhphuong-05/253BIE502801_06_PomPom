const express = require("express");
const { Types } = require("mongoose");
const { Order, OrderItem, Product, ProductImage, Cart, CartItem, OrderStatusHistory, User } = require("../models");
const { serialize } = require("../serialize");

const router = express.Router();
const oid = (v) => (Types.ObjectId.isValid(v) ? new Types.ObjectId(v) : null);

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

    for (const o of orders) {
      const items = await OrderItem.find({ order_id: o._id }).lean();
      o.item_count = items.length;
      o.first_item_name = null;
      o.first_item_image = null;
      if (items.length) {
        const p = await Product.findById(items[0].product_id).lean();
        o.first_item_name = p?.name || null;
        o.first_item_image = p?.thumbnail_url || null;
        if (!o.first_item_image && p) {
          const img = await ProductImage.findOne({ product_id: p._id }).sort({ sort_order: 1 }).lean();
          o.first_item_image = img?.image_url || null;
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

// GET /api/orders/:id  -> order + line items (with product name/thumbnail)
router.get("/:id", async (req, res) => {
  try {
    const order = await Order.findById(oid(req.params.id)).lean();
    if (!order) return res.status(404).json({ error: "Không tìm thấy đơn hàng" });
    const items = await OrderItem.find({ order_id: order._id }).lean();
    for (const it of items) {
      const p = await Product.findById(it.product_id).lean();
      it.product_name = p?.name || null;
      it.product_thumbnail = p?.thumbnail_url || null;
      if (p && !it.product_thumbnail) {
        const img = await ProductImage.findOne({ product_id: p._id }).sort({ sort_order: 1 }).lean();
        it.product_thumbnail = img?.image_url || null;
      }
    }
    order.items = items;

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
    const finalAmount = Math.max(0, totalAmount + shippingFee - discount);
    const now = new Date();
    const orderNumber = "PP-ORD-" + now.getTime().toString(36).toUpperCase();

    const order = await Order.create({
      order_number: orderNumber,
      user_id: uid,
      session_id: null,
      address_id: b.address_id ? oid(b.address_id) : null,
      total_amount: totalAmount,
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
