package com.pompom.group6.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.pompom.group6.MainActivity;
import com.pompom.group6.databinding.ActivitySplashBinding;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Full screen edge-to-edge
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        );
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        ActivitySplashBinding binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean onboardingDone = prefs.getBoolean("onboarding_completed", false);

        if (onboardingDone) {
            // Returning user: play the logo reveal (~1.7s), then go straight to Home
            // the moment the animation finishes.
            binding.ivSplashLogo.setScaleX(0.8f);
            binding.ivSplashLogo.setScaleY(0.8f);
            binding.ivSplashLogo.setAlpha(0f);

            binding.tvSplashSlogan.setAlpha(0f);
            binding.tvSplashSlogan.animate()
                    .alpha(1f)
                    .setStartDelay(300)
                    .setDuration(1200)
                    .start();

            binding.ivSplashLogo.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(1200)
                    .withEndAction(() -> goTo(MainActivity.class))
                    .start();
            return;
        }

        // First launch: full branded splash, then the onboarding slides.
        // Slow zoom-in to match the reference splash feeling.
        binding.ivSplashLogo.setScaleX(0.72f);
        binding.ivSplashLogo.setScaleY(0.72f);
        binding.ivSplashLogo.setAlpha(0.9f);
        binding.ivSplashLogo.animate()
                .scaleX(1.18f)
                .scaleY(1.18f)
                .alpha(1f)
                .setDuration(1300)
                .start();

        binding.tvSplashSlogan.setAlpha(0f);
        binding.tvSplashSlogan.animate()
                .alpha(1f)
                .setDuration(1500)
                .setStartDelay(300)
                .start();

        // Keep the splash visible (no fade-to-black), then cross-fade to onboarding.
        binding.getRoot().postDelayed(() -> goTo(OnboardingActivity.class), 1500);
    }

    private void goTo(Class<?> target) {
        startActivity(new Intent(SplashActivity.this, target));
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        finish();
    }
}
