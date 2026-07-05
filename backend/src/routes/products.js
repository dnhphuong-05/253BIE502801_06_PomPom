const express = require("express");
const { Types } = require("mongoose");
const { Product, ProductImage, ProductVariant, ProductReview, Category, User } = require("../models");
const { serialize } = require("../serialize");

const router = express.Router();
const oid = (v) => (Types.ObjectId.isValid(v) ? new Types.ObjectId(v) : null);

// GET /api/products?category_id=&q=&min_price=&max_price=&min_rating=&sort=
// sort ∈ alpha | price_asc | price_desc | newest | popular
router.get("/", async (req, res) => {
  try {
    const filter = { is_active: { $ne: false } };
    if (req.query.category_id) {
      // Hỗ trợ 1 hoặc nhiều category_id (phân tách bằng dấu phẩy) → $in.
      const ids = String(req.query.category_id).split(",").map(oid).filter(Boolean);
      if (ids.length) filter.category_id = { $in: ids };
    }
    if (req.query.q) filter.name = { $regex: req.query.q, $options: "i" };

    let products = await Product.find(filter).lean();
    products = await Promise.all(products.map(attachThumb));

    // Giá hiển thị (xử lý quirk sale_price > price của dữ liệu seed).
    for (const p of products) {
      let price = p.price || 0;
      let sale = p.sale_price || 0;
      if (sale > price) { const t = price; price = sale; sale = t; }
      p._effPrice = sale > 0 && sale < price ? sale : price;
    }

    const minPrice = req.query.min_price != null ? Number(req.query.min_price) : null;
    const maxPrice = req.query.max_price != null ? Number(req.query.max_price) : null;
    const minRating = req.query.min_rating != null ? Number(req.query.min_rating) : null;
    if (minPrice != null) products = products.filter((p) => p._effPrice >= minPrice);
    if (maxPrice != null) products = products.filter((p) => p._effPrice <= maxPrice);

    // Rating/popularity chỉ tính khi cần (lọc theo sao hoặc sort popular).
    const needStats = minRating != null || req.query.sort === "popular";
    if (needStats) {
      for (const p of products) {
        const reviews = await ProductReview.find({ product_id: p._id }).lean();
        p._reviewCount = reviews.length;
        p._rating = reviews.length ? reviews.reduce((s, r) => s + (r.rating || 0), 0) / reviews.length : 0;
      }
      if (minRating != null) products = products.filter((p) => (p._rating || 0) >= minRating);
    }

    switch (req.query.sort) {
      case "alpha": products.sort((a, b) => (a.name || "").localeCompare(b.name || "")); break;
      case "price_asc": products.sort((a, b) => a._effPrice - b._effPrice); break;
      case "price_desc": products.sort((a, b) => b._effPrice - a._effPrice); break;
      case "newest": products.sort((a, b) => String(b.created_at || "").localeCompare(String(a.created_at || ""))); break;
      case "popular": products.sort((a, b) => (b._reviewCount || 0) - (a._reviewCount || 0)); break;
      default: break;
    }

    products.forEach((p) => { delete p._effPrice; delete p._rating; delete p._reviewCount; });
    res.json(products.map(serialize));
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// GET /api/products/:id  -> product + images + variants + review summary
router.get("/:id", async (req, res) => {
  try {
    const id = oid(req.params.id);
    if (!id) return res.status(400).json({ error: "id không hợp lệ" });

    const product = await Product.findById(id).lean();
    if (!product) return res.status(404).json({ error: "Không tìm thấy sản phẩm" });

    const [images, variants, reviews] = await Promise.all([
      ProductImage.find({ product_id: id }).sort({ sort_order: 1 }).lean(),
      ProductVariant.find({ product_id: id }).lean(),
      ProductReview.find({ product_id: id }).lean(),
    ]);

    product.images = images.map((i) => i.image_url);
    product.thumbnail_url = product.thumbnail_url || images[0]?.image_url || null;
    product.variants = variants;
    product.review_count = reviews.length;
    product.rating_avg = reviews.length
      ? reviews.reduce((s, r) => s + (r.rating || 0), 0) / reviews.length
      : 0;
    const cat = product.category_id ? await Category.findById(product.category_id).lean() : null;
    product.category_name = cat?.category_name || "";
    res.json(serialize(product));
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// GET /api/products/:id/reviews  -> reviews with author name/avatar
router.get("/:id/reviews", async (req, res) => {
  try {
    const reviews = await ProductReview.find({ product_id: oid(req.params.id) })
      .sort({ created_at: -1 })
      .lean();
    for (const r of reviews) {
      const u = await User.findById(r.user_id).lean();
      r.user_name = u?.full_name || "Người dùng";
      r.user_avatar = u?.avatar_url || null;
    }
    res.json(reviews.map(serialize));
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// GET /api/products/:id/related  -> products in the same category (excluding self)
router.get("/:id/related", async (req, res) => {
  try {
    const id = oid(req.params.id);
    const product = await Product.findById(id).lean();
    if (!product) return res.json([]);
    const limit = Math.min(parseInt(req.query.limit, 10) || 6, 20);
    const related = await Product.find({
      category_id: product.category_id,
      _id: { $ne: id },
      is_active: { $ne: false },
    })
      .limit(limit)
      .lean();
    const out = await Promise.all(related.map(attachThumb));
    res.json(out.map(serialize));
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

async function attachThumb(p) {
  if (!p.thumbnail_url) {
    const img = await ProductImage.findOne({ product_id: p._id }).sort({ sort_order: 1 }).lean();
    p.thumbnail_url = img?.image_url || null;
  }
  return p;
}

module.exports = router;

// Categories mounted separately at /api/categories
const categoriesRouter = express.Router();
categoriesRouter.get("/", async (req, res) => {
  const cats = await Category.find().sort({ sort_order: 1 }).lean();
  res.json(cats.map(serialize));
});
module.exports.categoriesRouter = categoriesRouter;
