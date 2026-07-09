const mongoose = require("mongoose");

// The teammate migrated the DB so every collection uses ObjectId `_id` and ObjectId
// foreign keys (e.g. products.category_id -> categories._id). We use loose schemas
// (strict:false) so each model reads/writes whatever fields the document actually has,
// while pinning the exact collection name that currently holds the data.
//
// `indexes` (tuỳ chọn): danh sách index để MongoDB dựng khi khởi động. Rất quan trọng cho
// các trường được lọc/đếm thường xuyên (nhất là màn Profile: toUserDto chạy ~11 count theo
// user_id). Không có index -> mỗi count phải quét TOÀN BỘ collection -> trang "Me" tải chậm.
function model(name, collection, indexes = []) {
  const schema = new mongoose.Schema({}, { strict: false, collection, versionKey: false });
  for (const spec of indexes) schema.index(spec);
  return mongoose.model(name, schema);
}

// NearbyPost: story 24h theo bán kính GPS. Cần 2 index đặc biệt mà model() không tạo:
//  - 2dsphere trên `location` (GeoJSON Point) để truy vấn theo bán kính (km).
//  - TTL trên `expires_at` (expireAfterSeconds:0) để MongoDB tự xoá sau đúng thời điểm hết hạn (24h).
const nearbyPostSchema = new mongoose.Schema({}, { strict: false, collection: "nearbyposts", versionKey: false });
nearbyPostSchema.index({ location: "2dsphere" });
nearbyPostSchema.index({ expires_at: 1 }, { expireAfterSeconds: 0 });
nearbyPostSchema.index({ user_id: 1 }); // story_count ở màn Profile đếm theo user_id.
const NearbyPost = mongoose.model("NearbyPost", nearbyPostSchema);

module.exports = {
  NearbyPost,
  User: model("User", "users"),
  Product: model("Product", "products"),
  Category: model("Category", "categories"),
  ProductImage: model("ProductImage", "productimages", [{ product_id: 1 }]),
  ProductVariant: model("ProductVariant", "productvariants"),
  ProductReview: model("ProductReview", "productreviews", [{ user_id: 1 }]),
  Order: model("Order", "orders", [{ user_id: 1 }]),
  OrderItem: model("OrderItem", "orderitems", [{ order_id: 1 }]),
  OrderStatusHistory: model("OrderStatusHistory", "orderstatushistories", [{ order_id: 1 }]),
  Payment: model("Payment", "payments"),
  Cart: model("Cart", "carts"),
  CartItem: model("CartItem", "cartitems"),
  CommunityPost: model("CommunityPost", "communityposts", [{ user_id: 1 }]),
  Comment: model("Comment", "comments", [{ user_id: 1 }]),
  Like: model("Like", "likes", [{ user_id: 1 }]),
  // follows: đếm followers (following_id) & following (follower_id) ở màn Profile,
  // kiểm tra/tạo/xoá quan hệ theo cặp (follower_id, following_id).
  Follow: model("Follow", "follows", [{ following_id: 1 }, { follower_id: 1 }]),
  Voucher: model("Voucher", "vouchers"),
  UserVoucher: model("UserVoucher", "uservouchers", [{ user_id: 1, used_at: 1 }]),
  Banner: model("Banner", "banners"),
  UserAddress: model("UserAddress", "useraddresses", [{ user_id: 1 }]),
  MembershipHistory: model("MembershipHistory", "membershiphistories", [{ user_id: 1, changed_at: -1 }]),
  PointsTransaction: model("PointsTransaction", "pointstransactions", [{ user_id: 1 }]),
  Notification: model("Notification", "notifications"),
  Wishlist: model("Wishlist", "wishlists", [{ user_id: 1 }]),
  Promotion: model("Promotion", "promotions"),
  PromotionDetail: model("PromotionDetail", "promotiondetails"),
  SavedPost: model("SavedPost", "savedposts", [{ user_id: 1 }]),
  HiddenPost: model("HiddenPost", "hiddenposts"),
  Reel: model("Reel", "reels"),
  Blog: model("Blog", "blogs"),
  ExpertArticle: model("ExpertArticle", "expertarticles"),
  Expert: model("Expert", "experts"),
  ConsultationRequest: model("ConsultationRequest", "consultationrequests", [{ user_id: 1 }]),
};
