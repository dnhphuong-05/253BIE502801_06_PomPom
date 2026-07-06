package com.pompom.group6.network;

import android.content.Context;
import android.content.SharedPreferences;

import com.pompom.group6.network.dto.ApiUser;

/**
 * Quản lý phiên đăng nhập trong SharedPreferences.
 *
 * Trong quá trình chuyển dần từ SQLite sang backend (chiến lược "strangler-fig"),
 * ta lưu SONG SONG:
 *  - "user_oid"  : id chuỗi ObjectId từ backend — dùng cho các màn đã migrate.
 *  - "user_id"   : id số cũ (SQLite) — giữ nguyên để các màn CHƯA migrate vẫn chạy.
 * Nhờ vậy app luôn build & chạy được ở mọi bước.
 */
public final class Session {
    private static final String PREFS = "user_prefs";
    private Session() {}

    private static SharedPreferences prefs(Context c) {
        return c.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    /** Lưu phiên sau khi đăng nhập/đăng ký thành công qua backend. */
    public static void save(Context c, ApiUser user) {
        prefs(c).edit()
                .putBoolean("is_logged_in", true)
                .putString("user_oid", user.id)
                .putString("user_name", user.fullName)
                .putString("user_email", user.email)
                .putString("user_avatar", user.avatarUrl)
                .apply();
    }

    public static boolean isLoggedIn(Context c) {
        return prefs(c).getBoolean("is_logged_in", false);
    }

    /** Id chuỗi (ObjectId) của backend, hoặc null nếu chưa đăng nhập qua backend. */
    public static String getUserOid(Context c) {
        return prefs(c).getString("user_oid", null);
    }

    public static String getUserName(Context c) {
        return prefs(c).getString("user_name", null);
    }

    public static String getUserAvatar(Context c) {
        return prefs(c).getString("user_avatar", null);
    }

    public static void logout(Context c) {
        prefs(c).edit()
                .remove("is_logged_in")
                .remove("user_oid")
                .remove("user_name")
                .remove("user_email")
                .remove("user_avatar")
                .apply();
    }
}
