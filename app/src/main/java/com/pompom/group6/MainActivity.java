package com.pompom.group6;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.pompom.group6.databinding.ActivityMainBinding;
import com.pompom.group6.fragments.HomeFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.white));

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (savedInstanceState == null) {
            loadFragment(new HomeFragment());
        }
        // Always set initial state explicitly after view is ready
        selectTab(binding.navHome, binding.ivHome, binding.tvHome,
                R.drawable.ic_home_pink, true);
        selectTab(binding.navShop, binding.ivShop, binding.tvShop,
                R.drawable.ic_shop_border, false);
        selectTab(binding.navCommunity, binding.ivCommunity, binding.tvCommunity,
                R.drawable.ic_community_border, false);
        selectTab(binding.navMe, binding.ivMe, binding.tvMe,
                R.drawable.ic_me_border, false);

        setupNavigation();
    }

    private void setupNavigation() {
        binding.navHome.setOnClickListener(v -> {
            loadFragment(new HomeFragment());
            updateNavUI(0);
        });
        binding.navShop.setOnClickListener(v -> updateNavUI(1));
        binding.navCommunity.setOnClickListener(v -> updateNavUI(2));
        binding.navMe.setOnClickListener(v -> updateNavUI(3));
        binding.btnAi.setOnClickListener(v -> {
            // Handle AI button click - bounce animation
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(v, "scaleX", 1f, 1.15f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(v, "scaleY", 1f, 1.15f, 1f);
            AnimatorSet set = new AnimatorSet();
            set.playTogether(scaleX, scaleY);
            set.setDuration(300);
            set.start();
        });
    }

    private void updateNavUI(int index) {
        selectTab(binding.navHome, binding.ivHome, binding.tvHome,
                index == 0 ? R.drawable.ic_home_pink : R.drawable.ic_home_border, index == 0);
        selectTab(binding.navShop, binding.ivShop, binding.tvShop,
                index == 1 ? R.drawable.ic_shop_pink : R.drawable.ic_shop_border, index == 1);
        selectTab(binding.navCommunity, binding.ivCommunity, binding.tvCommunity,
                index == 2 ? R.drawable.ic_community_pink : R.drawable.ic_community_border, index == 2);
        selectTab(binding.navMe, binding.ivMe, binding.tvMe,
                index == 3 ? R.drawable.ic_me_pink : R.drawable.ic_me_border, index == 3);
    }

    private void selectTab(View container, ImageView icon, TextView label,
                           int iconRes, boolean selected) {
        // Set icon image explicitly
        icon.setImageResource(iconRes);
        // Set state for color selectors
        icon.setSelected(selected);
        label.setSelected(selected);
        container.setSelected(selected);

        if (selected) {
            // Pop-up bounce when selected
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(icon, "scaleX", 0.85f, 1.15f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(icon, "scaleY", 0.85f, 1.15f, 1f);
            AnimatorSet set = new AnimatorSet();
            set.playTogether(scaleX, scaleY);
            set.setDuration(250);
            set.start();
            label.setTextSize(11.5f);
        } else {
            icon.setScaleX(1f);
            icon.setScaleY(1f);
            label.setTextSize(11f);
        }
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }
}
