package com.pompom.group6.utils;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.customview.widget.ViewDragHelper;

public class SwipeBackLayout extends FrameLayout {
    private ViewDragHelper viewDragHelper;
    private OnSwipeBackListener onSwipeBackListener;
    private float scrollTouchSlop;

    public interface OnSwipeBackListener {
        void onSwipeBack();
    }

    public SwipeBackLayout(@NonNull Context context) {
        this(context, null);
    }

    public SwipeBackLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeBackLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        viewDragHelper = ViewDragHelper.create(this, 1.0f, new ViewDragHelper.Callback() {
            @Override
            public boolean tryCaptureView(@NonNull android.view.View child, int pointerId) {
                return false; // We don't want to drag any child view, we drag the whole content
            }

            @Override
            public int clampViewPositionHorizontal(@NonNull android.view.View child, int left, int dx) {
                return Math.max(0, left);
            }

            @Override
            public void onViewPositionChanged(@NonNull android.view.View changedView, int left, int top, int dx, int dy) {
                if (left >= getWidth()) {
                    if (onSwipeBackListener != null) {
                        onSwipeBackListener.onSwipeBack();
                    }
                }
            }

            @Override
            public void onViewReleased(@NonNull android.view.View releasedChild, float xvel, float yvel) {
                if (releasedChild.getLeft() > getWidth() / 3 || xvel > 500) {
                    viewDragHelper.settleCapturedViewAt(getWidth(), 0);
                } else {
                    viewDragHelper.settleCapturedViewAt(0, 0);
                }
                invalidate();
            }
        });
        viewDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
        scrollTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
    }

    public void setOnSwipeBackListener(OnSwipeBackListener listener) {
        this.onSwipeBackListener = listener;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return viewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        viewDragHelper.processTouchEvent(ev);
        return true;
    }

    @Override
    public void computeScroll() {
        if (viewDragHelper.continueSettling(true)) {
            invalidate();
        }
    }
}
