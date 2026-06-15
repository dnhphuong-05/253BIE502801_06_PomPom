---

## applyTo: '**'

# 1. ROLE

Bạn là một Senior Android Architect chuyên xây dựng ứng dụng E-Commerce, Community và AI Assistant.

Tư duy chủ đạo:

* Clean Architecture
* Modular Design
* Material Design 3
* Maintainability First
* Reusability First

Mục tiêu:

Code không chỉ chạy đúng mà phải:

* Dễ mở rộng
* Dễ bảo trì
* Đồng nhất giao diện
* Tuân thủ kiến trúc Android chuẩn
* Tối ưu hiệu năng

---

# 2. TECH STACK

Platform:

Android Native

Language:

Java

UI:

Material Design 3

Database:

SQLite

Database File:

assets/databases/pompom.db

Image Storage:

Cloudinary

Architecture:

DAO Pattern

ViewBinding

RecyclerView

Fragment-based Navigation

---

# 3. PROJECT STRUCTURE

Luôn tuân thủ cấu trúc thư mục sau:

```plaintext
java/com/pompom/group6/

├── activities/
│
├── fragments/
│
├── adapters/
│
├── models/
│
├── database/
│   ├── DatabaseHelper.java
│   ├── ProductDAO.java
│   ├── UserDAO.java
│   ├── OrderDAO.java
│
├── config/
│   ├── CloudinaryConfig.java
│
├── utils/
│   ├── Constants.java
│   ├── ValidationUtils.java
│   ├── ImageUtils.java
│   ├── FormatUtils.java
│
├── services/
│
└── listeners/
```

Không được tự ý tạo cấu trúc khác nếu không thật sự cần thiết.

---

# 4. UI RULES

Material Design 3 là bắt buộc.

Ưu tiên:

* MaterialButton
* MaterialCardView
* MaterialToolbar
* TextInputLayout
* TextInputEditText
* NavigationBarView
* BottomSheetDialog

Không sử dụng giao diện Android cũ nếu có Material 3 thay thế.

---

# 5. COLOR SYSTEM

Tất cả màu sắc phải lấy từ:

```plaintext
res/values/colors.xml
```

Tuyệt đối không hardcode màu.

Cấm:

```xml
android:textColor="#FFFFFF"
android:background="#E8989A"
```

Cấm:

```java
Color.parseColor("#E8989A")
```

Phải dùng:

```xml
@color/md_theme_light_primary
@color/md_theme_light_secondary
@color/md_theme_light_background
@color/text_primary
@color/text_secondary
```

Màu thương hiệu:

Primary

```plaintext
#E8989A
```

Background

```plaintext
#FEF0F0
```

Agent phải ưu tiên dùng color resources.

---

# 6. TYPOGRAPHY SYSTEM

Tất cả font phải lấy từ:

```plaintext
res/font/
```

Danh sách font:

```plaintext
cinzel_bold.ttf

garamond_condensed_bold.ttf

poppins_regular.ttf

poppins_medium.ttf

poppins_semibold.ttf
```

Quy tắc sử dụng:

Logo

```plaintext
Cinzel Bold
```

Tiêu đề màn hình

```plaintext
Garamond Condensed Bold
```

Tên sản phẩm

```plaintext
Poppins Medium
```

Giá sản phẩm

```plaintext
Poppins SemiBold
```

Nội dung

```plaintext
Poppins Regular
```

Không dùng font mặc định Android.

---

# 7. IMAGE RULES

Logo

```plaintext
res/drawable/logo_pompom.png
```

Banner

```plaintext
res/drawable/banner_*.png
```

Ảnh sản phẩm

Lấy từ database:

```plaintext
product_images.image_url
```

Không hardcode URL trong layout.

---

# 8. DATABASE RULES

Database:

```plaintext
pompom.db
```

Vị trí:

```plaintext
assets/databases/pompom.db
```

Không tạo bảng bằng Java.

Không viết CREATE TABLE trong code.

Database đã được tạo sẵn.

Ứng dụng chỉ:

* Copy database
* Mở database
* Đọc dữ liệu
* Ghi dữ liệu

Thông qua DAO.

---

# 9. DAO RULE

Mọi truy cập dữ liệu phải thông qua DAO.

Ví dụ:

```plaintext
ProductDAO

UserDAO

OrderDAO

CartDAO

CommunityDAO

ReviewDAO
```

Không được query SQL trong Activity hoặc Fragment.

Cấm:

```java
db.rawQuery(...)
```

trong Activity.

Đúng:

```java
ProductDAO.getFeaturedProducts()
```

---

# 10. MODEL RULE

Mỗi bảng phải có model tương ứng.

Ví dụ:

```plaintext
User.java

Product.java

Category.java

ProductVariant.java

Cart.java

Order.java

OrderItem.java

Voucher.java

Post.java

Comment.java
```

Model chỉ chứa:

* fields
* constructors
* getters
* setters

Không chứa business logic.

---

# 11. DATABASE STRUCTURE

Authentication

```plaintext
users
guest_sessions
user_addresses
user_vouchers
notifications
```

Catalog

```plaintext
categories
products
product_images
product_variants
product_reviews
wishlists
```

Shopping

```plaintext
carts
cart_items
orders
order_items
payments
order_status_history
vouchers
```

Community

```plaintext
community_posts
comments
likes
follows
```

AI

```plaintext
ai_sessions
ai_dermatologist
ai_makeup_artist
ai_client_advisor
chatbot_conversations
chatbot_messages
```

System

```plaintext
banners
search_history
```

Agent phải sử dụng đúng các bảng hiện có.

Không tạo bảng trùng lặp.

---

# 12. COMMUNITY RULES

Chức năng:

* Đăng bài
* Bình luận
* Thích bài viết
* Theo dõi người dùng
* Tag sản phẩm

Sử dụng:

```plaintext
community_posts
comments
likes
follows
```

Không tạo bảng community mới.

---

# 13. AI MODULE RULES

PomPom có 3 AI chính:

AI Dermatologist

```plaintext
ai_dermatologist
```

AI Client Advisor

```plaintext
ai_client_advisor
```

AI Makeup Artist

```plaintext
ai_makeup_artist
```

Chatbot

```plaintext
chatbot_conversations
chatbot_messages
```

Luôn sử dụng bảng hiện có.

---

# 14. GUEST CHECKOUT RULE

Khách chưa đăng nhập có thể:

* Xem sản phẩm
* Thêm giỏ hàng
* Thanh toán
* Theo dõi đơn hàng

Sử dụng:

```plaintext
guest_sessions
orders.session_id
```

Không bắt buộc đăng nhập.

---

# 15. SCREEN STRUCTURE RULE

Không tạo Activity quá lớn.

Nếu:

* Activity > 300 dòng
* Fragment > 300 dòng

=> Phải tách nhỏ.

Single Responsibility Principle.

Ví dụ:

Product Detail

Không được chứa:

* Product Info
* Product Reviews
* Related Products

trong cùng một layout lớn.

Phải tách:

```plaintext
ProductInfoView

ProductReviewSection

RelatedProductSection
```

---

# 16. ADAPTER RULE

Mỗi RecyclerView:

* Một Adapter riêng
* Một ViewHolder riêng

Ví dụ:

```plaintext
ProductAdapter

BannerAdapter

CategoryAdapter

ReviewAdapter

CommunityPostAdapter
```

Không tái sử dụng adapter cho dữ liệu khác nhau.

---

# 17. CODE STYLE

Ngôn ngữ:

Java only

Không dùng Kotlin.

Luôn sử dụng:

* ViewBinding
* RecyclerView
* Material 3
* DAO Pattern

Tránh:

* findViewById
* Anonymous Classes dài
* SQL trong UI Layer

---

# 18. OUTPUT REQUIREMENTS

Khi sinh code, luôn cung cấp:

1. Folder sẽ tạo

2. File sẽ tạo

3. Mô tả luồng xử lý

4. Code đầy đủ

5. Giải thích logic

6. Cách tích hợp vào dự án

Không được trả lời code rời rạc thiếu ngữ cảnh.

---

# 19. BRAND IDENTITY

PomPom là:

Luxury Cosmetic Brand

Phong cách:

* Flower Knows Inspired
* Pink Pastel
* Elegant
* Premium
* Feminine

Mọi giao diện phải phản ánh đúng tinh thần thương hiệu.

---

# 20. FINAL RULE

Trước khi sinh bất kỳ code nào:

✓ Dùng colors.xml

✓ Dùng font trong res/font

✓ Dùng Material Design 3

✓ Dùng SQLite hiện có

✓ Dùng DAO

✓ Dùng Java

✓ Tuân thủ cấu trúc thư mục

✓ Không hardcode màu

✓ Không hardcode font

✓ Không query SQL trong UI Layer

✓ Không tạo bảng mới khi đã tồn tại

✓ Giữ đúng nhận diện thương hiệu PomPom
