package com.pompom.group6.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.pompom.group6.fragments.CommunityFragment;
import com.pompom.group6.fragments.HomeFragment;
import com.pompom.group6.fragments.MeFragment;
import com.pompom.group6.fragments.ShopFragment;

import java.util.ArrayList;
import java.util.List;

public class MainViewPagerAdapter extends FragmentStateAdapter {
    private final List<Fragment> fragments = new ArrayList<>();

    public MainViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        fragments.add(new HomeFragment());
        fragments.add(new ShopFragment());
        fragments.add(new CommunityFragment());
        fragments.add(new MeFragment());
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragments.get(position);
    }

    @Override
    public int getItemCount() {
        return fragments.size();
    }
}
