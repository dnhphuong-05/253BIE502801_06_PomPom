package com.pompom.group6.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationTokenSource;

/** Lấy toạ độ GPS hiện tại của thiết bị — dùng cho story theo bán kính. */
public final class LocationHelper {
    private LocationHelper() {}

    public interface Callback {
        void onLocation(double lat, double lng);
        void onUnavailable();
    }

    public static boolean hasPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    /** getLastLocation() chỉ trả về vị trí đã CACHE sẵn — nếu chưa app nào xin vị trí gần đây
     * (vừa bật GPS, vừa cấp quyền lần đầu...) nó trả về null dù GPS đang bật thật, khiến app báo
     * nhầm "vui lòng bật GPS". getCurrentLocation() chủ động xin một vị trí mới; chỉ khi nó cũng
     * thất bại mới rơi về getLastLocation() làm phương án dự phòng cuối.
     * Dùng BALANCED_POWER_ACCURACY (định vị mạng+GPS) để có kết quả nhanh, kể cả trong nhà —
     * HIGH_ACCURACY (GPS thuần) có thể mất rất lâu hoặc timeout khi không thấy trời. */
    public static void getCurrentLocation(Context context, Callback callback) {
        getCurrentLocation(context, Priority.PRIORITY_BALANCED_POWER_ACCURACY, callback);
    }

    /** Bản có độ chính xác cao hơn (chậm hơn) — dùng cho việc hiển thị tên khu vực cụ thể,
     * không dùng cho các luồng cần phản hồi nhanh như đăng story. */
    @SuppressLint("MissingPermission")
    public static void getCurrentLocation(Context context, int priority, Callback callback) {
        if (!hasPermission(context)) {
            callback.onUnavailable();
            return;
        }
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(context);
        client.getCurrentLocation(priority, new CancellationTokenSource().getToken())
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        callback.onLocation(location.getLatitude(), location.getLongitude());
                    } else {
                        fallbackToLastLocation(client, callback);
                    }
                })
                .addOnFailureListener(e -> fallbackToLastLocation(client, callback));
    }

    @SuppressLint("MissingPermission")
    private static void fallbackToLastLocation(FusedLocationProviderClient client, Callback callback) {
        client.getLastLocation()
                .addOnSuccessListener(location -> {
                    if (location != null) {
                        callback.onLocation(location.getLatitude(), location.getLongitude());
                    } else {
                        callback.onUnavailable();
                    }
                })
                .addOnFailureListener(e -> callback.onUnavailable());
    }
}
