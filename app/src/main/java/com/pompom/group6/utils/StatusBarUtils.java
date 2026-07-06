package com.pompom.group6.utils;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.pompom.group6.R;

/**
 * Centralised status-bar helper.
 *
 * <p>Makes the status bar fully transparent so each screen's own background shows
 * through it (edge-to-edge). Every Activity just calls {@link #applyTransparent(Activity)}.</p>
 *
 * <p>It deliberately does NOT touch the status-bar icon (light/dark) appearance —
 * each screen keeps whatever icon colour it already uses.</p>
 *
 * <p>For the top bar not to be hidden under the status bar, the screen's root layout
 * should declare {@code android:fitsSystemWindows="true"} (the system inset then
 * becomes padding on the root).</p>
 */
public final class StatusBarUtils {

    private StatusBarUtils() {
    }

    /**
     * Lays the content out behind a transparent status bar. Icon colour untouched.
     */
    public static void applyTransparent(Activity activity) {
        if (activity == null) {
            return;
        }
        Window window = activity.getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, false);
        // A translucent-status window ignores setStatusBarColor, so clear it first.
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    /**
     * Cho các màn chi tiết dùng header hồng cố định (include_screen_header): tô status bar
     * cùng màu hồng với header (icon trắng), thanh điều hướng hệ thống theo nền sáng của màn (icon đen).
     */
    public static void applyPinkHeader(Activity activity) {
        if (activity == null) return;
        Window window = activity.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(activity, R.color.brand_pink));
        window.setNavigationBarColor(ContextCompat.getColor(activity, R.color.md_theme_light_background));

        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(window, window.getDecorView());
        controller.setAppearanceLightStatusBars(false);
        controller.setAppearanceLightNavigationBars(true);
    }

    /**
     * Chèn padding thủ công cho các màn dùng {@code include_screen_header.xml}: đẩy header
     * (nền hồng) xuống dưới status bar mà vẫn tô màu xuyên qua vùng đó, và chừa khoảng dưới
     * để nội dung không bị thanh điều hướng hệ thống che mất.
     *
     * <p>Cần thiết vì từ Android 15 (targetSdk 35+), hệ thống LUÔN vẽ edge-to-edge — thuộc
     * tính {@code android:fitsSystemWindows} trên các View thường (không phải widget Material
     * như AppBarLayout) không còn tự động chèn padding như trước.</p>
     *
     * @param headerRoot      root view của include_screen_header (nhận padding-top)
     * @param bottomPaddedView view sẽ nhận thêm padding-bottom (thường là root layout của màn)
     */
    public static void applyHeaderContentInsets(Activity activity, View headerRoot, View bottomPaddedView) {
        if (activity == null) return;
        View decor = activity.getWindow().getDecorView();
        ViewCompat.setOnApplyWindowInsetsListener(decor, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            if (headerRoot != null) {
                headerRoot.setPadding(headerRoot.getPaddingLeft(), bars.top,
                        headerRoot.getPaddingRight(), headerRoot.getPaddingBottom());
            }
            if (bottomPaddedView != null) {
                bottomPaddedView.setPadding(bottomPaddedView.getPaddingLeft(), bottomPaddedView.getPaddingTop(),
                        bottomPaddedView.getPaddingRight(), bars.bottom);
            }
            return insets;
        });
        ViewCompat.requestApplyInsets(decor);
    }
}
