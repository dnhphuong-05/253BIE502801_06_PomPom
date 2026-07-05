package com.pompom.group6.activities;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.pompom.group6.R;
import com.pompom.group6.adapters.OrderAdapter;
import com.pompom.group6.databinding.ActivityOrdersBinding;
import com.pompom.group6.models.Order;
import com.pompom.group6.network.ApiClient;
import com.pompom.group6.network.Session;
import com.pompom.group6.network.dto.ApiOrder;
import com.pompom.group6.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrdersActivity extends SwipeBackActivity {

    private ActivityOrdersBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        UiUtils.applyPinkStatusBar(this);

        binding.header.tvHeaderTitle.setText("Đơn hàng của tôi");
        binding.header.btnBack.setOnClickListener(v -> finish());

        binding.rvOrders.setLayoutManager(new LinearLayoutManager(this));
        binding.emptyState.tvEmptyText.setText("Bạn chưa có đơn hàng nào");
        binding.emptyState.ivEmptyIcon.setImageResource(R.drawable.ic_bag);

        String userOid = Session.getUserOid(this);
        if (userOid == null) {
            binding.emptyState.getRoot().setVisibility(View.VISIBLE);
            return;
        }
        loadOrders(userOid);
    }

    /** Danh sách đơn hàng của user (từ MongoDB). */
    private void loadOrders(String userOid) {
        ApiClient.get().getOrders(userOid).enqueue(new Callback<List<ApiOrder>>() {
            @Override
            public void onResponse(Call<List<ApiOrder>> call, Response<List<ApiOrder>> resp) {
                if (binding == null) return;
                List<Order> orders = new ArrayList<>();
                if (resp.isSuccessful() && resp.body() != null) {
                    for (ApiOrder o : resp.body()) {
                        orders.add(new Order(0, o.orderNumber, o.finalAmount, o.status,
                                o.paymentMethod, o.createdAt, o.itemCount, o.firstItemName, o.firstItemImage));
                    }
                }
                binding.rvOrders.setAdapter(new OrderAdapter(orders));
                binding.emptyState.getRoot().setVisibility(orders.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onFailure(Call<List<ApiOrder>> call, Throwable t) {
                if (binding == null) return;
                binding.emptyState.getRoot().setVisibility(View.VISIBLE);
            }
        });
    }
}
