package com.pompom.group6.network;

/**
 * Cấu hình endpoint của backend PomPom.
 *
 * BASE_URL:
 *  - Máy thật qua cáp USB (KHUYẾN NGHỊ): http://127.0.0.1:3000/  +  chạy `adb reverse tcp:3000 tcp:3000`
 *  - Máy thật cùng WiFi:                 http://10.102.37.150:3000/  (IP LAN của máy chạy backend)
 *  - Android emulator:                   http://10.0.2.2:3000/
 *  - Khi deploy (Render/Railway):        https://<domain>/
 *
 * Nhớ có dấu "/" ở cuối.
 */



public final class ApiConfig {
    private ApiConfig() {}

    // URL prod (Render) — dùng khi commit/push để leader redeploy.
    public static final String BASE_URL = "https://pompom-backend-pha3.onrender.com/";

    // ⚠️ Test LOCAL trên máy thật: chạy `adb reverse tcp:3000 tcp:3000`.
    //    NHỚ đổi lại URL Render ở trên trước khi commit/push!
    //    - Emulator thì dùng: "http://10.0.2.2:3000/"
    // public static final String BASE_URL = "http://127.0.0.1:3000/";
}
