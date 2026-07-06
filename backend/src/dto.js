const {
  MembershipHistory,
  UserVoucher,
  Follow,
  CommunityPost,
  NearbyPost,
  ProductReview,
  SavedPost,
  ConsultationRequest,
  Wishlist,
  UserAddress,
} = require("./models");
const { serialize } = require("./serialize");

// Shapes a User document into the JSON the Android app expects (mirrors UserDAO.getUserById).
// Mọi số liệu ở màn Profile được đọc TRỰC TIẾP từ các collection thật (không mock):
//  - followers/following  <- follows (followed_id / follower_id)
//  - story_count          <- nearbyposts (story 24h) theo user_id
//  - post_count           <- communityposts theo user_id
//  - review_count         <- productreviews theo user_id
//  - saved_count          <- savedposts theo user_id
//  - consultation_count   <- consultationrequests theo user_id
//  - wishlist_count       <- wishlists theo user_id
//  - address_count        <- useraddresses theo user_id
// Các trường hồ sơ mỹ phẩm (skin_concerns, skin_tone, avoid_ingredients) được TRẢ NGUYÊN
// từ document; nếu user chưa nhập thì là null để app hiển thị trạng thái "Thêm thông tin".
async function toUserDto(userDoc) {
  const user = userDoc.toObject ? userDoc.toObject() : userDoc;
  const uid = user._id;

  const [
    latest,
    voucherCount,
    followersCount,
    followingCount,
    postCount,
    storyCount,
    reviewCount,
    savedCount,
    consultationCount,
    wishlistCount,
    addressCount,
  ] = await Promise.all([
    MembershipHistory.findOne({ user_id: uid }).sort({ changed_at: -1 }).lean(),
    UserVoucher.countDocuments({ user_id: uid, used_at: null }),
    Follow.countDocuments({ followed_id: uid }),
    Follow.countDocuments({ follower_id: uid }),
    CommunityPost.countDocuments({ user_id: uid }),
    NearbyPost.countDocuments({ user_id: uid }),
    ProductReview.countDocuments({ user_id: uid }),
    SavedPost.countDocuments({ user_id: uid }),
    ConsultationRequest.countDocuments({ user_id: uid }),
    Wishlist.countDocuments({ user_id: uid }),
    UserAddress.countDocuments({ user_id: uid }),
  ]);

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
    // Hồ sơ mỹ phẩm mở rộng (có thể null nếu user chưa nhập).
    skin_concerns: user.skin_concerns ?? null,
    skin_tone: user.skin_tone ?? null,
    avoid_ingredients: user.avoid_ingredients ?? null,
    // Số liệu community & mua sắm — đọc thật từ DB.
    followers_count: followersCount,
    following_count: followingCount,
    post_count: postCount,
    story_count: storyCount,
    review_count: reviewCount,
    saved_count: savedCount,
    consultation_count: consultationCount,
    wishlist_count: wishlistCount,
    address_count: addressCount,
  };
}

module.exports = { toUserDto, serialize };
