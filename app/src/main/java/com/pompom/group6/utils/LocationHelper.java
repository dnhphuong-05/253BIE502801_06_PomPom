package com.pompom.group6.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

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

    @SuppressLint("MissingPermission")
    public static void getCurrentLocation(Context context, Callback callback) {
        if (!hasPermission(context)) {
            callback.onUnavailable();
            return;
        }
        FusedLocationProviderClient client = LocationServices.getFusedLocationProviderClient(context);
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
