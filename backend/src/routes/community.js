const express = require("express");
const { Types } = require("mongoose");
const { CommunityPost, Comment, User, Product, Like, SavedPost, Notification } = require("../models");
const { serialize } = require("../serialize");

const router = express.Router();
const oid = (v) => (Types.ObjectId.isValid(v) ? new Types.ObjectId(v) : null);

// Tạo thông báo cho chủ bài viết khi có người khác thích/bình luận — bỏ qua khi tự tương tác
// với bài của chính mình (không cần tự thông báo cho bản thân).
async function notifyPostOwner(postId, actorId, type, message) {
  const post = await CommunityPost.findById(postId).lean();
  if (!post || String(post.user_id) === String(actorId)) return;
  const actor = await User.findById(actorId).lean();
  await Notification.create({
    user_id: post.user_id,
    type,
    actor_id: actorId,
    actor_name: actor?.full_name || "Người dùng",
    actor_avatar: actor?.avatar_url || null,
    post_id: postId,
    message,
    is_read: false,
    created_at: new Date(),
  });
}

async function withAuthors(posts) {
  for (const p of posts) {
    const u = await User.findById(p.user_id).lean();
    p.author_name = u?.full_name || "Người dùng";
    p.author_avatar = u?.avatar_url || null;
  }
  return posts;
}

// Gắn is_saved/is_liked của viewerId vào từng bài — để nút bookmark/tim hiện đúng
// trạng thái ngay từ lần tải đầu, không chỉ là toggle tạm trên UI.
async function withViewerState(posts, viewerId) {
  if (!viewerId) return posts;
  const postIds = posts.map((p) => p._id);
  const [savedIds, likedIds] = await Promise.all([
    SavedPost.find({ user_id: viewerId, post_id: { $in: postIds } }).distinct("post_id"),
    Like.find({ user_id: viewerId, post_id: { $in: postIds } }).distinct("post_id"),
  ]);
  const savedSet = new Set(savedIds.map(String));
  const likedSet = new Set(likedIds.map(String));
  for (const p of posts) {
    p.is_saved = savedSet.has(String(p._id));
    p.is_liked = likedSet.has(String(p._id));
  }
  return posts;
}

// GET /api/community/posts?limit=&q=&author_id=&saved_by=&viewer_id=
//  -> "Bài viết" (mặc định): mọi bài hiển thị
//  -> "Của bạn": author_id=<uid> lọc theo đúng người đăng
//  -> "Đã lưu": saved_by=<uid> lọc theo bài đã lưu của uid đó
//  -> viewer_id: gắn is_saved/is_liked theo đúng người đang xem (khác với author_id/saved_by)
router.get("/posts", async (req, res) => {
  try {
    const filter = { is_hidden: { $ne: true } };
    if (req.query.q) filter.content = { $regex: req.query.q, $options: "i" };
    if (req.query.author_id) {
      const authorId = oid(req.query.author_id);
      if (!authorId) return res.status(400).json({ error: "author_id không hợp lệ" });
      filter.user_id = authorId;
    }
    if (req.query.saved_by) {
      const savedBy = oid(req.query.saved_by);
      if (!savedBy) return res.status(400).json({ error: "saved_by không hợp lệ" });
      const savedIds = await SavedPost.find({ user_id: savedBy }).distinct("post_id");
      filter._id = { $in: savedIds };
    }

    let query = CommunityPost.find(filter).sort({ created_at: -1 });
    if (req.query.limit) query = query.limit(parseInt(req.query.limit, 10));
    let posts = await withAuthors(await query.lean());
    posts = await withViewerState(posts, oid(req.query.viewer_id));
    res.json(posts.map(serialize));
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// GET /api/community/highlights?limit=  -> most-liked posts
router.get("/highlights", async (req, res) => {
  try {
    const limit = Math.min(parseInt(req.query.limit, 10) || 6, 30);
    const posts = await withAuthors(
      await CommunityPost.find({ is_hidden: { $ne: true } }).sort({ like_count: -1 }).limit(limit).lean()
    );
    res.json(posts.map(serialize));
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// POST /api/community/posts  { user_id, content, images? }  -> create a post
router.post("/posts", async (req, res) => {
  try {
    const b = req.body || {};
    const uid = oid(b.user_id);
    if (!uid || !b.content) return res.status(400).json({ error: "Thiếu user_id hoặc nội dung" });
    const post = await CommunityPost.create({
      user_id: uid,
      content: b.content,
      images: Array.isArray(b.images) ? b.images : b.images ? [b.images] : [],
      product_tag: b.product_tag ? oid(b.product_tag) : null,
      like_count: 0,
      comment_count: 0,
      is_hidden: false,
      created_at: new Date(),
    });
    res.status(201).json(serialize(post.toObject()));
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// GET /api/community/posts/:id/tagged  -> sản phẩm được gắn thẻ trong bài
router.get("/posts/:id/tagged", async (req, res) => {
  try {
    const post = await CommunityPost.findById(oid(req.params.id)).lean();
    if (!post || !post.product_tag) return res.json([]);
    const p = await Product.findById(post.product_tag).lean();
    res.json(p ? [serialize(p)] : []);
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// GET /api/community/posts/:id  -> single post + author
router.get("/posts/:id", async (req, res) => {
  try {
    const post = await CommunityPost.findById(oid(req.params.id)).lean();
    if (!post) return res.status(404).json({ error: "Không tìm thấy bài viết" });
    const u = await User.findById(post.user_id).lean();
    post.author_name = u?.full_name || null;
    post.author_avatar = u?.avatar_url || null;
    res.json(serialize(post));
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// GET /api/community/posts/:id/comments
router.get("/posts/:id/comments", async (req, res) => {
  try {
    const comments = await Comment.find({ post_id: oid(req.params.id) })
      .sort({ created_at: 1 })
      .lean();
    for (const c of comments) {
      const u = await User.findById(c.user_id).lean();
      c.author_name = u?.full_name || null;
      c.author_avatar = u?.avatar_url || null;
    }
    res.json(comments.map(serialize));
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// POST /api/community/posts/:id/comments  { user_id, content }
router.post("/posts/:id/comments", async (req, res) => {
  try {
    const b = req.body || {};
    const uid = oid(b.user_id);
    const postId = oid(req.params.id);
    if (!uid || !postId || !b.content) return res.status(400).json({ error: "Thiếu user_id hoặc nội dung" });

    const comment = await Comment.create({
      post_id: postId,
      user_id: uid,
      content: b.content,
      created_at: new Date(),
    });
    await CommunityPost.updateOne({ _id: postId }, { $inc: { comment_count: 1 } });
    await notifyPostOwner(postId, uid, "comment", "đã bình luận về bài viết của bạn");

    const u = await User.findById(uid).lean();
    const out = serialize(comment.toObject());
    out.author_name = u?.full_name || "Người dùng";
    out.author_avatar = u?.avatar_url || null;
    res.status(201).json(out);
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// POST /api/community/posts/:id/like  { user_id }  -> toggle like (idempotent)
router.post("/posts/:id/like", async (req, res) => {
  try {
    const uid = oid(req.body?.user_id);
    const postId = oid(req.params.id);
    if (!uid || !postId) return res.status(400).json({ error: "Thiếu user_id" });

    const existing = await Like.findOne({ user_id: uid, post_id: postId }).lean();
    if (existing) {
      await Like.deleteOne({ _id: existing._id });
      await CommunityPost.updateOne({ _id: postId }, { $inc: { like_count: -1 } });
      res.json({ liked: false });
    } else {
      await Like.create({ user_id: uid, post_id: postId, created_at: new Date() });
      await CommunityPost.updateOne({ _id: postId }, { $inc: { like_count: 1 } });
      await notifyPostOwner(postId, uid, "like", "đã thích bài viết của bạn");
      res.json({ liked: true });
    }
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// POST /api/community/posts/:id/save  { user_id }  -> toggle lưu bài (cho tab "Đã lưu")
router.post("/posts/:id/save", async (req, res) => {
  try {
    const uid = oid(req.body?.user_id);
    const postId = oid(req.params.id);
    if (!uid || !postId) return res.status(400).json({ error: "Thiếu user_id" });

    const existing = await SavedPost.findOne({ user_id: uid, post_id: postId }).lean();
    if (existing) {
      await SavedPost.deleteOne({ _id: existing._id });
      res.json({ saved: false });
    } else {
      await SavedPost.create({ user_id: uid, post_id: postId, created_at: new Date() });
      res.json({ saved: true });
    }
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

module.exports = router;
