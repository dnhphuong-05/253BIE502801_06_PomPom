package com.pompom.group6.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.pompom.group6.MainActivity;
import com.pompom.group6.R;
import com.pompom.group6.adapters.OnboardingAdapter;
import com.pompom.group6.databinding.ActivityOnboardingBinding;
import com.pompom.group6.models.OnboardingItem;

import java.util.ArrayList;
import java.util.List;

public class OnboardingActivity extends AppCompatActivity {

    private ActivityOnboardingBinding binding;
    private OnboardingAdapter onboardingAdapter;

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

        binding = ActivityOnboardingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupOnboardingItems();
        setupIndicators();
        setCurrentIndicator(0);

        binding.viewPager.setOffscreenPageLimit(1);
        binding.viewPager.setPageTransformer((page, position) -> {
            float absPosition = Math.abs(position);
            page.setAlpha(0.35f + (1f - absPosition) * 0.65f);
            page.setTranslationX(-position * 36f);
            page.setScaleY(0.95f + (1f - absPosition) * 0.05f);
        });

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentIndicator(position);
                if (position == onboardingAdapter.getItemCount() - 1) {
                    binding.btnNext.setText(getString(R.string.btn_start));
                } else {
                    binding.btnNext.setText(getString(R.string.btn_continue));
                }
            }
        });

        binding.btnNext.setOnClickListener(v -> {
            if (binding.viewPager.getCurrentItem() + 1 < onboardingAdapter.getItemCount()) {
                binding.viewPager.setCurrentItem(binding.viewPager.getCurrentItem() + 1);
            } else {
                finishOnboarding();
            }
        });

        binding.btnSkip.setOnClickListener(v -> finishOnboarding());
    }

    /**
     * Marks onboarding as seen (in an app-level pref that survives logout) so it is
     * not shown on later launches, then enters the app.
     */
    private void finishOnboarding() {
        getSharedPreferences("app_prefs", MODE_PRIVATE)
                .edit()
                .putBoolean("onboarding_completed", true)
                .apply();
        startActivity(new Intent(OnboardingActivity.this, MainActivity.class));
        finish();
    }

    private void setupOnboardingItems() {
        List<OnboardingItem> items = new ArrayList<>();
        items.add(new OnboardingItem(
                R.drawable.spalsh2,
                getString(R.string.onboarding_title_1),
                getString(R.string.onboarding_desc_1)
        ));
        items.add(new OnboardingItem(
                R.drawable.spalsh3,
                getString(R.string.onboarding_title_2),
                getString(R.string.onboarding_desc_2)
        ));
        items.add(new OnboardingItem(
                R.drawable.splash4,
                getString(R.string.onboarding_title_3),
                getString(R.string.onboarding_desc_3)
        ));

        onboardingAdapter = new OnboardingAdapter(items);
        binding.viewPager.setAdapter(onboardingAdapter);
    }

    private void setupIndicators() {
        int count = onboardingAdapter.getItemCount();
        ImageView[] indicators = new ImageView[count];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(12, 0, 12, 0);
        for (int i = 0; i < count; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(ContextCompat.getDrawable(
                    getApplicationContext(),
                    R.drawable.indicator_inactive
            ));
            indicators[i].setLayoutParams(layoutParams);
            binding.indicatorContainer.addView(indicators[i]);
        }
    }

    private void setCurrentIndicator(int index) {
        int childCount = binding.indicatorContainer.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) binding.indicatorContainer.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        getApplicationContext(),
                        R.drawable.indicator_active
                ));
                imageView.animate().scaleX(1.2f).scaleY(1.2f).setDuration(200).start();
            } else {
                imageView.setImageDrawable(ContextCompat.getDrawable(
                        getApplicationContext(),
                        R.drawable.indicator_inactive
                ));
                imageView.animate().scaleX(1.0f).scaleY(1.0f).setDuration(200).start();
            }
        }
    }
}
