package com.pompom.group6.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

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

        // Slow zoom-in to match the reference splash feeling.
        binding.ivSplashLogo.setScaleX(0.72f);
        binding.ivSplashLogo.setScaleY(0.72f);
        binding.ivSplashLogo.setAlpha(0.9f);
        binding.ivSplashLogo.animate()
                .scaleX(1.18f)
                .scaleY(1.18f)
                .alpha(1f)
                .setDuration(2300)
                .start();

        binding.tvSplashSlogan.setAlpha(0f);
        binding.tvSplashSlogan.animate()
                .alpha(1f)
                .setDuration(1500)
                .setStartDelay(300)
                .start();

        binding.getRoot().animate()
                .alpha(0f)
                .setStartDelay(1900)
                .setDuration(500)
                .withEndAction(() -> {
                    Intent intent = new Intent(SplashActivity.this, OnboardingActivity.class);
                    startActivity(intent);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                })
                .start();
    }
}
