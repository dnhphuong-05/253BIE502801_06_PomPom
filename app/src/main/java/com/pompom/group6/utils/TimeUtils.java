package com.pompom.group6.utils;

import java.time.Instant;
import java.util.concurrent.TimeUnit;

/** Đổi timestamp ISO thật từ server thành dạng "x phút/giờ/ngày trước" dùng chung nhiều màn. */
public final class TimeUtils {
    private TimeUtils() {}

    public static String relativeTime(String iso) {
        if (iso == null || iso.isEmpty()) return "";
        try {
            Instant then = Instant.parse(iso);
            long minutes = TimeUnit.MILLISECONDS.toMinutes(
                    Instant.now().toEpochMilli() - then.toEpochMilli());
            if (minutes < 1) return "Vừa xong";
            if (minutes < 60) return minutes + " phút trước";
            long hours = minutes / 60;
            if (hours < 24) return hours + " giờ trước";
            long days = hours / 24;
            if (days < 7) return days + " ngày trước";
            return then.toString().substring(0, 10);
        } catch (Exception e) {
            return "";
        }
    }
}
