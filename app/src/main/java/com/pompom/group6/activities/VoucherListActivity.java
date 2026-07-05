package com.pompom.group6.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayoutMediator;
import com.pompom.group6.adapters.VoucherPagerAdapter;
import com.pompom.group6.databinding.ActivityVoucherListBinding;
import com.pompom.group6.utils.StatusBarUtils;

public class VoucherListActivity extends SwipeBackActivity {

    private static final String[] TAB_TITLES = {"Tất cả", "Sắp hết hạn", "Đã dùng"};

    private ActivityVoucherListBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVoucherListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Transparent status bar: this screen's background shows through.
        StatusBarUtils.applyTransparent(this);

        binding.btnBack.setOnClickListener(v -> finish());

        binding.viewPager.setAdapter(new VoucherPagerAdapter(this));
        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> tab.setText(TAB_TITLES[position])).attach();

        binding.btnApplyCode.setOnClickListener(v -> applyCode());
    }

    private void applyCode() {
        String code = binding.etVoucherCode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, "Vui lòng nhập mã voucher", Toast.LENGTH_SHORT).show();
            return;
        }
        Toast.makeText(this, "Đã áp dụng mã: " + code, Toast.LENGTH_SHORT).show();
        binding.etVoucherCode.setText("");
    }
}
