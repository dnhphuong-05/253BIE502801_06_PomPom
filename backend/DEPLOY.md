# Deploy backend PomPom (để app dùng thật ngoài mạng LAN)

Hiện app gọi backend ở `http://127.0.0.1:3000` qua cáp USB (`adb reverse`). Muốn app chạy ở
bất kỳ đâu (nhiều máy/nhiều người, không cần cắm cáp), cần deploy backend lên một dịch vụ
có HTTPS công khai. Dưới đây là 2 cách miễn phí.

## Trước khi deploy — bảo mật MongoDB Atlas
1. **Đổi mật khẩu DB** (mật khẩu cũ đã lộ khi dán vào chat). Atlas → Database Access → Edit user.
2. **Network Access**: thêm `0.0.0.0/0` (cho phép mọi IP) để host cloud kết nối được.
   (Chặt hơn thì thêm dải IP của Render/Railway, nhưng `0.0.0.0/0` là đủ cho đồ án.)
3. Cập nhật lại `MONGODB_URI` với mật khẩu mới (dùng khi đặt biến môi trường bên dưới).

## Cách A — Render.com (khuyến nghị, đã có sẵn `render.yaml`)
1. Push repo này lên GitHub.
2. Vào https://dashboard.render.com → **New +** → **Blueprint** → chọn repo.
   Render đọc `render.yaml` ở gốc repo (đã cấu hình `rootDir: backend`, health check `/api/health`).
3. Khi được hỏi, dán **MONGODB_URI** (chuỗi kết nối Atlas với mật khẩu MỚI).
4. Deploy xong sẽ có URL dạng `https://pompom-backend.onrender.com`.
5. Kiểm tra: mở `https://pompom-backend.onrender.com/api/health` → thấy `{"ok":true,...}`.

> Lưu ý gói free của Render "ngủ" sau 15 phút không dùng → request đầu tiên chậm ~30s.

## Cách B — Railway.app
1. https://railway.app → New Project → Deploy from GitHub repo.
2. Set **Root Directory** = `backend` (Settings → Root Directory).
3. Thêm biến môi trường `MONGODB_URI`.
4. Railway tự chạy `npm start` (có `Procfile`). Lấy URL public ở tab Settings → Networking → Generate Domain.

## Sau khi deploy — trỏ app tới URL mới
Sửa `app/src/main/java/com/pompom/group6/network/ApiConfig.java`:

```java
public static final String BASE_URL = "https://pompom-backend.onrender.com/"; // nhớ dấu "/" cuối
```

Rồi build lại app (`gradlew installDebug`). Từ giờ app chạy ở **bất kỳ máy nào có mạng**,
không cần `adb reverse` hay cùng WiFi nữa. Vì là HTTPS nên không cần `network_security_config`
(có thể giữ nguyên, không ảnh hưởng).

## Kiểm thử nhanh sau deploy
```bash
curl https://<domain>/api/health
curl https://<domain>/api/products
```
