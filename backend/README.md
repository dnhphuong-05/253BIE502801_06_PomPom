# PomPom Backend (Node/Express + MongoDB Atlas)

Lớp API trung gian giữa app Android và MongoDB Atlas. App **không** nối thẳng vào Mongo — mọi truy cập đi qua các endpoint ở đây (backend giữ chuỗi kết nối bí mật).

## Chạy local

```bash
cd backend
npm install
cp .env.example .env      # điền MONGODB_URI thật (đã có sẵn trong .env, KHÔNG commit)
npm start                 # http://localhost:3000
```

- Kiểm tra: `GET http://localhost:3000/api/health`
- Từ **Android emulator**, gọi backend qua `http://10.0.2.2:3000` (10.0.2.2 = localhost của máy host).
- Từ **điện thoại thật cùng WiFi**, dùng IP LAN của máy chạy backend (vd `http://192.168.1.x:3000`).
- Để "dùng chung nhiều máy" thật sự → deploy lên Render/Railway rồi đổi base URL trong app.

## Kiến trúc

- `src/server.js` — khởi tạo Express, mount routes, health check.
- `src/config/db.js` — kết nối Atlas. Lưu ý: `strictQuery=false` (bắt buộc, vì models dùng loose schema).
- `src/models/index.js` — models Mongoose loose (`strict:false`) trỏ đúng tên collection thật.
- `src/serialize.js` — đổi `_id`/ObjectId → chuỗi `id` cho app dễ dùng.
- `src/routes/*` — auth, users, products, orders, carts, community, catalog.

> **Id:** MongoDB dùng ObjectId (chuỗi 24 hex). API trả về field `id` dạng chuỗi. Khi tích hợp app Android, các id phải là `String` (không phải `int`).

## Danh sách endpoint

### Auth
| Method | Path | Mô tả |
|---|---|---|
| POST | `/api/auth/register` | `{full_name,email,password,phone_number}` → tạo user (bcrypt) |
| POST | `/api/auth/login` | `{email,password}` → user, hoặc 401 |
| GET | `/api/auth/email-exists?email=` | `{exists}` |

### Users
| Method | Path | Mô tả |
|---|---|---|
| GET | `/api/users/:id` | hồ sơ (kèm membership, points, voucher_count) |
| PUT | `/api/users/:id` | cập nhật full_name/phone/bio/gender/birth_date/skin_type |
| POST | `/api/users/:id/change-password` | `{old_password,new_password}` |
| GET/POST | `/api/users/:id/addresses` | xem / thêm địa chỉ |
| PUT | `/api/users/:id/addresses/:addrId/default` | đặt mặc định |
| DELETE | `/api/users/:id/addresses/:addrId` | xoá địa chỉ |
| GET | `/api/users/:id/vouchers?onlyAvailable=true` | voucher của user |
| GET | `/api/users/:id/wishlist` | sản phẩm yêu thích |
| POST | `/api/users/:id/wishlist` | `{product_id}` (idempotent) |
| DELETE | `/api/users/:id/wishlist/:productId` | bỏ yêu thích |
| GET | `/api/users/:id/points` | lịch sử điểm |

### Products / Catalog
| Method | Path | Mô tả |
|---|---|---|
| GET | `/api/products?category_id=&q=` | danh sách (kèm thumbnail) |
| GET | `/api/products/:id` | chi tiết + images + variants + review summary |
| GET | `/api/products/:id/reviews` | đánh giá |
| GET | `/api/categories` | danh mục |
| GET | `/api/banners` | banner |
| GET | `/api/vouchers` | voucher đang hoạt động |
| GET | `/api/promotions` | khuyến mãi + sản phẩm chi tiết |
| GET | `/api/notifications?user_id=` | thông báo |

### Cart
| Method | Path | Mô tả |
|---|---|---|
| GET | `/api/carts?user_id=` | giỏ + items + total |
| POST | `/api/carts/items` | `{user_id,product_id,variant_id?,quantity}` thêm/cộng dồn |
| PUT | `/api/carts/items/:itemId` | `{quantity}` (0 → xoá) |
| DELETE | `/api/carts/items/:itemId` | xoá dòng |

### Orders
| Method | Path | Mô tả |
|---|---|---|
| GET | `/api/orders?user_id=&status=` | danh sách đơn |
| GET | `/api/orders/counts?user_id=` | đếm theo trạng thái |
| GET | `/api/orders/:id` | chi tiết + line items |
| POST | `/api/orders` | tạo đơn (`from_cart:true` lấy từ giỏ & xoá giỏ, hoặc truyền `items:[]`) |

### Community
| Method | Path | Mô tả |
|---|---|---|
| GET | `/api/community/posts` | bài viết + tác giả |
| GET | `/api/community/posts/:id` | 1 bài |
| GET | `/api/community/posts/:id/comments` | bình luận |

## Test nhanh

`node test-writes.js` (khi server đang chạy) — chạy end-to-end mọi endpoint ghi rồi tự dọn dữ liệu test theo đúng id user vừa tạo.
