const express = require("express");
const { Voucher, Banner, Promotion, PromotionDetail, Notification, Product, ProductImage, Reel } = require("../models");
const { serialize } = require("../serialize");
const { Types } = require("mongoose");

const router = express.Router();
const oid = (v) => (Types.ObjectId.isValid(v) ? new Types.ObjectId(v) : null);

// GET /api/flash-sale?limit=  -> sản phẩm đang giảm giá (cho widget Flash Sale)
router.get("/flash-sale", async (req, res) => {
  try {
    const limit = Math.min(parseInt(req.query.limit, 10) || 6, 20);
    const all = await Product.find({ is_active: { $ne: false } }).lean();
    const onSale = [];
    for (const p of all) {
      let price = p.price || 0;
      let sale = p.sale_price || 0;
      if (sale > price) { const t = price; price = sale; sale = t; }
      if (sale > 0 && sale < price) {
        let img = p.thumbnail_url;
        if (!img) {
          const i = await ProductImage.findOne({ product_id: p._id }).sort({ sort_order: 1 }).lean();
          img = i?.image_url || null;
        }
        onSale.push({
          product_id: p._id.toString(),
          name: p.name,
          image_url: img,
          original_price: price,
          sale_price: sale,
          discount_percent: Math.round((1 - sale / price) * 100),
          total_stock: 400,
          sold_count: 50 + Math.floor(Math.random() * 200),
        });
      }
      if (onSale.length >= limit) break;
    }
    res.json(onSale);
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// GET /api/vouchers  -> active vouchers
router.get("/vouchers", async (req, res) => {
  const list = await Voucher.find({ is_active: { $ne: false } }).lean();
  res.json(list.map(serialize));
});

// GET /api/banners
router.get("/banners", async (req, res) => {
  const list = await Banner.find({ is_active: { $ne: false } }).sort({ sort_order: 1 }).lean();
  res.json(list.map(serialize));
});

// GET /api/promotions  -> promotions + their detail products
router.get("/promotions", async (req, res) => {
  const promos = await Promotion.find({ is_active: { $ne: false } }).lean();
  for (const pr of promos) {
    const details = await PromotionDetail.find({ promotion_id: pr._id }).lean();
    for (const d of details) {
      const p = await Product.findById(d.product_id).lean();
      d.product_name = p?.name || null;
      d.price = p?.price || null;
      d.sale_price = p?.sale_price || null;
    }
    pr.details = details;
  }
  res.json(promos.map(serialize));
});

// GET /api/reels?limit=  -> reels đăng lại từ Instagram/Facebook, có gắn thẻ sản phẩm
router.get("/reels", async (req, res) => {
  try {
    const limit = Math.min(parseInt(req.query.limit, 10) || 20, 50);
    const reels = await Reel.find({ is_active: { $ne: false } })
      .sort({ created_at: -1 })
      .limit(limit)
      .lean();
    for (const r of reels) {
      const tagIds = Array.isArray(r.product_tags) ? r.product_tags : [];
      const products = await Product.find({ _id: { $in: tagIds } })
        .select("name thumbnail_url price sale_price")
        .lean();
      r.product_tags = products.map(serialize);
    }
    res.json(reels.map(serialize));
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// GET /api/notifications?user_id=
router.get("/notifications", async (req, res) => {
  const list = await Notification.find({ user_id: oid(req.query.user_id) })
    .sort({ created_at: -1 })
    .lean();
  res.json(list.map(serialize));
});

module.exports = router;
