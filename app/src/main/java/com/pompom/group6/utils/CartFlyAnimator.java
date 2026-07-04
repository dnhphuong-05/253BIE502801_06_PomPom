package com.pompom.group6.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.pompom.group6.R;

/**
 * Hiệu ứng "bay vào giỏ": một bong bóng hồng mang số lượng sản phẩm bay theo
 * đường cong từ vị trí popup tới icon giỏ hàng, kèm scale/fade mượt mà.
 *
 * Toạ độ truyền vào là toạ độ TRÊN MÀN HÌNH (getLocationOnScreen).
 */
public final class CartFlyAnimator {

    private CartFlyAnimator() {}

    public static void fly(Activity activity, int quantity, float startScreenX, float startScreenY) {
        if (activity == null) return;
        final ViewGroup root = activity.findViewById(android.R.id.content);
        if (root == null) return;

        final int[] rootLoc = new int[2];
        root.getLocationOnScreen(rootLoc);

        final float density = activity.getResources().getDisplayMetrics().density;
        final int size = (int) (40 * density);

        // Vị trí bắt đầu (quy về toạ độ trong root)
        final float startX = startScreenX - rootLoc[0];
        final float startY = startScreenY - rootLoc[1];

        // Vị trí đích = icon giỏ hàng nếu tìm được, ngược lại góc trên-phải
        View cart = findCartTarget(root);
        final float targetX, targetY;
        if (cart != null && cart.isShown()) {
            int[] cLoc = new int[2];
            cart.getLocationOnScreen(cLoc);
            targetX = cLoc[0] - rootLoc[0] + cart.getWidth() / 2f;
            targetY = cLoc[1] - rootLoc[1] + cart.getHeight() / 2f;
        } else {
            targetX = root.getWidth() - 36 * density;
            targetY = 56 * density;
        }

        // Bong bóng hồng mang số lượng
        final TextView bubble = new TextView(activity);
        bubble.setText(String.valueOf(quantity));
        bubble.setTextColor(Color.WHITE);
        bubble.setTextSize(15f);
        bubble.setTypeface(bubble.getTypeface(), android.graphics.Typeface.BOLD);
        bubble.setGravity(Gravity.CENTER);
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.OVAL);
        bg.setColor(ContextCompat.getColor(activity, R.color.brand_pink));
        bubble.setBackground(bg);
        bubble.setElevation(24 * density);

        root.addView(bubble, new FrameLayout.LayoutParams(size, size));
        bubble.setX(startX - size / 2f);
        bubble.setY(startY - size / 2f);

        // Điểm điều khiển cho đường cong bậc hai (vòng lên trên cho đẹp)
        final float ctrlX = (startX + targetX) / 2f;
        final float ctrlY = Math.min(startY, targetY) - 120 * density;

        // Pop nhẹ lúc xuất hiện
        bubble.setScaleX(0.4f);
        bubble.setScaleY(0.4f);
        bubble.animate().scaleX(1f).scaleY(1f)
                .setInterpolator(new OvershootInterpolator())
                .setDuration(180).start();

        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f);
        anim.setStartDelay(140);
        anim.setDuration(620);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.addUpdateListener(a -> {
            float t = a.getAnimatedFraction();
            float mt = 1f - t;
            // Quadratic Bézier
            float x = mt * mt * startX + 2 * mt * t * ctrlX + t * t * targetX;
            float y = mt * mt * startY + 2 * mt * t * ctrlY + t * t * targetY;
            bubble.setX(x - size / 2f);
            bubble.setY(y - size / 2f);
            float scale = 1f - 0.55f * t;
            bubble.setScaleX(scale);
            bubble.setScaleY(scale);
            if (t > 0.82f) bubble.setAlpha(Math.max(0f, (1f - t) / 0.18f));
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                root.removeView(bubble);
                pulse(cart);
            }
        });
        anim.start();
    }

    /** Nảy nhẹ icon giỏ hàng khi bong bóng cập bến. */
    private static void pulse(View cart) {
        if (cart == null) return;
        cart.animate().scaleX(1.25f).scaleY(1.25f).setDuration(120)
                .withEndAction(() -> cart.animate().scaleX(1f).scaleY(1f).setDuration(120).start())
                .start();
    }

    /** Tìm icon giỏ hàng trong hierarchy theo các id đã biết. */
    private static View findCartTarget(View root) {
        int[] ids = {R.id.btnCartTop, R.id.cartIconContainer, R.id.ivCart};
        for (int id : ids) {
            View v = root.findViewById(id);
            if (v != null) return v;
        }
        return null;
    }
}
