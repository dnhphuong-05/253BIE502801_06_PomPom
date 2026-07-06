const express = require("express");
const { Types } = require("mongoose");
const { Expert, ExpertArticle, ConsultationRequest, Product } = require("../models");
const { serialize } = require("../serialize");

const router = express.Router();
const oid = (v) => (Types.ObjectId.isValid(v) ? new Types.ObjectId(v) : null);

async function withExpertInfo(articles) {
  for (const a of articles) {
    const e = await Expert.findById(a.expert_id).lean();
    a.expert_name = e?.name || null;
    a.expert_title = e?.title || null;
    a.expert_avatar = e?.avatar_url || null;
  }
  return articles;
}

// product_tags luôn được trả về dạng object đầy đủ (không phải chuỗi id thô) ở
// mọi endpoint, để phía app không phải xử lý 2 kiểu dữ liệu khác nhau.
async function resolveProductTags(docs) {
  for (const d of docs) {
    const tagIds = Array.isArray(d.product_tags) ? d.product_tags : [];
    const products = await Product.find({ _id: { $in: tagIds } })
      .select("name thumbnail_url price sale_price")
      .lean();
    d.product_tags = products.map(serialize);
  }
  return docs;
}

// GET /api/experts  -> chuyên gia đang nhận tư vấn
router.get("/experts", async (req, res) => {
  try {
    const experts = await Expert.find({ is_available: { $ne: false } }).lean();
    res.json(experts.map(serialize));
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// GET /api/expert-articles?limit=&expert_id=  -> bài viết tips từ bác sĩ tư vấn
router.get("/expert-articles", async (req, res) => {
  try {
    const filter = { is_published: { $ne: false } };
    if (req.query.expert_id) filter.expert_id = oid(req.query.expert_id);
    const limit = Math.min(parseInt(req.query.limit, 10) || 20, 50);
    let articles = await ExpertArticle.find(filter).sort({ published_at: -1 }).limit(limit).lean();
    articles = await resolveProductTags(articles);
    articles = await withExpertInfo(articles);
    res.json(articles.map(serialize));
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// GET /api/expert-articles/:id  -> chi tiết bài viết + hồ sơ chuyên gia + sản phẩm gắn thẻ
router.get("/expert-articles/:id", async (req, res) => {
  try {
    const id = oid(req.params.id);
    if (!id) return res.status(400).json({ error: "id không hợp lệ" });
    const article = await ExpertArticle.findById(id).lean();
    if (!article) return res.status(404).json({ error: "Không tìm thấy bài viết" });

    const expert = await Expert.findById(article.expert_id).lean();
    article.expert = expert ? serialize(expert) : null;

    await resolveProductTags([article]);

    res.json(serialize(article));
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// POST /api/consultation-requests  { user_id?, expert_id, name, phone, email?, skin_type?, topic, message, preferred_channel, source_article_id? }
router.post("/consultation-requests", async (req, res) => {
  try {
    const b = req.body || {};
    const expertId = oid(b.expert_id);
    if (!expertId) return res.status(400).json({ error: "expert_id không hợp lệ" });
    if (!b.name || !b.phone) return res.status(400).json({ error: "Thiếu tên hoặc số điện thoại" });

    const request = await ConsultationRequest.create({
      user_id: b.user_id ? oid(b.user_id) : null,
      expert_id: expertId,
      source_article_id: b.source_article_id ? oid(b.source_article_id) : null,
      name: b.name,
      phone: b.phone,
      email: b.email || null,
      skin_type: b.skin_type || null,
      topic: b.topic || "",
      message: b.message || "",
      preferred_channel: b.preferred_channel || "phone",
      status: "pending",
      created_at: new Date(),
    });
    res.status(201).json(serialize(request.toObject()));
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

module.exports = router;
