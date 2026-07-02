package com.pompom.group6;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.ViewGroup;
import com.pompom.group6.activities.AiChatActivity;
import android.content.Intent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.pompom.group6.adapters.MainViewPagerAdapter;
import com.pompom.group6.databinding.ActivityMainBinding;
import com.pompom.group6.fragments.HomeFragment;
import com.pompom.group6.fragments.ShopFragment;
import com.pompom.group6.fragments.CommunityFragment;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private MainViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Modern Edge-to-Edge: Content flows behind status and navigation bars
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        // Make navigation bar truly transparent to allow app background to show through
        getWindow().setNavigationBarColor(Color.TRANSPARENT);

        // Disable system-enforced contrast to keep the bar truly transparent (Android 10+)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            getWindow().setNavigationBarContrastEnforced(false);
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Handle navigation bar insets for bottom nav
        // We apply padding to the inner LinearLayout so the background of the ConstraintLayout (parent)
        // can bleed into the system navigation bar area.
        ViewCompat.setOnApplyWindowInsetsListener(binding.bottomNavContainer, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            if (v instanceof ViewGroup && ((ViewGroup) v).getChildCount() > 0) {
                View innerLayout = ((ViewGroup) v).getChildAt(0);
                // Simple bottom padding to lift icons above the system navigation bar
                innerLayout.setPadding(0, innerLayout.getPaddingTop(),
                        0, systemBars.bottom);
            }
            return insets;
        });

        // Handle status bar insets if needed for other components
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            // Root padding should not be applied if we want full screen, 
            // but we can pass it down if needed.
            return insets;
        });

        setupViewPager();
        setupNavigation();
        setupFloatingMascot();

        // Initial state
        updateNavUI(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.notifyItemChanged(4);
        }
    }

    private void setupViewPager() {
        adapter = new MainViewPagerAdapter(this);
        binding.viewPager.setAdapter(adapter);
        binding.viewPager.setUserInputEnabled(false); // Disable swiping as per user request

        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateNavUI(position);
            }
        });
        
        // Prevent swiping away from Me screen if it's the last one, or keep it consistent
        binding.viewPager.setOffscreenPageLimit(3);
    }

    public void switchToTab(int index) {
        binding.viewPager.setCurrentItem(index);
    }

    private void setupNavigation() {
        binding.navHome.setOnClickListener(v -> binding.viewPager.setCurrentItem(0));
        binding.navShop.setOnClickListener(v -> binding.viewPager.setCurrentItem(1));
        binding.btnAi.setOnClickListener(v -> {
            binding.viewPager.setCurrentItem(2);
            // Handle AI button click - bounce animation
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(v, "scaleX", 1f, 1.15f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(v, "scaleY", 1f, 1.15f, 1f);
            AnimatorSet set = new AnimatorSet();
            set.playTogether(scaleX, scaleY);
            set.setDuration(300);
            set.start();
        });
        binding.navCommunity.setOnClickListener(v -> binding.viewPager.setCurrentItem(3));
        binding.navMe.setOnClickListener(v -> binding.viewPager.setCurrentItem(4));
    }

    private void setupFloatingMascot() {
        binding.ivFloatingMascot.setOnTouchListener(new View.OnTouchListener() {
            private float dX, dY;
            private float startX, startY;
            private static final int CLICK_ACTION_THRESHOLD = 10;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        dX = view.getX() - event.getRawX();
                        dY = view.getY() - event.getRawY();
                        startX = event.getRawX();
                        startY = event.getRawY();
                        break;

                    case MotionEvent.ACTION_MOVE:
                        view.animate()
                                .x(event.getRawX() + dX)
                                .y(event.getRawY() + dY)
                                .setDuration(0)
                                .start();
                        break;

                    case MotionEvent.ACTION_UP:
                        float endX = event.getRawX();
                        float endY = event.getRawY();
                        if (isAClick(startX, endX, startY, endY)) {
                            view.performClick();
                        }
                        break;
                    default:
                        return false;
                }
                return true;
            }

            private boolean isAClick(float startX, float endX, float startY, float endY) {
                float differenceX = Math.abs(startX - endX);
                float differenceY = Math.abs(startY - endY);
                return !(differenceX > CLICK_ACTION_THRESHOLD || differenceY > CLICK_ACTION_THRESHOLD);
            }
        });

        binding.ivFloatingMascot.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AiChatActivity.class);
            startActivity(intent);
        });
    }

    private void updateNavUI(int index) {
        WindowInsetsControllerCompat controller = WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        
        if (controller != null) {
            // Dynamic Status Bar Icon Color based on Fragment background
            if (index == 0 || index == 1 || index == 4) { // Home (0), Shop (1) or Me (4) have pink headers
                // Pink background -> White icons (disable light status bar)
                controller.setAppearanceLightStatusBars(false);
            } else {
                // White background -> Dark icons
                controller.setAppearanceLightStatusBars(true);
            }
            
            // Always keep light navigation bar as bottom nav is light-colored
            controller.setAppearanceLightNavigationBars(true);
        }

        selectTab(binding.navHome, binding.ivHome, binding.tvHome,
                index == 0 ? R.drawable.ic_home_pink : R.drawable.ic_home_border, index == 0);
        selectTab(binding.navShop, binding.ivShop, binding.tvShop,
                index == 1 ? R.drawable.ic_shop_pink : R.drawable.ic_shop_border, index == 1);
        
        // AI Center button visual state
        if (index == 2) {
            binding.btnAi.setAlpha(1.0f);
            binding.btnAi.setScaleX(1.1f);
            binding.btnAi.setScaleY(1.1f);
        } else {
            binding.btnAi.setAlpha(0.85f);
            binding.btnAi.setScaleX(1.0f);
            binding.btnAi.setScaleY(1.0f);
        }

        selectTab(binding.navCommunity, binding.ivCommunity, binding.tvCommunity,
                index == 3 ? R.drawable.ic_community_pink : R.drawable.ic_community_border, index == 3);
        selectTab(binding.navMe, binding.ivMe, binding.tvMe,
                index == 4 ? R.drawable.ic_me_pink : R.drawable.ic_me_border, index == 4);
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
}
