const mongoose = require("mongoose");

// The teammate migrated the DB so every collection uses ObjectId `_id` and ObjectId
// foreign keys (e.g. products.category_id -> categories._id). We use loose schemas
// (strict:false) so each model reads/writes whatever fields the document actually has,
// while pinning the exact collection name that currently holds the data.
function model(name, collection) {
  const schema = new mongoose.Schema({}, { strict: false, collection, versionKey: false });
  return mongoose.model(name, schema);
}

module.exports = {
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
};
