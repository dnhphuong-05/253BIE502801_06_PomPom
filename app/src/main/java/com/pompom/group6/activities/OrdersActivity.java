package com.pompom.group6.activities;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayoutMediator;
import com.pompom.group6.adapters.OrdersPagerAdapter;
import com.pompom.group6.databinding.ActivityOrdersBinding;
import com.pompom.group6.fragments.OrderListPageFragment;
import com.pompom.group6.utils.StatusBarUtils;

public class OrdersActivity extends SwipeBackActivity {

    /** Nhóm trạng thái cần lọc khi mở từ shortcut Profile: "pending" | "packing" | "shipping" | "delivered" | "return". */
    public static final String EXTRA_STATUS_GROUP = "status_group";

    private static final String[] TAB_TITLES = {
            "Tất cả", "Chờ xác nhận", "Chờ lấy hàng", "Chờ giao hàng", "Đã giao", "Trả hàng", "Đã huỷ"
    };

    private ActivityOrdersBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        StatusBarUtils.applyPinkHeader(this);
        StatusBarUtils.applyHeaderContentInsets(this, binding.header.getRoot(), binding.getRoot());

        binding.header.tvHeaderTitle.setText("Đơn hàng của tôi");
        binding.header.btnBack.setOnClickListener(v -> finish());

        binding.viewPager.setAdapter(new OrdersPagerAdapter(this));
        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> tab.setText(TAB_TITLES[position])).attach();

        binding.viewPager.setCurrentItem(initialTabIndex(), false);
    }

    /** Shortcut trạng thái từ Profile -> chọn sẵn đúng tab khi vào màn. */
    private int initialTabIndex() {
        String group = getIntent().getStringExtra(EXTRA_STATUS_GROUP);
        if (group == null) return OrderListPageFragment.TAB_ALL;
        switch (group) {
            case "pending": return OrderListPageFragment.TAB_PENDING;
            case "packing": return OrderListPageFragment.TAB_PACKING;
            case "shipping": return OrderListPageFragment.TAB_SHIPPING;
            case "delivered": return OrderListPageFragment.TAB_DELIVERED;
            case "return": return OrderListPageFragment.TAB_RETURN;
            case "cancelled": return OrderListPageFragment.TAB_CANCELLED;
            default: return OrderListPageFragment.TAB_ALL;
        }
    }
}
