const express = require("express");
const { Types } = require("mongoose");
const { Blog } = require("../models");
const { serialize } = require("../serialize");

const router = express.Router();
const oid = (v) => (Types.ObjectId.isValid(v) ? new Types.ObjectId(v) : null);

// GET /api/blogs?limit=  -> blog thương hiệu đã xuất bản
router.get("/", async (req, res) => {
  try {
    const limit = Math.min(parseInt(req.query.limit, 10) || 20, 50);
    const blogs = await Blog.find({ is_published: { $ne: false } })
      .sort({ published_at: -1 })
      .limit(limit)
      .lean();
    res.json(blogs.map(serialize));
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// GET /api/blogs/:id  -> chi tiết 1 blog
router.get("/:id", async (req, res) => {
  try {
    const id = oid(req.params.id);
    if (!id) return res.status(400).json({ error: "id không hợp lệ" });
    const blog = await Blog.findById(id).lean();
    if (!blog) return res.status(404).json({ error: "Không tìm thấy bài viết" });
    res.json(serialize(blog));
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

module.exports = router;
