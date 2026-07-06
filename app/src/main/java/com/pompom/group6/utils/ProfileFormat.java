package com.pompom.group6.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Định dạng dữ liệu hồ sơ cho màn Profile.
 *
 * Nguyên tắc: KHÔNG bịa dữ liệu. Nếu giá trị từ DB trống/null thì trả về null để
 * lớp UI hiển thị gợi ý "Thêm thông tin" (empty state), không thay bằng nội dung mẫu.
 */
public final class ProfileFormat {
    private ProfileFormat() {}

    /** Gợi ý hiển thị khi một trường hồ sơ còn trống. */
    public static final String HINT_ADD = "Thêm thông tin";

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    /** Ánh xạ mã loại da trong DB sang nhãn tiếng Việt; null nếu chưa có dữ liệu. */
    public static String skinTypeLabel(String code) {
        if (isBlank(code)) return null;
        switch (code.trim().toLowerCase(Locale.ROOT)) {
            case "oily": return "Da dầu";
            case "dry": return "Da khô";
            case "normal": return "Da thường";
            case "combination": return "Da hỗn hợp";
            case "sensitive": return "Da nhạy cảm";
            default: return code; // giữ nguyên nếu là giá trị người dùng tự nhập
        }
    }

    /** Trả về giá trị nếu có, ngược lại null (để UI hiện empty state). */
    public static String orNull(String value) {
        return isBlank(value) ? null : value.trim();
    }

    /** Định dạng ngày sinh ISO (vd "1995-05-09T17:00:00.000Z") -> "dd/MM/yyyy"; null nếu trống. */
    public static String birthDate(String iso) {
        if (isBlank(iso)) return null;
        String[] patterns = {
                "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
                "yyyy-MM-dd'T'HH:mm:ss'Z'",
                "yyyy-MM-dd'T'HH:mm:ss",
                "yyyy-MM-dd"
        };
        for (String p : patterns) {
            try {
                SimpleDateFormat in = new SimpleDateFormat(p, Locale.US);
                Date d = in.parse(iso);
                if (d != null) {
                    return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(d);
                }
            } catch (ParseException ignored) {
                // thử pattern tiếp theo
            }
        }
        return null;
    }
}
