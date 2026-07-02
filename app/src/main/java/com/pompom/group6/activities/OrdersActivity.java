package com.pompom.group6.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pompom.group6.R;
import com.pompom.group6.adapters.OrderAdapter;
import com.pompom.group6.database.OrderDAO;
import com.pompom.group6.databinding.ActivityOrdersBinding;
import com.pompom.group6.models.Order;
import com.pompom.group6.utils.UiUtils;

import java.util.List;

public class OrdersActivity extends AppCompatActivity {

    private ActivityOrdersBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        UiUtils.applyPinkStatusBar(this);

        binding.header.tvHeaderTitle.setText("Đơn hàng của tôi");
        binding.header.btnBack.setOnClickListener(v -> finish());

        OrderDAO orderDAO = new OrderDAO(this);
        SharedPreferences prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", 1);

        List<Order> orders = orderDAO.getOrders(userId);

        binding.rvOrders.setLayoutManager(new LinearLayoutManager(this));
        binding.rvOrders.setAdapter(new OrderAdapter(orders));

        binding.emptyState.tvEmptyText.setText("Bạn chưa có đơn hàng nào");
        binding.emptyState.ivEmptyIcon.setImageResource(R.drawable.ic_bag);
        binding.emptyState.getRoot().setVisibility(orders.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
