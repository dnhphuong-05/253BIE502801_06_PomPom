const express = require("express");
const bcrypt = require("bcryptjs");
const { Types } = require("mongoose");
const { User, UserAddress, Wishlist, Product, ProductImage, UserVoucher, Voucher, PointsTransaction, Follow, Notification } = require("../models");
const { toUserDto, serialize } = require("../dto");

const router = express.Router();
const oid = (v) => (Types.ObjectId.isValid(v) ? new Types.ObjectId(v) : null);

// GET /api/users/:id  -> user dto
router.get("/:id", async (req, res) => {
  try {
    const user = await User.findById(oid(req.params.id));
    if (!user) return res.status(404).json({ error: "Không tìm thấy người dùng" });
    res.json(await toUserDto(user));
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// PUT /api/users/:id  -> update editable profile fields
router.put("/:id", async (req, res) => {
  try {
    const allowed = ["full_name", "phone_number", "bio", "gender", "birth_date", "skin_type"];
    const update = { updated_at: new Date() };
    for (const k of allowed) if (k in req.body) update[k] = req.body[k];
    const user = await User.findByIdAndUpdate(oid(req.params.id), update, { new: true });
    if (!user) return res.status(404).json({ error: "Không tìm thấy người dùng" });
    res.json(await toUserDto(user));
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// GET /api/users/:id/addresses
router.get("/:id/addresses", async (req, res) => {
  const list = await UserAddress.find({ user_id: oid(req.params.id) })
    .sort({ is_default: -1 })
    .lean();
  res.json(list.map(serialize));
});

// GET /api/users/:id/vouchers?onlyAvailable=true
router.get("/:id/vouchers", async (req, res) => {
  const q = { user_id: oid(req.params.id) };
  if (req.query.onlyAvailable === "true") q.used_at = null;
  const uv = await UserVoucher.find(q).lean();
  const voucherIds = uv.map((x) => x.voucher_id);
  const vouchers = await Voucher.find({ _id: { $in: voucherIds } }).lean();
  res.json(vouchers.map(serialize));
});

// POST /api/users/:id/vouchers/:voucherId  -> lưu voucher cho user (idempotent)
router.post("/:id/vouchers/:voucherId", async (req, res) => {
  try {
    const uid = oid(req.params.id);
    const vid = oid(req.params.voucherId);
    if (!uid || !vid) return res.status(400).json({ error: "id không hợp lệ" });
    const existing = await UserVoucher.findOne({ user_id: uid, voucher_id: vid });
    if (!existing) {
      await UserVoucher.create({ user_id: uid, voucher_id: vid, assigned_at: new Date(), used_at: null });
    }
    res.status(201).json({ ok: true });
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// GET /api/users/:id/wishlist -> products
router.get("/:id/wishlist", async (req, res) => {
  const wl = await Wishlist.find({ user_id: oid(req.params.id) }).sort({ created_at: -1 }).lean();
  const ids = wl.map((w) => w.product_id);
  const products = await Product.find({ _id: { $in: ids } }).lean();
  for (const p of products) {
    if (!p.thumbnail_url) {
      const img = await ProductImage.findOne({ product_id: p._id }).sort({ sort_order: 1 }).lean();
      p.thumbnail_url = img?.image_url || null;
    }
  }
  res.json(products.map(serialize));
});

// GET /api/users/:id/points -> points transactions
router.get("/:id/points", async (req, res) => {
  const tx = await PointsTransaction.find({ user_id: oid(req.params.id) }).sort({ created_at: -1 }).lean();
  res.json(tx.map(serialize));
});

// ---------------------------------------------------------------- write ops

// POST /api/users/:id/change-password  { old_password, new_password }
router.post("/:id/change-password", async (req, res) => {
  try {
    const { old_password, new_password } = req.body || {};
    if (!old_password || !new_password) return res.status(400).json({ error: "Thiếu mật khẩu" });

    const user = await User.findById(oid(req.params.id));
    if (!user) return res.status(404).json({ error: "Không tìm thấy người dùng" });

    const stored = user.password_hash || "";
    const ok = stored.startsWith("$2") ? await bcrypt.compare(old_password, stored) : stored === old_password;
    if (!ok) return res.status(400).json({ error: "Mật khẩu hiện tại không đúng" });

    // Loose (strict:false) schemas don't persist `.save()` field edits reliably; use updateOne.
    await User.updateOne(
      { _id: user._id },
      { password_hash: await bcrypt.hash(new_password, 10), updated_at: new Date() }
    );
    res.json({ ok: true });
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// POST /api/users/:id/addresses  -> add address (optionally default)
router.post("/:id/addresses", async (req, res) => {
  try {
    const uid = oid(req.params.id);
    const b = req.body || {};
    if (b.is_default) await UserAddress.updateMany({ user_id: uid }, { is_default: false });
    const addr = await UserAddress.create({
      user_id: uid,
      label: b.label || "",
      recipient_name: b.recipient_name || "",
      phone: b.phone || "",
      address_line: b.address_line || "",
      ward: b.ward || "",
      district: b.district || "",
      city: b.city || "",
      is_default: !!b.is_default,
    });
    res.status(201).json(serialize(addr.toObject()));
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// PUT /api/users/:id/addresses/:addrId/default  -> set as default
router.put("/:id/addresses/:addrId/default", async (req, res) => {
  try {
    const uid = oid(req.params.id);
    await UserAddress.updateMany({ user_id: uid }, { is_default: false });
    await UserAddress.updateOne({ _id: oid(req.params.addrId), user_id: uid }, { is_default: true });
    res.json({ ok: true });
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// DELETE /api/users/:id/addresses/:addrId
router.delete("/:id/addresses/:addrId", async (req, res) => {
  try {
    const r = await UserAddress.deleteOne({ _id: oid(req.params.addrId), user_id: oid(req.params.id) });
    res.json({ ok: r.deletedCount > 0 });
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// POST /api/users/:id/wishlist  { product_id }  -> add (idempotent)
router.post("/:id/wishlist", async (req, res) => {
  try {
    const uid = oid(req.params.id);
    const pid = oid(req.body?.product_id);
    if (!pid) return res.status(400).json({ error: "product_id không hợp lệ" });
    const existing = await Wishlist.findOne({ user_id: uid, product_id: pid });
    if (!existing) await Wishlist.create({ user_id: uid, product_id: pid, created_at: new Date() });
    res.status(201).json({ ok: true });
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// DELETE /api/users/:id/wishlist/:productId
router.delete("/:id/wishlist/:productId", async (req, res) => {
  try {
    const r = await Wishlist.deleteOne({ user_id: oid(req.params.id), product_id: oid(req.params.productId) });
    res.json({ ok: r.deletedCount > 0 });
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// GET /api/users/:id/follow-status?follower_id=  -> follower_id đang có theo dõi :id hay không
router.get("/:id/follow-status", async (req, res) => {
  try {
    const followingId = oid(req.params.id);
    const followerId = oid(req.query.follower_id);
    if (!followingId || !followerId) return res.status(400).json({ error: "id không hợp lệ" });
    const exists = await Follow.exists({ follower_id: followerId, following_id: followingId });
    res.json({ following: !!exists });
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// POST /api/users/:id/follow  { follower_id }  -> theo dõi (idempotent) + tạo thông báo
router.post("/:id/follow", async (req, res) => {
  try {
    const followingId = oid(req.params.id);
    const followerId = oid(req.body?.follower_id);
    if (!followingId || !followerId) return res.status(400).json({ error: "id không hợp lệ" });
    if (String(followingId) === String(followerId)) {
      return res.status(400).json({ error: "Không thể tự theo dõi chính mình" });
    }

    const existing = await Follow.findOne({ follower_id: followerId, following_id: followingId });
    if (!existing) {
      await Follow.create({ follower_id: followerId, following_id: followingId, created_at: new Date() });
      const actor = await User.findById(followerId).lean();
      await Notification.create({
        user_id: followingId,
        type: "follow",
        actor_id: followerId,
        actor_name: actor?.full_name || "Người dùng",
        actor_avatar: actor?.avatar_url || null,
        post_id: null,
        message: "đã bắt đầu theo dõi bạn",
        is_read: false,
        created_at: new Date(),
      });
    }
    res.status(201).json({ following: true });
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// DELETE /api/users/:id/follow?follower_id=  -> bỏ theo dõi
router.delete("/:id/follow", async (req, res) => {
  try {
    const followingId = oid(req.params.id);
    const followerId = oid(req.query.follower_id);
    if (!followingId || !followerId) return res.status(400).json({ error: "id không hợp lệ" });
    await Follow.deleteOne({ follower_id: followerId, following_id: followingId });
    res.json({ following: false });
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

module.exports = router;
