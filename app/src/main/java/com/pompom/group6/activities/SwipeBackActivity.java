package com.pompom.group6.activities;

import android.animation.ArgbEvaluator;
import android.graphics.Color;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;
import com.pompom.group6.R;

/**
 * Base activity thêm cử chỉ "vuốt mép trái sang phải để thoát" giống trang chi tiết
 * sản phẩm. Mọi màn con chỉ cần {@code extends SwipeBackActivity} là có sẵn tính năng;
 * mũi tên chỉ báo được chèn tự động, không cần sửa layout.
 */
public abstract class SwipeBackActivity extends AppCompatActivity {

    private boolean swipeBackEnabled = true;

    private MaterialCardView swipeIndicator;
    private ImageView swipeArrow;

    private float swipeDownX, swipeDownY;
    private boolean edgeSwipe, swipeDragging;
    private float edgePx, slopPx, colorStartPx, colorEndPx, iconLiftPx;
    private final ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    /** Cho phép màn con tắt cử chỉ nếu cần (mặc định bật). */
    protected void setSwipeBackEnabled(boolean enabled) {
        this.swipeBackEnabled = enabled;
        if (swipeIndicator != null && !enabled) swipeIndicator.setAlpha(0f);
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        if (swipeIndicator == null) buildIndicator();
    }

    private void buildIndicator() {
        ViewGroup content = findViewById(android.R.id.content);
        if (content == null) return;

        float d = getResources().getDisplayMetrics().density;
        edgePx = 32 * d;
        slopPx = 8 * d;
        colorStartPx = 20 * d;
        colorEndPx = 30 * d;
        iconLiftPx = 64 * d;

        int brandPink = ContextCompat.getColor(this, R.color.brand_pink);

        MaterialCardView card = new MaterialCardView(this);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams((int) (48 * d), (int) (48 * d));
        lp.gravity = Gravity.CENTER_VERTICAL | Gravity.START;
        lp.leftMargin = (int) (8 * d);
        card.setLayoutParams(lp);
        card.setRadius(24 * d);
        card.setCardElevation(6 * d);
        card.setElevation(20 * d);
        card.setStrokeWidth((int) (2 * d));
        card.setStrokeColor(brandPink);
        card.setCardBackgroundColor(Color.WHITE);
        card.setAlpha(0f);

        ImageView arrow = new ImageView(this);
        FrameLayout.LayoutParams alp = new FrameLayout.LayoutParams((int) (26 * d), (int) (26 * d));
        alp.gravity = Gravity.CENTER;
        arrow.setLayoutParams(alp);
        arrow.setImageResource(R.drawable.ic_left_chevron);
        arrow.setColorFilter(brandPink);
        card.addView(arrow);

        content.addView(card);
        swipeIndicator = card;
        swipeArrow = arrow;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!swipeBackEnabled || swipeIndicator == null) return super.dispatchTouchEvent(ev);
        int screenW = getResources().getDisplayMetrics().widthPixels;

        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                swipeDownX = ev.getX();
                swipeDownY = ev.getY();
                edgeSwipe = swipeDownX <= edgePx;
                swipeDragging = false;
                break;

            case MotionEvent.ACTION_MOVE:
                if (edgeSwipe && !swipeDragging) {
                    float dx = ev.getX() - swipeDownX;
                    float dy = ev.getY() - swipeDownY;
                    if (dx > slopPx && dx > Math.abs(dy) * 1.5f) {
                        swipeDragging = true;
                        MotionEvent cancel = MotionEvent.obtain(ev);
                        cancel.setAction(MotionEvent.ACTION_CANCEL);
                        super.dispatchTouchEvent(cancel);
                        cancel.recycle();
                    }
                }
                if (swipeDragging) {
                    float dx = Math.max(0, ev.getX() - swipeDownX);
                    float homeCx = swipeIndicator.getLeft() + swipeIndicator.getWidth() / 2f;
                    float homeCy = swipeIndicator.getTop() + swipeIndicator.getHeight() / 2f;
                    swipeIndicator.setAlpha(Math.min(1f, dx / (colorStartPx * 0.5f)));
                    swipeIndicator.setTranslationX(ev.getX() - homeCx);
                    swipeIndicator.setTranslationY(ev.getY() - homeCy - iconLiftPx);

                    float t = Math.max(0f, Math.min(1f,
                            (dx - colorStartPx) / (colorEndPx - colorStartPx)));
                    int brandPink = ContextCompat.getColor(this, R.color.brand_pink);
                    int bg = (int) argbEvaluator.evaluate(t, Color.WHITE, brandPink);
                    int arrow = (int) argbEvaluator.evaluate(t, brandPink, Color.WHITE);
                    swipeIndicator.setCardBackgroundColor(bg);
                    swipeIndicator.setStrokeColor(brandPink);
                    swipeArrow.setColorFilter(arrow);
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (swipeDragging) {
                    swipeDragging = false;
                    edgeSwipe = false;
                    float dx = Math.max(0, ev.getX() - swipeDownX);
                    if (dx >= colorEndPx) {
                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    } else {
                        swipeIndicator.animate()
                                .alpha(0f).translationX(0f).translationY(0f).setDuration(180).start();
                    }
                    return true;
                }
                edgeSwipe = false;
                break;
        }
        return super.dispatchTouchEvent(ev);
    }
}
