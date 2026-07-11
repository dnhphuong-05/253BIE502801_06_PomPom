# ĐẶC TẢ DỮ LIỆU — CƠ SỞ DỮ LIỆU POMPOM

Cơ sở dữ liệu: **MongoDB Atlas** (database `pompom_db`), truy cập qua Mongoose (`src/models/index.js`).
Toàn bộ đặc tả dưới đây được trích xuất trực tiếp từ dữ liệu thật trên Atlas (kiểu dữ liệu, index, giá trị enum) kết hợp với logic nghiệp vụ trong `src/routes/*`.

**Quy ước chung:**
- Khoá chính (PK) của mọi collection là `_id` kiểu `ObjectId`, do MongoDB tự sinh.
- Các cột có hậu tố `_id` (không phải `_id` chính nó) là **khoá ngoại (FK)**, kiểu `ObjectId`, tham chiếu `_id` của collection khác.
- Cột `__v` (version key nội bộ của Mongoose) xuất hiện ở hầu hết collection nhưng không mang ý nghĩa nghiệp vụ nên **không liệt kê** trong các bảng bên dưới.
- Vì model dùng schema "loose" (`strict:false`), ràng buộc NOT NULL/kiểu dữ liệu chỉ được áp bởi tầng ứng dụng (routes), không phải do MongoDB ép buộc; cột "Ràng buộc" phản ánh thực tế dữ liệu quan sát được + logic ở route.

---

## 1. Nhóm Người dùng

### 1.1. `users` — Tài khoản người dùng

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK | Định danh người dùng |
| `full_name` | String | Bắt buộc | Họ tên hiển thị |
| `email` | String | Bắt buộc, UNIQUE (index) | Email đăng nhập |
| `password_hash` | String | Bắt buộc | Mật khẩu đã băm (bcrypt) |
| `phone_number` | String | Không bắt buộc | Số điện thoại |
| `avatar_url` | String | Không bắt buộc | Ảnh đại diện |
| `avatar_frame` | String | Không bắt buộc; giá trị quan sát được: `gold`, `mint` | Khung avatar trang trí theo hạng thành viên |
| `bio` | String | Không bắt buộc | Tiểu sử ngắn |
| `join_date` | Date | Bắt buộc | Ngày tạo tài khoản |
| `role` | String | Bắt buộc; enum: `user`, `admin` | Vai trò phân quyền |
| `status` | String | Bắt buộc; enum quan sát được: `active` | Trạng thái tài khoản |
| `last_login` | Date | Bắt buộc | Lần đăng nhập gần nhất |
| `birth_date` | Date/String | Không bắt buộc | Ngày sinh (`YYYY-MM-DD`) |
| `gender` | String | Không bắt buộc; enum: `male`, `female` | Giới tính |
| `skin_type` | String | Không bắt buộc; enum: `oily`, `dry`, `normal`, `combination`, `sensitive` | Loại da |
| `skin_tone` | String | Không bắt buộc | Tông da (hồ sơ mỹ phẩm mở rộng) |
| `skin_concerns` | Array\<String\> | Không bắt buộc, có thể null | Các vấn đề về da quan tâm |
| `avoid_ingredients` | Array\<String\> | Không bắt buộc, có thể null | Thành phần cần tránh |
| `created_at` | Date | Bắt buộc | Thời điểm tạo bản ghi |
| `updated_at` | Date | Bắt buộc | Thời điểm cập nhật gần nhất |

### 1.2. `useraddresses` — Địa chỉ giao hàng

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK | Định danh địa chỉ |
| `user_id` | ObjectId | FK → `users._id`, bắt buộc (có index) | Chủ sở hữu địa chỉ |
| `label` | String | Bắt buộc | Nhãn địa chỉ (VD: "Nhà", "Công ty") |
| `recipient_name` | String | Bắt buộc | Tên người nhận |
| `phone` | String | Bắt buộc | SĐT người nhận |
| `address_line` | String | Bắt buộc | Số nhà, tên đường |
| `city` | String | Bắt buộc | Tỉnh/thành phố |
| `district` | String | Bắt buộc | Quận/huyện |
| `ward` | String | Bắt buộc | Phường/xã |
| `is_default` | Boolean | Bắt buộc | Có phải địa chỉ mặc định hay không |

### 1.3. `guestsessions` — Phiên khách vãng lai (chưa đăng nhập)

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK; được dùng làm FK `carts.session_id` / `orders.session_id` | Định danh phiên khách |
| `session_id` | String | Bắt buộc, UNIQUE (index) | Mã phiên do client sinh, dùng để nhận diện khách chưa đăng nhập |
| `created_at` | Date | Bắt buộc | Thời điểm tạo phiên |
| `last_activity` | Date | Bắt buộc | Thời điểm hoạt động gần nhất |

---

## 2. Nhóm Danh mục & Sản phẩm

### 2.1. `categories` — Danh mục sản phẩm

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK | Định danh danh mục |
| `category_name` | String | Bắt buộc | Tên danh mục |
| `parent_id` | ObjectId | FK → `categories._id`, có thể null (danh mục gốc) | Danh mục cha (cây phân cấp) |
| `sort_order` | Number | Bắt buộc | Thứ tự hiển thị |
| `image_url` | String | Bắt buộc | Ảnh minh hoạ danh mục |

### 2.2. `products` — Sản phẩm

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK | Định danh sản phẩm |
| `name` | String | Bắt buộc | Tên sản phẩm |
| `slug` | String | Bắt buộc, UNIQUE (index) | Định danh dạng URL |
| `description` | String | Bắt buộc | Mô tả sản phẩm |
| `price` | Number | Bắt buộc, ≥ 0 | Giá niêm yết |
| `sale_price` | Number | Bắt buộc, ≥ 0 | Giá khuyến mãi/giá bán thực tế |
| `stock` | Number | Bắt buộc, ≥ 0 | Số lượng tồn kho |
| `sku` | String | Bắt buộc, UNIQUE (index) | Mã hàng (Stock Keeping Unit) |
| `category_id` | ObjectId | FK → `categories._id`, bắt buộc (có index) | Danh mục của sản phẩm |
| `brand` | String | Bắt buộc | Thương hiệu |
| `is_active` | Boolean | Bắt buộc (có index) | Sản phẩm còn kinh doanh/hiển thị hay không |

### 2.3. `productimages` — Ảnh sản phẩm

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK | Định danh ảnh |
| `product_id` | ObjectId | FK → `products._id`, bắt buộc (index kết hợp với `sort_order`) | Sản phẩm sở hữu ảnh |
| `image_url` | String | Bắt buộc | Đường dẫn ảnh |
| `sort_order` | Number | Bắt buộc | Thứ tự hiển thị trong gallery |

### 2.4. `productvariants` — Biến thể sản phẩm (màu/tone/size)

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK | Định danh biến thể |
| `product_id` | ObjectId | FK → `products._id`, bắt buộc (index) | Sản phẩm gốc |
| `variant_name` | String | Bắt buộc | Tên biến thể (VD: "Tone 01 – Sáng") |
| `sku` | String | Bắt buộc, UNIQUE (index) | Mã hàng riêng của biến thể |
| `additional_price` | Number | Bắt buộc | Phụ phí so với giá gốc của sản phẩm |
| `stock` | Number | Bắt buộc, ≥ 0 | Tồn kho riêng của biến thể |
| `image_url` | String | Bắt buộc | Ảnh minh hoạ biến thể |

### 2.5. `productreviews` — Đánh giá sản phẩm

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK | Định danh đánh giá |
| `user_id` | ObjectId | FK → `users._id`, bắt buộc (index) | Người đánh giá |
| `product_id` | ObjectId | FK → `products._id`, bắt buộc (index) | Sản phẩm được đánh giá |
| `order_id` | ObjectId | FK → `orders._id`, bắt buộc | Đơn hàng làm căn cứ "đã mua" |
| `rating` | Number | Bắt buộc, khoảng giá trị 1–5 | Số sao đánh giá |
| `comment` | String | Bắt buộc | Nội dung nhận xét |
| `images` | Array\<String\> | Không bắt buộc | Ảnh đính kèm đánh giá |
| `is_verified_purchase` | Boolean | Bắt buộc | Đã xác minh mua hàng thật hay chưa |
| `created_at` | Date | Bắt buộc | Thời điểm đánh giá |

---

## 3. Nhóm Giỏ hàng & Đơn hàng

### 3.1. `carts` — Giỏ hàng

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK | Định danh giỏ hàng |
| `user_id` | ObjectId | FK → `users._id`, có thể null (index) | Chủ giỏ hàng nếu đã đăng nhập |
| `session_id` | ObjectId | FK → `guestsessions._id`, có thể null | Phiên khách nếu chưa đăng nhập (loại trừ lẫn nhau với `user_id`) |
| `created_at` | Date | Bắt buộc | Thời điểm tạo giỏ |
| `updated_at` | Date | Bắt buộc | Thời điểm cập nhật gần nhất |

### 3.2. `cartitems` — Dòng sản phẩm trong giỏ

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK | Định danh dòng giỏ hàng |
| `cart_id` | ObjectId | FK → `carts._id`, bắt buộc (index) | Giỏ hàng chứa dòng này |
| `product_id` | ObjectId | FK → `products._id`, bắt buộc | Sản phẩm được chọn |
| `variant_id` | ObjectId | FK → `productvariants._id`, bắt buộc | Biến thể được chọn |
| `quantity` | Number | Bắt buộc, > 0 | Số lượng |
| `unit_price` | Number | Bắt buộc, ≥ 0 | Đơn giá tại thời điểm thêm vào giỏ |

### 3.3. `orders` — Đơn hàng

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK | Định danh đơn hàng |
| `order_number` | String | Bắt buộc, UNIQUE (index) | Mã đơn hiển thị cho khách (VD: `PP-ORD-001`) |
| `user_id` | ObjectId | FK → `users._id`, có thể null (index) | Khách đặt hàng nếu đã đăng nhập |
| `session_id` | ObjectId | FK → `guestsessions._id`, có thể null | Phiên khách nếu đặt hàng không đăng nhập |
| `address_id` | ObjectId | FK → `useraddresses._id`, có thể null | Địa chỉ giao hàng |
| `total_amount` | Number | Bắt buộc, ≥ 0 | Tổng tiền hàng trước giảm giá/phí ship |
| `shipping_fee` | Number | Bắt buộc, ≥ 0 | Phí vận chuyển |
| `discount_amount` | Number | Bắt buộc, ≥ 0 | Số tiền được giảm |
| `final_amount` | Number | Bắt buộc, ≥ 0 | Số tiền phải thanh toán cuối cùng |
| `status` | String | Bắt buộc (index); enum: `pending`, `paid`, `shipping`, `delivered`, `cancelled`, `returned` | Trạng thái đơn hàng |
| `payment_method` | String | Bắt buộc; enum quan sát được: `COD`, `MoMo`, `VNPay`, `VISA` | Phương thức thanh toán |
| `payment_status` | String | Bắt buộc; enum: `unpaid`, `pending`, `paid`, `failed` | Trạng thái thanh toán |
| `shipping_carrier` | String | Có thể null; enum quan sát được: `GHTK`, `GHN`, `Viettel` | Đơn vị vận chuyển |
| `tracking_number` | String | Có thể null | Mã vận đơn |
| `note` | String | Có thể null | Ghi chú của khách |
| `created_at` | Date | Bắt buộc (index) | Thời điểm tạo đơn |

### 3.4. `orderitems` — Dòng sản phẩm trong đơn hàng

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK | Định danh dòng đơn hàng |
| `order_id` | ObjectId | FK → `orders._id`, bắt buộc (index) | Đơn hàng chứa dòng này |
| `product_id` | ObjectId | FK → `products._id`, bắt buộc (index) | Sản phẩm đã mua |
| `variant_id` | ObjectId | FK → `productvariants._id`, bắt buộc | Biến thể đã mua |
| `quantity` | Number | Bắt buộc, > 0 | Số lượng đặt mua |
| `price` | Number | Bắt buộc, ≥ 0 | Đơn giá tại thời điểm đặt hàng (snapshot) |

### 3.5. `orderstatushistories` — Lịch sử trạng thái đơn hàng

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK | Định danh bản ghi lịch sử |
| `order_id` | ObjectId | FK → `orders._id`, bắt buộc (index) | Đơn hàng liên quan |
| `status` | String | Bắt buộc; enum: `pending`, `preparing`, `paid`, `shipping`, `delivered`, `cancelled`, `returned` | Trạng thái tại mốc thời gian này |
| `note` | String | Bắt buộc | Ghi chú diễn giải |
| `created_at` | Date | Bắt buộc | Thời điểm chuyển trạng thái |

### 3.6. `payments` — Giao dịch thanh toán

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK | Định danh giao dịch |
| `order_id` | ObjectId | FK → `orders._id`, bắt buộc, UNIQUE (index) — quan hệ 1-1 | Đơn hàng được thanh toán |
| `transaction_id` | String | Có thể null | Mã giao dịch từ cổng thanh toán |
| `amount` | Number | Bắt buộc, ≥ 0 | Số tiền giao dịch |
| `status` | String | Bắt buộc; enum: `pending`, `success`, `failed` | Trạng thái giao dịch |
| `payment_method` | String | Bắt buộc; enum: `COD`, `MoMo`, `VNPay`, `VISA` | Phương thức thanh toán |
| `paid_at` | Date | Có thể null | Thời điểm thanh toán thành công |

---

## 4. Nhóm Khuyến mãi, Voucher & Thành viên

### 4.1. `vouchers` — Mã giảm giá

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK | Định danh voucher |
| `code` | String | Bắt buộc, UNIQUE (index) | Mã voucher |
| `discount_type` | String | Bắt buộc; enum: `percent`, `fixed` | Kiểu giảm giá |
| `discount_value` | Number | Bắt buộc, ≥ 0 | Giá trị giảm (% hoặc số tiền tuỳ `discount_type`) |
| `min_order_amount` | Number | Bắt buộc, ≥ 0 | Giá trị đơn tối thiểu để áp dụng |
| `max_discount` | Number | Bắt buộc, ≥ 0 | Mức giảm tối đa |
| `start_date` | Date | Bắt buộc | Ngày bắt đầu hiệu lực |
| `end_date` | Date | Bắt buộc | Ngày hết hiệu lực |
| `usage_limit` | Number | Bắt buộc, ≥ 0 | Số lượt sử dụng tối đa |
| `used_count` | Number | Bắt buộc, ≥ 0 | Số lượt đã sử dụng |
| `is_active` | Boolean | Bắt buộc | Voucher còn hiệu lực phát hành hay không |

### 4.2. `uservouchers` — Voucher đã cấp cho người dùng

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK | Định danh bản ghi |
| `user_id` | ObjectId | FK → `users._id`, bắt buộc (index kết hợp `used_at`) | Người sở hữu voucher |
| `voucher_id` | ObjectId | FK → `vouchers._id`, bắt buộc | Voucher được cấp |
| `assigned_at` | Date | Bắt buộc | Thời điểm cấp voucher |
| `used_at` | Date | Có thể null (null = chưa sử dụng) | Thời điểm sử dụng |

### 4.3. `promotions` — Chương trình khuyến mãi

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK | Định danh chương trình |
| `name` | String | Bắt buộc | Tên chương trình |
| `type` | String | Bắt buộc; enum: `bogo`, `flash_sale`, `free_shipping`, `percent_discount` | Loại khuyến mãi |
| `start_date` | Date | Bắt buộc | Ngày bắt đầu |
| `end_date` | Date | Bắt buộc | Ngày kết thúc |
| `is_active` | Boolean | Bắt buộc | Đang áp dụng hay không |

### 4.4. `promotiondetails` — Chi tiết áp dụng khuyến mãi theo sản phẩm

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK | Định danh chi tiết |
| `promotion_id` | ObjectId | FK → `promotions._id`, bắt buộc | Chương trình khuyến mãi áp dụng |
| `product_id` | ObjectId | FK → `products._id`, bắt buộc | Sản phẩm cần mua (điều kiện) |
| `buy_quantity` | Number | Bắt buộc, > 0 | Số lượng cần mua để được ưu đãi |
| `get_product_id` | ObjectId | FK → `products._id`, có thể null | Sản phẩm được tặng (áp dụng cho loại `bogo`) |
| `get_quantity` | Number | Có thể null | Số lượng sản phẩm được tặng |
| `discount_percent` | Number | Có thể null | % giảm giá (áp dụng cho `percent_discount`/`flash_sale`) |
| `discount_amount` | Number | Có thể null | Số tiền giảm cố định |

### 4.5. `membershiphistories` — Lịch sử hạng thành viên & điểm

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK | Định danh bản ghi |
| `user_id` | ObjectId | FK → `users._id`, bắt buộc (index kết hợp `changed_at`) | Người dùng liên quan |
| `level` | String | Bắt buộc; enum: `Bronze Member`, `Silver Member`, `Gold Member`, `Diamond Member` | Hạng thành viên tại thời điểm ghi nhận |
| `points` | Number | Bắt buộc, ≥ 0 | Điểm tích luỹ tại thời điểm ghi nhận |
| `changed_at` | Date | Bắt buộc | Thời điểm thay đổi hạng/điểm |

### 4.6. `pointstransactions` — Giao dịch điểm thưởng

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK | Định danh giao dịch điểm |
| `user_id` | ObjectId | FK → `users._id`, bắt buộc (index) | Người dùng liên quan |
| `points_change` | Number | Bắt buộc (có thể âm/dương) | Số điểm cộng/trừ |
| `reason` | String | Bắt buộc; enum quan sát được: `earn_from_order`, `redeem_voucher`, `redeem_membership`, `bonus` | Lý do phát sinh giao dịch |
| `reference_id` | String | Có thể null | Id tham chiếu tới đối tượng gây phát sinh (VD: id đơn hàng) |
| `created_at` | Date | Bắt buộc | Thời điểm phát sinh |

---

## 5. Nhóm Cộng đồng (Community)

### 5.1. `communityposts` — Bài đăng cộng đồng

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK | Định danh bài đăng |
| `user_id` | ObjectId | FK → `users._id`, bắt buộc (index) | Tác giả |
| `content` | String | Bắt buộc | Nội dung bài đăng |
| `images` | Array\<String\> | Bắt buộc (có thể rỗng) | Danh sách ảnh đính kèm |
| `product_tag` | ObjectId | FK → `products._id`, có thể null | Sản phẩm được gắn thẻ trong bài |
| `like_count` | Number | Bắt buộc, ≥ 0 | Số lượt thích (denormalized) |
| `comment_count` | Number | Bắt buộc, ≥ 0 | Số bình luận (denormalized) |
| `share_count` | Number | Không bắt buộc, ≥ 0 | Số lượt chia sẻ (denormalized) |
| `is_hidden` | Boolean | Bắt buộc (index kết hợp `created_at`) | Bài đã bị ẩn hay chưa |
| `created_at` | Date | Bắt buộc | Thời điểm đăng |

### 5.2. `comments` — Bình luận

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK | Định danh bình luận |
| `post_id` | ObjectId | FK → `communityposts._id`, bắt buộc (index) | Bài đăng được bình luận |
| `user_id` | ObjectId | FK → `users._id`, bắt buộc (index) | Người bình luận |
| `parent_id` | ObjectId | FK → `comments._id` (tự tham chiếu), có thể null | Bình luận cha (khi là trả lời/reply) |
| `content` | String | Bắt buộc | Nội dung bình luận |
| `created_at` | Date | Bắt buộc | Thời điểm bình luận |

### 5.3. `likes` — Lượt thích

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK | Định danh lượt thích |
| `user_id` | ObjectId | FK → `users._id`, bắt buộc (index) | Người thực hiện thích |
| `target_type` | String | Bắt buộc (index kết hợp `target_id`); enum quan sát được: `post` | Loại đối tượng được thích (đa hình) |
| `target_id` | ObjectId | FK đa hình → `communityposts._id` (theo `target_type`) | Đối tượng được thích |
| `created_at` | Date | Bắt buộc | Thời điểm thích |

### 5.4. `follows` — Quan hệ theo dõi

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK | Định danh quan hệ |
| `follower_id` | ObjectId | FK → `users._id`, bắt buộc (index) | Người theo dõi |
| `followed_id` | ObjectId | FK → `users._id`, bắt buộc (index) | Người được theo dõi |
| `created_at` | Date | Bắt buộc | Thời điểm theo dõi |

### 5.5. `savedposts` — Bài đăng đã lưu

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK | Định danh bản ghi |
| `user_id` | ObjectId | FK → `users._id`, bắt buộc (index) | Người lưu bài |
| `post_id` | ObjectId | FK → `communityposts._id`, bắt buộc | Bài đăng được lưu |
| `created_at` | Date | Bắt buộc | Thời điểm lưu |

### 5.6. `hiddenposts` — Bài đăng bị người dùng ẩn (chưa có dữ liệu)

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK | Định danh bản ghi |
| `user_id` | ObjectId | FK → `users._id` (dự kiến) | Người ẩn bài |
| `post_id` | ObjectId | FK → `communityposts._id` (dự kiến) | Bài đăng bị ẩn |

> Collection hiện chưa có dữ liệu và chưa có route sử dụng — cấu trúc cột suy ra theo quy ước đặt tên tương tự `savedposts`.

### 5.7. `nearbyposts` — Story 24h theo vị trí (GPS)

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK | Định danh story |
| `user_id` | ObjectId | FK → `users._id`, bắt buộc (index) | Người đăng |
| `media_url` | String | Bắt buộc | Đường dẫn ảnh/video |
| `media_type` | String | Bắt buộc; enum: `image`, `video` | Loại media |
| `caption` | String | Không bắt buộc | Chú thích |
| `location` | GeoJSON Point | Bắt buộc, index `2dsphere` | Toạ độ đăng bài (`{type:"Point", coordinates:[lng,lat]}`), dùng để truy vấn theo bán kính |
| `created_at` | Date | Bắt buộc | Thời điểm đăng |
| `expires_at` | Date | Bắt buộc, TTL index (`expireAfterSeconds:0`) | Thời điểm hết hạn — MongoDB tự xoá bản ghi sau mốc này (24h) |

### 5.8. `reels` — Video ngắn (sưu tầm từ mạng xã hội)

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK | Định danh reel |
| `source` | String | Bắt buộc; enum: `instagram`, `tiktok`, `facebook`, `youtube` | Nền tảng gốc |
| `source_url` | String | Bắt buộc | Link gốc trên nền tảng |
| `video_url` | String | Bắt buộc | Link video (host trên Cloudinary) |
| `thumbnail_url` | String | Bắt buộc | Ảnh đại diện video |
| `caption` | String | Bắt buộc | Chú thích |
| `hashtags` | Array\<String\> | Bắt buộc | Danh sách hashtag |
| `author` | Object `{name, handle, avatar_url, verified}` | Bắt buộc | Thông tin tác giả (nhúng) |
| `product_tags` | Array\<ObjectId\> | Bắt buộc; FK → `products._id` | Danh sách sản phẩm được gắn thẻ |
| `duration` | Number | Bắt buộc, > 0 | Thời lượng video (giây) |
| `view_count` | Number | Bắt buộc, ≥ 0 | Lượt xem |
| `like_count` | Number | Bắt buộc, ≥ 0 | Lượt thích |
| `comment_count` | Number | Bắt buộc, ≥ 0 | Lượt bình luận |
| `share_count` | Number | Bắt buộc, ≥ 0 | Lượt chia sẻ |
| `is_active` | Boolean | Bắt buộc (index kết hợp `created_at`) | Còn hiển thị hay không |
| `created_at` | Date | Bắt buộc | Thời điểm tạo bản ghi |

---

## 6. Nhóm Nội dung biên tập (Blog / Chuyên gia)

### 6.1. `blogs` — Bài viết blog

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK | Định danh bài viết |
| `title` | String | Bắt buộc | Tiêu đề |
| `slug` | String | Bắt buộc, UNIQUE (index) | Định danh dạng URL |
| `cover_image` | String | Bắt buộc | Ảnh bìa |
| `excerpt` | String | Bắt buộc | Tóm tắt ngắn |
| `content` | String (HTML) | Bắt buộc | Nội dung đầy đủ |
| `author` | Object `{name, avatar_url, role}` | Bắt buộc | Thông tin tác giả (nhúng) |
| `category` | String | Bắt buộc | Chuyên mục |
| `tags` | Array\<String\> | Bắt buộc | Thẻ gắn bài viết |
| `read_time` | Number | Bắt buộc, > 0 | Thời gian đọc ước tính (phút) |
| `view_count` | Number | Bắt buộc, ≥ 0 | Lượt xem |
| `like_count` | Number | Bắt buộc, ≥ 0 | Lượt thích |
| `is_published` | Boolean | Bắt buộc (index kết hợp `published_at`) | Đã xuất bản hay chưa |
| `published_at` | Date | Bắt buộc | Thời điểm xuất bản |

### 6.2. `experts` — Chuyên gia/Bác sĩ tư vấn

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK | Định danh chuyên gia |
| `name` | String | Bắt buộc | Họ tên |
| `title` | String | Bắt buộc | Chức danh (VD: "Bác sĩ Da liễu") |
| `specialty` | String | Bắt buộc | Chuyên môn |
| `avatar_url` | String | Bắt buộc | Ảnh đại diện |
| `credentials` | String | Bắt buộc | Bằng cấp/chứng chỉ |
| `bio` | String | Bắt buộc | Giới thiệu |
| `years_experience` | Number | Bắt buộc, ≥ 0 | Số năm kinh nghiệm |
| `rating` | Number | Bắt buộc, khoảng 0–5 | Điểm đánh giá trung bình |
| `consultation_count` | Number | Bắt buộc, ≥ 0 | Số lượt đã tư vấn |
| `contact` | Object `{phone, zalo, messenger, email}` | Bắt buộc | Thông tin liên hệ (nhúng) |
| `is_available` | Boolean | Bắt buộc | Đang nhận tư vấn hay không |
| `created_at` | Date | Bắt buộc | Thời điểm tạo hồ sơ |

### 6.3. `expertarticles` — Bài viết của chuyên gia

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK | Định danh bài viết |
| `expert_id` | ObjectId | FK → `experts._id`, bắt buộc | Chuyên gia biên soạn |
| `title` | String | Bắt buộc | Tiêu đề |
| `cover_image` | String | Bắt buộc | Ảnh bìa |
| `excerpt` | String | Bắt buộc | Tóm tắt |
| `content` | String (HTML) | Bắt buộc | Nội dung đầy đủ |
| `category` | String | Bắt buộc | Chuyên mục |
| `tags` | Array\<String\> | Bắt buộc | Thẻ gắn bài viết |
| `product_tags` | Array\<Object\> | Không bắt buộc | Sản phẩm liên quan được gắn kèm bài viết |
| `read_time` | Number | Bắt buộc, > 0 | Thời gian đọc ước tính (phút) |
| `view_count` | Number | Bắt buộc, ≥ 0 | Lượt xem |
| `like_count` | Number | Bắt buộc, ≥ 0 | Lượt thích |
| `is_published` | Boolean | Bắt buộc (index kết hợp `published_at`) | Đã xuất bản hay chưa |
| `published_at` | Date | Bắt buộc | Thời điểm xuất bản |

### 6.4. `consultationrequests` — Yêu cầu tư vấn từ khách hàng

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK | Định danh yêu cầu |
| `user_id` | ObjectId | FK → `users._id`, bắt buộc (index) | Người gửi yêu cầu |
| `expert_id` | ObjectId | FK → `experts._id`, bắt buộc | Chuyên gia được yêu cầu tư vấn |
| `source_article_id` | ObjectId | FK → `expertarticles._id`, có thể null | Bài viết là nguồn dẫn tới yêu cầu (nếu có) |
| `name` | String | Bắt buộc | Tên người yêu cầu |
| `phone` | String | Bắt buộc | SĐT liên hệ |
| `email` | String | Bắt buộc | Email liên hệ |
| `skin_type` | String | Không bắt buộc; enum: `oily`, `dry`, `normal`, `combination` | Loại da |
| `topic` | String | Bắt buộc | Chủ đề cần tư vấn |
| `message` | String | Bắt buộc | Nội dung yêu cầu |
| `preferred_channel` | String | Bắt buộc; enum: `phone`, `zalo`, `messenger`, `email` | Kênh liên hệ mong muốn |
| `status` | String | Bắt buộc (index kết hợp `created_at`); enum quan sát được: `pending`, `contacted` | Trạng thái xử lý yêu cầu |
| `created_at` | Date | Bắt buộc | Thời điểm gửi yêu cầu |

---

## 7. Nhóm Trang chủ & Bố cục hiển thị

### 7.1. `banners` — Banner quảng cáo trang chủ

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK | Định danh banner |
| `title` | String | Bắt buộc | Tiêu đề banner |
| `image_url` | String | Bắt buộc | Ảnh banner |
| `target_type` | String | Bắt buộc; enum: `product`, `category`, `url` | Loại đích khi bấm vào banner |
| `target_id` | String | Có thể null | Id đối tượng đích (theo `target_type`, dùng khi không phải `url`) |
| `target_url` | String | Có thể null | Đường dẫn đích (dùng khi `target_type = url`) |
| `sort_order` | Number | Bắt buộc | Thứ tự hiển thị |
| `is_active` | Boolean | Bắt buộc | Đang hiển thị hay không |

### 7.2. `homesections` — Cấu hình các khối hiển thị trên trang chủ

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK | Định danh khối |
| `name` | String | Bắt buộc | Tên khối (hiển thị cho admin) |
| `content_type` | String | Bắt buộc; enum: `banner`, `products`, `collections`, `community` | Loại nội dung của khối |
| `sort_order` | Number | Bắt buộc | Thứ tự khối trên trang chủ |
| `is_active` | Boolean | Bắt buộc | Đang hiển thị hay không |
| `created_at` | Date | Bắt buộc | Thời điểm tạo |
| `updated_at` | Date | Bắt buộc | Thời điểm cập nhật gần nhất |

### 7.3. `quicklinks` — Liên kết nhanh (icon menu trang chủ)

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK | Định danh liên kết |
| `name` | String | Bắt buộc | Tên hiển thị |
| `icon` | String | Bắt buộc | Tên icon |
| `path` | String | Bắt buộc | Đường dẫn điều hướng trong app |
| `sort_order` | Number | Bắt buộc | Thứ tự hiển thị |
| `is_active` | Boolean | Bắt buộc | Đang hiển thị hay không |
| `created_at` | Date | Bắt buộc | Thời điểm tạo |
| `updated_at` | Date | Bắt buộc | Thời điểm cập nhật gần nhất |

### 7.4. `webcollections` — Bộ sưu tập sản phẩm theo chủ đề

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK | Định danh bộ sưu tập |
| `name` | String | Bắt buộc | Tên bộ sưu tập |
| `image_url` | String | Bắt buộc | Ảnh đại diện |
| `product_ids` | Array\<ObjectId\> | Bắt buộc; FK → `products._id` | Danh sách sản phẩm thuộc bộ sưu tập (N-N) |
| `sort_order` | Number | Bắt buộc | Thứ tự hiển thị |
| `is_active` | Boolean | Bắt buộc | Đang hiển thị hay không |
| `created_at` | Date | Bắt buộc | Thời điểm tạo |
| `updated_at` | Date | Bắt buộc | Thời điểm cập nhật gần nhất |

---

## 8. Nhóm Tương tác cá nhân hoá & Thông báo

### 8.1. `notifications` — Thông báo

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK | Định danh thông báo |
| `user_id` | ObjectId | FK → `users._id`, bắt buộc (index kết hợp `is_read`) | Người nhận thông báo |
| `type` | String | Bắt buộc; enum quan sát được: `order`, `promotion`, `voucher`, `like`, `comment`, `follow`, `community`, `review`, `system`, `ai`, `ai_result` | Loại thông báo |
| `title` | String | Không bắt buộc | Tiêu đề thông báo |
| `message` | String | Bắt buộc | Nội dung thông báo |
| `image_url` | String | Có thể null | Ảnh minh hoạ |
| `action_url` | String | Không bắt buộc | Đường dẫn điều hướng khi bấm vào |
| `is_read` | Boolean | Bắt buộc (index) | Đã đọc hay chưa |
| `reference_id` | String | Có thể null | Id đối tượng liên quan (đơn hàng, bài viết...) |
| `actor_id` | ObjectId | FK → `users._id`, không bắt buộc | Người tạo ra hành động phát sinh thông báo (VD: người thích/bình luận) |
| `actor_name` | String | Không bắt buộc | Tên người thực hiện hành động (denormalized) |
| `actor_avatar` | String | Không bắt buộc | Avatar người thực hiện hành động (denormalized) |
| `post_id` | ObjectId | FK → `communityposts._id`, không bắt buộc | Bài đăng liên quan (nếu có) |
| `created_at` | Date | Bắt buộc | Thời điểm phát sinh thông báo |

### 8.2. `wishlists` — Sản phẩm yêu thích

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK | Định danh bản ghi |
| `user_id` | ObjectId | FK → `users._id`, bắt buộc (index) | Người dùng |
| `product_id` | ObjectId | FK → `products._id`, bắt buộc | Sản phẩm yêu thích |
| `created_at` | Date | Bắt buộc | Thời điểm thêm vào yêu thích |

### 8.3. `recentlyvieweds` — Sản phẩm đã xem gần đây

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK | Định danh bản ghi |
| `user_id` | ObjectId | FK → `users._id`, bắt buộc (index) | Người dùng |
| `product_id` | ObjectId | FK → `products._id`, bắt buộc | Sản phẩm đã xem |
| `viewed_at` | Date | Bắt buộc | Thời điểm xem |

### 8.4. `searchhistories` — Lịch sử tìm kiếm (chưa có dữ liệu)

| Tên cột | Kiểu dữ liệu | Ràng buộc | Ý nghĩa |
|---|---|---|---|
| `_id` | ObjectId | PK | Định danh bản ghi |
| `user_id` | ObjectId | FK → `users._id` (dự kiến) | Người tìm kiếm |
| `keyword` | String (dự kiến) | — | Từ khoá đã tìm |
| `created_at` | Date (dự kiến) | — | Thời điểm tìm kiếm |

> Collection hiện chưa có dữ liệu và chưa có route sử dụng — cấu trúc cột chỉ mang tính dự kiến theo mục đích đặt tên.

---

## 9. Nhóm dự phòng / chưa tích hợp (AI Chatbot)

Các collection sau đã được tạo index (chuẩn bị hạ tầng) nhưng **hiện chưa có dữ liệu và chưa có route/API sử dụng** trong backend hiện tại. Đây nhiều khả năng là phần mở rộng cho tính năng tư vấn AI (chatbot) trong tương lai.

| Collection | Chỉ mục đã tạo | Ghi chú |
|---|---|---|
| `aisessions` | `_id` | Phiên làm việc với AI |
| `aiclientadvisors` | `_id`, `ai_session_id` (UNIQUE) | Hồ sơ AI đóng vai "cố vấn khách hàng", gắn 1-1 với `aisessions` |
| `aidermatologists` | `_id`, `ai_session_id` (UNIQUE) | Hồ sơ AI đóng vai "bác sĩ da liễu", gắn 1-1 với `aisessions` |
| `aimakeupartists` | `_id`, `ai_session_id` (UNIQUE) | Hồ sơ AI đóng vai "chuyên viên trang điểm", gắn 1-1 với `aisessions` |
| `chatbotconversations` | `_id` | Cuộc hội thoại chatbot |
| `chatbotmessages` | `_id` | Tin nhắn trong cuộc hội thoại chatbot |

---

## 10. Các mối liên kết giữa các thực thể (Entity Relationships)

| Thực thể nguồn | Khoá ngoại | Thực thể đích | Bản chất quan hệ | Ý nghĩa |
|---|---|---|---|---|
| `useraddresses` | `user_id` | `users` | N–1 | Một người dùng có nhiều địa chỉ |
| `carts` | `user_id` | `users` | N–1 (nullable) | Giỏ hàng của người dùng đã đăng nhập |
| `carts` | `session_id` | `guestsessions` | N–1 (nullable) | Giỏ hàng của khách chưa đăng nhập |
| `cartitems` | `cart_id` | `carts` | N–1 | Một giỏ hàng có nhiều dòng sản phẩm |
| `cartitems` | `product_id` | `products` | N–1 | Sản phẩm được thêm vào giỏ |
| `cartitems` | `variant_id` | `productvariants` | N–1 | Biến thể được chọn trong giỏ |
| `orders` | `user_id` | `users` | N–1 (nullable) | Một người dùng có nhiều đơn hàng |
| `orders` | `session_id` | `guestsessions` | N–1 (nullable) | Đơn hàng đặt bởi khách chưa đăng nhập |
| `orders` | `address_id` | `useraddresses` | N–1 | Địa chỉ giao hàng của đơn |
| `orderitems` | `order_id` | `orders` | N–1 | Một đơn hàng có nhiều dòng sản phẩm |
| `orderitems` | `product_id` | `products` | N–1 | Sản phẩm trong đơn |
| `orderitems` | `variant_id` | `productvariants` | N–1 | Biến thể trong đơn |
| `orderstatushistories` | `order_id` | `orders` | N–1 | Một đơn hàng có nhiều mốc lịch sử trạng thái |
| `payments` | `order_id` | `orders` | 1–1 | Một đơn hàng có đúng một giao dịch thanh toán |
| `productreviews` | `user_id` | `users` | N–1 | Người viết đánh giá |
| `productreviews` | `product_id` | `products` | N–1 | Sản phẩm được đánh giá |
| `productreviews` | `order_id` | `orders` | N–1 | Đơn hàng làm căn cứ đã mua |
| `productimages` | `product_id` | `products` | N–1 | Một sản phẩm có nhiều ảnh |
| `productvariants` | `product_id` | `products` | N–1 | Một sản phẩm có nhiều biến thể |
| `products` | `category_id` | `categories` | N–1 | Một danh mục có nhiều sản phẩm |
| `categories` | `parent_id` | `categories` | N–1 (tự tham chiếu) | Danh mục con thuộc danh mục cha (cây phân cấp) |
| `webcollections` | `product_ids[]` | `products` | N–N | Một bộ sưu tập gồm nhiều sản phẩm |
| `promotiondetails` | `promotion_id` | `promotions` | N–1 | Một chương trình khuyến mãi có nhiều chi tiết áp dụng |
| `promotiondetails` | `product_id`, `get_product_id` | `products` | N–1 | Sản phẩm điều kiện / sản phẩm tặng kèm |
| `uservouchers` | `user_id` | `users` | N–1 | Một người dùng được cấp nhiều voucher |
| `uservouchers` | `voucher_id` | `vouchers` | N–1 | Một voucher được cấp cho nhiều người dùng |
| `membershiphistories` | `user_id` | `users` | N–1 | Lịch sử hạng thành viên của người dùng |
| `pointstransactions` | `user_id` | `users` | N–1 | Lịch sử điểm thưởng của người dùng |
| `communityposts` | `user_id` | `users` | N–1 | Tác giả bài đăng |
| `communityposts` | `product_tag` | `products` | N–1 (nullable) | Sản phẩm được gắn thẻ trong bài đăng |
| `comments` | `post_id` | `communityposts` | N–1 | Một bài đăng có nhiều bình luận |
| `comments` | `user_id` | `users` | N–1 | Người viết bình luận |
| `comments` | `parent_id` | `comments` | N–1 (tự tham chiếu) | Bình luận trả lời (reply) |
| `likes` | `user_id` | `users` | N–1 | Người thực hiện thích |
| `likes` | `target_id` (theo `target_type`) | `communityposts` (hiện tại) | N–1 (đa hình) | Đối tượng được thích |
| `follows` | `follower_id` | `users` | N–1 | Người theo dõi |
| `follows` | `followed_id` | `users` | N–1 | Người được theo dõi (N–N giữa `users` với chính nó qua bảng trung gian) |
| `savedposts` | `user_id` | `users` | N–1 | Người lưu bài |
| `savedposts` | `post_id` | `communityposts` | N–1 | Bài đăng được lưu |
| `nearbyposts` | `user_id` | `users` | N–1 | Người đăng story |
| `reels` | `product_tags[]` | `products` | N–N | Sản phẩm được gắn thẻ trong video |
| `expertarticles` | `expert_id` | `experts` | N–1 | Một chuyên gia viết nhiều bài |
| `expertarticles` | `product_tags[]` | `products` | N–N (nullable) | Sản phẩm được gắn thẻ trong bài viết |
| `consultationrequests` | `user_id` | `users` | N–1 | Người gửi yêu cầu tư vấn |
| `consultationrequests` | `expert_id` | `experts` | N–1 | Chuyên gia được yêu cầu tư vấn |
| `consultationrequests` | `source_article_id` | `expertarticles` | N–1 (nullable) | Bài viết dẫn tới yêu cầu tư vấn |
| `notifications` | `user_id` | `users` | N–1 | Người nhận thông báo |
| `notifications` | `actor_id` | `users` | N–1 (nullable) | Người gây ra hành động phát sinh thông báo |
| `notifications` | `post_id` | `communityposts` | N–1 (nullable) | Bài đăng liên quan tới thông báo |
| `wishlists` | `user_id` | `users` | N–1 | Danh sách yêu thích của người dùng |
| `wishlists` | `product_id` | `products` | N–1 | Sản phẩm được yêu thích |
| `recentlyvieweds` | `user_id` | `users` | N–1 | Lịch sử xem của người dùng |
| `recentlyvieweds` | `product_id` | `products` | N–1 | Sản phẩm đã xem |
| `banners` | `target_id`/`target_url` | `products`/`categories`/URL ngoài | N–1 (đa hình, tuỳ `target_type`) | Đích điều hướng khi bấm banner |

---

*Tài liệu được sinh dựa trên dữ liệu thực tế truy vấn trực tiếp từ MongoDB Atlas (database `pompom_db`) và mã nguồn `src/models/index.js`, `src/routes/*` tại thời điểm 2026-07-10.*
