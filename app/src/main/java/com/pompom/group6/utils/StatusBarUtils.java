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
     * Cho các màn dùng header/toolbar nền trắng (vd form nhập liệu, tra cứu): tô status bar
     * trắng cùng màu header (icon tối), thanh điều hướng hệ thống theo nền sáng của màn (icon đen).
     */
    public static void applyWhiteHeader(Activity activity) {
        if (activity == null) return;
        Window window = activity.getWindow();
        window.setStatusBarColor(ContextCompat.getColor(activity, R.color.white));
        window.setNavigationBarColor(ContextCompat.getColor(activity, R.color.md_theme_light_background));

        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(window, window.getDecorView());
        controller.setAppearanceLightStatusBars(true);
        controller.setAppearanceLightNavigationBars(true);
    }

    /**
     * Cho các màn toàn màn hình nền tối (vd xem story/video full-bleed): status bar trong suốt
     * hoà vào nền tối, icon sáng để vẫn đọc được trên nền đó.
     */
    public static void applyDarkImmersive(Activity activity) {
        if (activity == null) return;
        Window window = activity.getWindow();
        WindowCompat.setDecorFitsSystemWindows(window, false);
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(Color.TRANSPARENT);
        window.setNavigationBarColor(Color.TRANSPARENT);

        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(window, window.getDecorView());
        controller.setAppearanceLightStatusBars(false);
        controller.setAppearanceLightNavigationBars(false);
    }

    /**
     * Chèn padding thủ công cho các màn dùng {@code include_screen_header.xml}: đẩy header
     * (nền hồng) xuống dưới status bar mà vẫn tô màu xuyên qua vùng đó, và chừa khoảng dưới
     * để nội dung không bị thanh điều hướng hệ thống che mất.
     *
     * <p>Chủ động bật edge-to-edge (thay vì dựa vào mặc định của từng bản Android — Android 15+
     * luôn ép edge-to-edge, các bản cũ hơn thì KHÔNG, hệ thống tự chừa padding sẵn). Nếu không
     * ép edge-to-edge ở đây, trên Android cũ padding thủ công bên dưới sẽ bị CỘNG DỒN với phần
     * hệ thống đã tự chừa sẵn, làm header/nội dung bị đẩy lệch xuống gấp đôi.</p>
     *
     * @param headerRoot      root view của include_screen_header (nhận padding-top)
     * @param bottomPaddedView view sẽ nhận thêm padding-bottom (thường là root layout của màn)
     */
    public static void applyHeaderContentInsets(Activity activity, View headerRoot, View bottomPaddedView) {
        if (activity == null) return;
        WindowCompat.setDecorFitsSystemWindows(activity.getWindow(), false);
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
