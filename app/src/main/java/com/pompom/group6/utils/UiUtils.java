package com.pompom.group6.utils;

import android.app.Activity;

import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.pompom.group6.R;

public final class UiUtils {

    private UiUtils() {}

    /** Paints the status bar brand pink with light (white) icons, matching the profile screens. */
    public static void applyPinkStatusBar(Activity activity) {
        activity.getWindow().setStatusBarColor(
                ContextCompat.getColor(activity, R.color.brand_pink));
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(
                activity.getWindow(), activity.getWindow().getDecorView());
        if (controller != null) {
            controller.setAppearanceLightStatusBars(false);
        }
    }
}
