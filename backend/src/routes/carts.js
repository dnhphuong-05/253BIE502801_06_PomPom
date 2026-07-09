const express = require("express");
const { Types } = require("mongoose");
const { Cart, CartItem, Product, ProductImage, ProductVariant } = require("../models");
const { serialize } = require("../serialize");

const router = express.Router();
const oid = (v) => (Types.ObjectId.isValid(v) ? new Types.ObjectId(v) : null);

// Find the user's cart, creating one if needed.
async function getOrCreateCart(userId) {
  let cart = await Cart.findOne({ user_id: userId });
  if (!cart) {
    const now = new Date();
    cart = await Cart.create({ user_id: userId, session_id: null, created_at: now, updated_at: now });
  }
  return cart;
}

// GET /api/carts?user_id=  -> cart with items (product name/thumbnail resolved)
router.get("/", async (req, res) => {
  try {
    const uid = oid(req.query.user_id);
    if (!uid) return res.status(400).json({ error: "user_id không hợp lệ" });

    const cart = await Cart.findOne({ user_id: uid }).lean();
    if (!cart) return res.json({ id: null, items: [], total: 0 });

    const items = await CartItem.find({ cart_id: cart._id }).lean();
    let total = 0;
    for (const it of items) {
      const p = await Product.findById(it.product_id).lean();
      it.product_name = p?.name || null;
      it.price = p?.sale_price && p.sale_price < p.price ? p.sale_price : p?.price || it.unit_price || 0;
      const img = await ProductImage.findOne({ product_id: it.product_id }).sort({ sort_order: 1 }).lean();
      it.thumbnail_url = p?.thumbnail_url || img?.image_url || null;
      if (it.variant_id) {
        const variant = await ProductVariant.findById(it.variant_id).lean();
        it.variant_name = variant?.variant_name || null;
        it.variant_image_url = variant?.image_url || null;
      }
      total += (it.price || 0) * (it.quantity || 0);
    }
    const out = serialize(cart);
    out.items = items.map(serialize);
    out.total = total;
    res.json(out);
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// POST /api/carts/items  { user_id, product_id, variant_id?, quantity }  -> add/increment
router.post("/items", async (req, res) => {
  try {
    const uid = oid(req.body?.user_id);
    const pid = oid(req.body?.product_id);
    const vid = req.body?.variant_id ? oid(req.body.variant_id) : null;
    const qty = Math.max(1, parseInt(req.body?.quantity, 10) || 1);
    if (!uid || !pid) return res.status(400).json({ error: "user_id/product_id không hợp lệ" });

    const product = await Product.findById(pid).lean();
    if (!product) return res.status(404).json({ error: "Không tìm thấy sản phẩm" });

    const cart = await getOrCreateCart(uid);
    const unitPrice = product.sale_price && product.sale_price < product.price ? product.sale_price : product.price;

    // Giỏ hàng theo cấp DÒNG (sản phẩm + biến thể): cùng sản phẩm khác biến thể = 2 dòng riêng.
    // Chỉ gộp số lượng khi TRÙNG cả product_id lẫn variant_id.
    let item = await CartItem.findOne({ cart_id: cart._id, product_id: pid, variant_id: vid });
    if (item) {
      // Loose (strict:false) schemas don't persist `.save()` field edits reliably; use updateOne.
      await CartItem.updateOne({ _id: item._id }, { quantity: (item.quantity || 0) + qty });
      item = await CartItem.findById(item._id);
    } else {
      item = await CartItem.create({
        cart_id: cart._id,
        product_id: pid,
        variant_id: vid,
        quantity: qty,
        unit_price: unitPrice,
      });
    }
    await Cart.updateOne({ _id: cart._id }, { updated_at: new Date() });
    res.status(201).json(serialize(item.toObject()));
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// PUT /api/carts/set  { user_id, product_id, variant_id?, quantity }  -> đặt số lượng đúng DÒNG (0 = xoá)
// variant_id để nhắm đúng biến thể; bỏ trống -> nhắm dòng không có biến thể (variant_id null).
router.put("/set", async (req, res) => {
  try {
    const uid = oid(req.body?.user_id);
    const pid = oid(req.body?.product_id);
    const vid = req.body?.variant_id ? oid(req.body.variant_id) : null;
    const qty = parseInt(req.body?.quantity, 10);
    if (!uid || !pid || !Number.isFinite(qty)) return res.status(400).json({ error: "Tham số không hợp lệ" });

    const cart = await Cart.findOne({ user_id: uid });
    if (!cart) return res.json({ ok: true });
    const lineFilter = { cart_id: cart._id, product_id: pid, variant_id: vid };
    if (qty <= 0) {
      await CartItem.deleteOne(lineFilter);
    } else {
      await CartItem.updateOne(lineFilter, { quantity: qty });
    }
    res.json({ ok: true });
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// DELETE /api/carts/by-product?user_id=&product_id=&variant_id=  -> xoá đúng 1 DÒNG (sản phẩm + biến thể)
router.delete("/by-product", async (req, res) => {
  try {
    const uid = oid(req.query.user_id);
    const pid = oid(req.query.product_id);
    const vid = req.query.variant_id ? oid(req.query.variant_id) : null;
    if (!uid || !pid) return res.status(400).json({ error: "Tham số không hợp lệ" });
    const cart = await Cart.findOne({ user_id: uid });
    if (cart) await CartItem.deleteOne({ cart_id: cart._id, product_id: pid, variant_id: vid });
    res.json({ ok: true });
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// PUT /api/carts/items/:itemId  { quantity }
router.put("/items/:itemId", async (req, res) => {
  try {
    const qty = parseInt(req.body?.quantity, 10);
    if (!Number.isFinite(qty)) return res.status(400).json({ error: "quantity không hợp lệ" });
    if (qty <= 0) {
      await CartItem.deleteOne({ _id: oid(req.params.itemId) });
      return res.json({ ok: true, removed: true });
    }
    const item = await CartItem.findByIdAndUpdate(oid(req.params.itemId), { quantity: qty }, { new: true });
    if (!item) return res.status(404).json({ error: "Không tìm thấy dòng giỏ hàng" });
    res.json(serialize(item.toObject()));
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// DELETE /api/carts?user_id=  -> xoá sạch giỏ của user (dùng sau khi đặt hàng thành công)
router.delete("/", async (req, res) => {
  try {
    const uid = oid(req.query.user_id);
    if (!uid) return res.status(400).json({ error: "user_id không hợp lệ" });
    const cart = await Cart.findOne({ user_id: uid });
    if (cart) await CartItem.deleteMany({ cart_id: cart._id });
    res.json({ ok: true });
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// DELETE /api/carts/items/:itemId
router.delete("/items/:itemId", async (req, res) => {
  try {
    const r = await CartItem.deleteOne({ _id: oid(req.params.itemId) });
    res.json({ ok: r.deletedCount > 0 });
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

module.exports = router;
