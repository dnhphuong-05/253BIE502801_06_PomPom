package com.pompom.group6.utils;

import androidx.annotation.NonNull;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.pompom.group6.MainActivity;

/** Ẩn bottom nav khi cuộn xuống, hiện lại khi cuộn lên — gắn vào view cuộn chính của mỗi tab. */
public final class BottomNavScrollHelper {

    /** Ngưỡng nhỏ để bỏ qua rung tay/cuộn lắt nhắt, tránh ẩn/hiện liên tục không mong muốn. */
    private static final int THRESHOLD_PX = 12;

    private BottomNavScrollHelper() {}

    public static void attach(RecyclerView recyclerView, Fragment fragment) {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView rv, int dx, int dy) {
                onScrollDelta(fragment, dy);
            }
        });
    }

    /** Dùng cho NestedScrollView CHƯA có OnScrollChangeListener nào khác — gọi này sẽ ghi đè,
     * nên nếu fragment đã tự set listener riêng (vd phân trang), hãy gọi {@link #onScrollDelta}
     * trực tiếp bên trong listener đó thay vì dùng overload này. */
    public static void attach(NestedScrollView scrollView, Fragment fragment) {
        scrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener)
                (v, scrollX, scrollY, oldScrollX, oldScrollY) ->
                        onScrollDelta(fragment, scrollY - oldScrollY));
    }

    /** Gọi trực tiếp khi fragment đã có sẵn scroll listener riêng (xem ShopFragment). */
    public static void onScrollDelta(Fragment fragment, int dy) {
        if (!(fragment.getActivity() instanceof MainActivity)) return;
        MainActivity activity = (MainActivity) fragment.getActivity();
        if (dy > THRESHOLD_PX) activity.hideBottomNav();
        else if (dy < -THRESHOLD_PX) activity.showBottomNav();
    }
}
