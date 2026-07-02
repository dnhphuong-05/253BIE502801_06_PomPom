package com.pompom.group6.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.pompom.group6.fragments.VoucherPageFragment;

public class VoucherPagerAdapter extends FragmentStateAdapter {

    public VoucherPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public androidx.fragment.app.Fragment createFragment(int position) {
        return VoucherPageFragment.newInstance(position);
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
