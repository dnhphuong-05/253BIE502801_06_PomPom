# PomPom

Ứng dụng Android cho thương hiệu mỹ phẩm PomPom: mua sắm (Shop), cộng đồng làm đẹp (Community), trợ lý AI (AI Beauty Hub), và hồ sơ cá nhân (Me). Gồm 2 phần trong repo này:

- **`app/`** — ứng dụng Android (Java, native).
- **`backend/`** — API trung gian (Node.js/Express) giữa app và MongoDB Atlas.

## Kiến trúc tổng quan

```
Android app (Java) ──HTTP/Retrofit──> backend (Express) ──Mongoose──> MongoDB Atlas
```

App **không** kết nối thẳng tới MongoDB — mọi dữ liệu (sản phẩm, đơn hàng, bài viết cộng đồng, người dùng...) đi qua các endpoint REST ở `backend/`, để chuỗi kết nối Atlas không bị lộ trong app. Xem chi tiết endpoint tại [`backend/README.md`](backend/README.md).

## Tech stack

**Android app**
- Ngôn ngữ: Java (không dùng Kotlin — xem `PROJECT_RULES.md`)
- UI: Material Design 3, ViewBinding, Fragment-based navigation (`ViewPager2` + bottom nav)
- Mạng: Retrofit2 + Gson (gọi backend), Glide (tải ảnh)
- Khác: CameraX + AR Try-on, Media3 ExoPlayer (Reels), Google Generative AI SDK (Gemini, cho các tính năng AI), Play Services Location (story theo bán kính GPS), Cloudinary (lưu trữ ảnh)
- `minSdk 24`, `targetSdk/compileSdk 36`

**Backend**
- Node.js + Express + Mongoose (MongoDB Atlas, schema `strict:false`)
- Deploy: Render (`render.yaml`), health check tại `/api/health`

## Cấu trúc thư mục

```
app/src/main/java/com/pompom/group6/
├── activities/     Màn hình dạng Activity (chi tiết sản phẩm, checkout, đăng nhập, AI...)
├── fragments/       4 tab chính trong bottom nav: Home, Shop, Community, AiHub, Me
├── adapters/        RecyclerView adapters (1 adapter riêng cho mỗi loại danh sách)
├── models/           Data model dùng trong UI
├── network/          Retrofit client, DTO, Session (trạng thái đăng nhập)
├── database/         DAO cho dữ liệu cục bộ (giỏ hàng, cache...)
├── config/            Cấu hình Cloudinary
├── utils/              Helper dùng chung (CartManager, AvatarUtils, LocationHelper...)
└── widgets/           Custom View

backend/src/
├── server.js         Khởi tạo Express, mount routes, health check
├── config/db.js       Kết nối MongoDB Atlas
├── models/             Mongoose models (loose schema, trỏ đúng collection thật)
├── routes/              auth, users, products, catalog, carts, orders, community, blogs, experts, nearbyPosts
└── serialize.js         Đổi ObjectId → chuỗi `id` cho app dễ dùng
```

Quy ước cấu trúc/coding style chi tiết cho phần Android xem tại [`PROJECT_RULES.md`](PROJECT_RULES.md).

## Chạy dự án

### Backend (chạy trước, app cần backend để có dữ liệu)

```bash
cd backend
npm install
cp .env.example .env      # điền MONGODB_URI thật
npm start                 # http://localhost:3000
```

Xem đầy đủ hướng dẫn kết nối từ emulator/máy thật và danh sách endpoint tại [`backend/README.md`](backend/README.md).

### Android app

1. Mở thư mục gốc repo bằng Android Studio.
2. Kiểm tra `app/src/main/java/com/pompom/group6/network/ApiConfig.java` — `BASE_URL` đang trỏ tới backend production (Render). Để test với backend chạy local, đổi tạm sang `http://127.0.0.1:3000/` (máy thật qua USB, cần `adb reverse tcp:3000 tcp:3000`) hoặc `http://10.0.2.2:3000/` (emulator). **Nhớ đổi lại URL Render trước khi commit/push.**
3. Build & chạy như app Android bình thường (`./gradlew assembleDebug` hoặc Run trong Android Studio).

## Ghi chú

- Không hardcode màu/font trong code — luôn dùng `res/values/colors.xml` và `res/font/` (xem `PROJECT_RULES.md`).
- Mỗi RecyclerView có Adapter + ViewHolder riêng, không tái sử dụng chéo giữa các loại dữ liệu khác nhau.
- `backend/.env` chứa chuỗi kết nối MongoDB thật — không commit (đã có trong `.gitignore`).
