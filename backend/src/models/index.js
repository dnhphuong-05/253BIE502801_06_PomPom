const mongoose = require("mongoose");

// The teammate migrated the DB so every collection uses ObjectId `_id` and ObjectId
// foreign keys (e.g. products.category_id -> categories._id). We use loose schemas
// (strict:false) so each model reads/writes whatever fields the document actually has,
// while pinning the exact collection name that currently holds the data.
function model(name, collection) {
  const schema = new mongoose.Schema({}, { strict: false, collection, versionKey: false });
  return mongoose.model(name, schema);
}

// NearbyPost: story 24h theo bán kính GPS. Cần 2 index đặc biệt mà model() không tạo:
//  - 2dsphere trên `location` (GeoJSON Point) để truy vấn theo bán kính (km).
//  - TTL trên `expires_at` (expireAfterSeconds:0) để MongoDB tự xoá sau đúng thời điểm hết hạn (24h).
const nearbyPostSchema = new mongoose.Schema({}, { strict: false, collection: "nearbyposts", versionKey: false });
nearbyPostSchema.index({ location: "2dsphere" });
nearbyPostSchema.index({ expires_at: 1 }, { expireAfterSeconds: 0 });
const NearbyPost = mongoose.model("NearbyPost", nearbyPostSchema);

module.exports = {
  NearbyPost,
  User: model("User", "users"),
  Product: model("Product", "products"),
  Category: model("Category", "categories"),
  ProductImage: model("ProductImage", "productimages"),
  ProductVariant: model("ProductVariant", "productvariants"),
  ProductReview: model("ProductReview", "productreviews"),
  Order: model("Order", "orders"),
  OrderItem: model("OrderItem", "orderitems"),
  OrderStatusHistory: model("OrderStatusHistory", "orderstatushistories"),
  Payment: model("Payment", "payments"),
  Cart: model("Cart", "carts"),
  CartItem: model("CartItem", "cartitems"),
  CommunityPost: model("CommunityPost", "communityposts"),
  Comment: model("Comment", "comments"),
  Like: model("Like", "likes"),
  Follow: model("Follow", "follows"),
  Voucher: model("Voucher", "vouchers"),
  UserVoucher: model("UserVoucher", "uservouchers"),
  Banner: model("Banner", "banners"),
  UserAddress: model("UserAddress", "useraddresses"),
  MembershipHistory: model("MembershipHistory", "membershiphistories"),
  PointsTransaction: model("PointsTransaction", "pointstransactions"),
  Notification: model("Notification", "notifications"),
  Wishlist: model("Wishlist", "wishlists"),
  Promotion: model("Promotion", "promotions"),
  PromotionDetail: model("PromotionDetail", "promotiondetails"),
  SavedPost: model("SavedPost", "savedposts"),
  HiddenPost: model("HiddenPost", "hiddenposts"),
  Reel: model("Reel", "reels"),
  Blog: model("Blog", "blogs"),
  ExpertArticle: model("ExpertArticle", "expertarticles"),
  Expert: model("Expert", "experts"),
  ConsultationRequest: model("ConsultationRequest", "consultationrequests"),
};
