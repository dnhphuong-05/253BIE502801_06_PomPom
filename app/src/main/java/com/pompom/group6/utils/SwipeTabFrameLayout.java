package com.pompom.group6.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

/**
 * FrameLayout phát hiện vuốt ngang (trái/phải) để chuyển tab — dùng cho màn Community
 * (Thước phim/Blog/Tips/Tin gần đây). Không ảnh hưởng cuộn dọc của RecyclerView con vì chỉ
 * chiếm luồng sự kiện khi độ dịch chuyển ngang rõ ràng lớn hơn dọc.
 */
public class SwipeTabFrameLayout extends FrameLayout {

    public interface OnSwipeListener {
        void onSwipeLeft();
        void onSwipeRight();
    }

    private float downX, downY;
    private boolean dragging;
    private boolean excludedGesture;
    private final float slop;
    private OnSwipeListener listener;
    private View excludedView;

    public SwipeTabFrameLayout(Context context) {
        this(context, null);
    }

    public SwipeTabFrameLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        slop = 24 * getResources().getDisplayMetrics().density;
    }

    public void setOnSwipeListener(OnSwipeListener l) {
        this.listener = l;
    }

    /** View mà nếu chạm bắt đầu bên trong nó (vd: hàng story cuộn ngang) thì bỏ qua vuốt-chuyển-tab. */
    public void setExcludedView(View v) {
        this.excludedView = v;
    }

    private boolean startedInExcludedView(MotionEvent ev) {
        if (excludedView == null || excludedView.getVisibility() != VISIBLE) return false;
        int[] loc = new int[2];
        excludedView.getLocationOnScreen(loc);
        float rawX = ev.getRawX();
        float rawY = ev.getRawY();
        return rawX >= loc[0] && rawX <= loc[0] + excludedView.getWidth()
                && rawY >= loc[1] && rawY <= loc[1] + excludedView.getHeight();
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                downX = ev.getX();
                downY = ev.getY();
                dragging = false;
                excludedGesture = startedInExcludedView(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                if (!excludedGesture && !dragging) {
                    float dx = ev.getX() - downX;
                    float dy = ev.getY() - downY;
                    if (Math.abs(dx) > slop && Math.abs(dx) > Math.abs(dy) * 1.5f) {
                        dragging = true;
                        return true;
                    }
                }
                break;
        }
        return false;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (dragging && ev.getActionMasked() == MotionEvent.ACTION_UP) {
            float dx = ev.getX() - downX;
            dragging = false;
            if (listener != null) {
                if (dx < 0) listener.onSwipeLeft(); else listener.onSwipeRight();
            }
            return true;
        }
        if (ev.getActionMasked() == MotionEvent.ACTION_CANCEL) dragging = false;
        return dragging;
    }
}
