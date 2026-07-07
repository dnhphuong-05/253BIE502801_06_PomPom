package com.pompom.group6.utils;

import android.content.Context;
import android.widget.ImageView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.pompom.group6.R;

import java.util.Arrays;
import java.util.List;

/**
 * Giải mã & hiển thị ảnh đại diện + khung avatar cho màn Me.
 *
 * `avatar_url` lưu ở DB có thể là:
 *  - URL thật (http...) khi user tải ảnh từ gallery/camera (Cloudinary) → render mọi nơi.
 *  - token "default:N" khi user chọn avatar mặc định của app → render bằng drawable cục bộ.
 * `avatar_frame` lưu token khung ("classic"/"gold"/"pink"/"mint"/"none"); null = khung cổ điển.
 */
public final class AvatarUtils {
    private AvatarUtils() {}

    public static final String DEFAULT_PREFIX = "default:";

    /** Bộ avatar mặc định của app (thứ tự cố định để token "default:N" ổn định). */
    @DrawableRes
    public static final int[] DEFAULT_AVATARS = {
            R.drawable.avatar_default_1,
            R.drawable.avatar_default_2,
            R.drawable.avatar_default_3,
            R.drawable.avatar_default_4,
            R.drawable.avatar_default_5,
            R.drawable.avatar_default_6,
    };

    /** Token khung theo thứ tự hiển thị (kèm "none" = không khung). */
    public static List<String> frameTokens() {
        return Arrays.asList("classic", "gold", "pink", "mint", "none");
    }

    public static boolean isDefaultAvatar(@Nullable String avatar) {
        return avatar != null && avatar.startsWith(DEFAULT_PREFIX);
    }

    public static boolean isUrl(@Nullable String avatar) {
        return avatar != null && avatar.startsWith("http");
    }

    /** token "default:N" -> chỉ số trong DEFAULT_AVATARS, hoặc -1 nếu không hợp lệ. */
    public static int defaultIndex(@Nullable String avatar) {
        if (!isDefaultAvatar(avatar)) return -1;
        try {
            int i = Integer.parseInt(avatar.substring(DEFAULT_PREFIX.length()));
            return (i >= 0 && i < DEFAULT_AVATARS.length) ? i : -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static String avatarToken(int index) {
        return DEFAULT_PREFIX + index;
    }

    /** Nạp ảnh đại diện vào ImageView theo giá trị lưu ở DB. */
    public static void loadAvatar(Context ctx, @Nullable String avatar, ImageView iv) {
        int di = defaultIndex(avatar);
        if (di >= 0) {
            iv.setImageResource(DEFAULT_AVATARS[di]);
        } else if (isUrl(avatar)) {
            Glide.with(ctx).load(avatar).placeholder(R.drawable.ic_avatar)
                    .error(R.drawable.ic_avatar).into(iv);
        } else {
            iv.setImageResource(R.drawable.ic_avatar);
        }
    }

    /** Drawable của khung theo token; 0 = không khung. */
    @DrawableRes
    public static int frameRes(@Nullable String token) {
        if (token == null || token.isEmpty() || token.equals("classic")) return R.drawable.khung_avatar;
        switch (token) {
            case "gold": return R.drawable.frame_gold;
            case "pink": return R.drawable.frame_pink;
            case "mint": return R.drawable.frame_mint;
            case "none": return 0;
            default: return R.drawable.khung_avatar;
        }
    }

    /** Áp khung lên ImageView (ẩn nếu "none"). */
    public static void applyFrame(ImageView iv, @Nullable String token) {
        int res = frameRes(token);
        if (res == 0) {
            iv.setImageDrawable(null);
        } else {
            iv.setImageResource(res);
        }
    }
}
