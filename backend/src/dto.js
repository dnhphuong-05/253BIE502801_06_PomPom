const { MembershipHistory, UserVoucher } = require("./models");
const { serialize } = require("./serialize");

// Shapes a User document into the JSON the Android app expects (mirrors UserDAO.getUserById).
async function toUserDto(userDoc) {
  const user = userDoc.toObject ? userDoc.toObject() : userDoc;
  const uid = user._id;

  const latest = await MembershipHistory.findOne({ user_id: uid }).sort({ changed_at: -1 }).lean();
  const voucherCount = await UserVoucher.countDocuments({ user_id: uid, used_at: null });

  return {
    id: uid.toString(),
    full_name: user.full_name,
    email: user.email,
    phone_number: user.phone_number,
    avatar_url: user.avatar_url,
    bio: user.bio,
    voucher_count: voucherCount,
    membership_level: latest?.level || "Bronze Member",
    points: latest?.points || 0,
    gender: user.gender,
    birth_date: user.birth_date ? serialize(user.birth_date) : null,
    skin_type: user.skin_type,
  };
}

module.exports = { toUserDto, serialize };
