const express = require("express");
const { Types } = require("mongoose");
const { NearbyPost, User } = require("../models");
const { serialize } = require("../serialize");

const router = express.Router();
const oid = (v) => (Types.ObjectId.isValid(v) ? new Types.ObjectId(v) : null);
const STORY_TTL_MS = 24 * 60 * 60 * 1000;

// GET /api/nearby-posts?user_id=  -> tất cả story của một user (mới nhất trước), cho màn "Story đã đăng".
// GET /api/nearby-posts?lat=&lng=&radius_km=  -> story còn hạn trong bán kính (mặc định 100km)
router.get("/", async (req, res) => {
  try {
    // Nhánh "story của tôi": lọc theo user_id, bỏ qua GPS.
    if (req.query.user_id) {
      const uid = oid(req.query.user_id);
      if (!uid) return res.status(400).json({ error: "user_id không hợp lệ" });
      const mine = await NearbyPost.find({ user_id: uid }).sort({ created_at: -1 }).lean();
      const u = await User.findById(uid).lean();
      for (const p of mine) {
        p.user_name = u?.full_name || "Người dùng";
        p.user_avatar = u?.avatar_url || null;
      }
      return res.json(mine.map(serialize));
    }

    const lat = parseFloat(req.query.lat);
    const lng = parseFloat(req.query.lng);
    if (!Number.isFinite(lat) || !Number.isFinite(lng)) {
      return res.status(400).json({ error: "Thiếu lat/lng hợp lệ" });
    }
    const radiusKm = Math.min(parseFloat(req.query.radius_km) || 100, 500);

    const posts = await NearbyPost.find({
      expires_at: { $gt: new Date() },
      location: {
        $nearSphere: {
          $geometry: { type: "Point", coordinates: [lng, lat] },
          $maxDistance: radiusKm * 1000,
        },
      },
    }).lean();

    for (const p of posts) {
      const u = await User.findById(p.user_id).lean();
      p.user_name = u?.full_name || "Người dùng";
      p.user_avatar = u?.avatar_url || null;
    }
    res.json(posts.map(serialize));
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// POST /api/nearby-posts  { user_id, media_url, media_type, caption?, lat, lng }
router.post("/", async (req, res) => {
  try {
    const b = req.body || {};
    const userId = oid(b.user_id);
    const lat = parseFloat(b.lat);
    const lng = parseFloat(b.lng);
    if (!userId) return res.status(400).json({ error: "user_id không hợp lệ" });
    if (!b.media_url || !["image", "video"].includes(b.media_type)) {
      return res.status(400).json({ error: "Thiếu media_url hoặc media_type không hợp lệ" });
    }
    if (!Number.isFinite(lat) || !Number.isFinite(lng)) {
      return res.status(400).json({ error: "Thiếu lat/lng hợp lệ" });
    }

    const now = new Date();
    const post = await NearbyPost.create({
      user_id: userId,
      media_url: b.media_url,
      media_type: b.media_type,
      caption: b.caption || "",
      location: { type: "Point", coordinates: [lng, lat] },
      created_at: now,
      expires_at: new Date(now.getTime() + STORY_TTL_MS),
    });

    const u = await User.findById(userId).lean();
    const out = serialize(post.toObject());
    out.user_name = u?.full_name || "Người dùng";
    out.user_avatar = u?.avatar_url || null;
    res.status(201).json(out);
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

module.exports = router;
