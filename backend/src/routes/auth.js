const express = require("express");
const bcrypt = require("bcryptjs");
const { User, MembershipHistory } = require("../models");
const { toUserDto } = require("../dto");

const router = express.Router();

// POST /api/auth/login  { email, password } -> user dto or 401
router.post("/login", async (req, res) => {
  try {
    const { email, password } = req.body || {};
    if (!email || !password) return res.status(400).json({ error: "email và password là bắt buộc" });

    const user = await User.findOne({ email, status: "active" });
    if (!user) return res.status(401).json({ error: "Email hoặc mật khẩu không đúng" });

    // Seeded data uses bcrypt hashes; fall back to plaintext compare for legacy rows.
    const stored = user.password_hash || "";
    const ok = stored.startsWith("$2") ? await bcrypt.compare(password, stored) : stored === password;
    if (!ok) return res.status(401).json({ error: "Email hoặc mật khẩu không đúng" });

    // Loose (strict:false) schemas don't persist `.save()` field edits reliably; use updateOne.
    await User.updateOne({ _id: user._id }, { last_login: new Date() });
    res.json(await toUserDto(user));
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// POST /api/auth/register  { full_name, email, password, phone_number } -> created user dto
router.post("/register", async (req, res) => {
  try {
    const { full_name, email, password, phone_number } = req.body || {};
    if (!full_name || !email || !password) return res.status(400).json({ error: "Thiếu thông tin bắt buộc" });
    if (await User.exists({ email })) return res.status(409).json({ error: "Email đã tồn tại" });

    const now = new Date();
    const user = await User.create({
      full_name,
      email,
      password_hash: await bcrypt.hash(password, 10),
      phone_number: phone_number || "",
      avatar_url: "",
      bio: "",
      join_date: now,
      role: "user",
      status: "active",
      last_login: now,
      created_at: now,
      updated_at: now,
    });

    await MembershipHistory.create({ user_id: user._id, level: "Bronze Member", points: 0, changed_at: now });
    res.status(201).json(await toUserDto(user));
  } catch (e) {
    res.status(500).json({ error: e.message });
  }
});

// GET /api/auth/email-exists?email=...
router.get("/email-exists", async (req, res) => {
  const exists = await User.exists({ email: req.query.email });
  res.json({ exists: !!exists });
});

module.exports = router;
