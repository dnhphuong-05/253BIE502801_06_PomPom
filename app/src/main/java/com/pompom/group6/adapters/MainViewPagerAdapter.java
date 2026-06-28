package com.pompom.group6.adapters;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.pompom.group6.fragments.AiHubFragment;
import com.pompom.group6.fragments.CommunityFragment;
import com.pompom.group6.fragments.HomeFragment;
import com.pompom.group6.fragments.MeFragment;
import com.pompom.group6.fragments.PremiumProfileFragment;
import com.pompom.group6.fragments.ShopFragment;

public class MainViewPagerAdapter extends FragmentStateAdapter {
    private final FragmentActivity fragmentActivity;

    public MainViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        this.fragmentActivity = fragmentActivity;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new HomeFragment();
            case 1: return new ShopFragment();
            case 2: return new AiHubFragment();
            case 3: return new CommunityFragment();
            case 4:
                SharedPreferences prefs = fragmentActivity.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
                boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);
                if (isLoggedIn) {
                    return new PremiumProfileFragment();
                } else {
                    return new MeFragment();
                }
            default: return new HomeFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }

    @Override
    public long getItemId(int position) {
        if (position == 4) {
            SharedPreferences prefs = fragmentActivity.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);
            return isLoggedIn ? 1004 : 4; // Force recreation when login state changes
        }
        return position;
    }

    @Override
    public boolean containsItem(long itemId) {
        return itemId >= 0;
    }
}
