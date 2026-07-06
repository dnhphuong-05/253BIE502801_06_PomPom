package com.pompom.group6.activities;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.pompom.group6.R;
import com.pompom.group6.adapters.VoucherAdapter;
import com.pompom.group6.databinding.ActivityVouchersBinding;
import com.pompom.group6.models.Voucher;
import com.pompom.group6.network.ApiClient;
import com.pompom.group6.network.Session;
import com.pompom.group6.network.dto.ApiVoucher;
import com.pompom.group6.utils.StatusBarUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VouchersActivity extends SwipeBackActivity {

    private ActivityVouchersBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVouchersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        StatusBarUtils.applyPinkHeader(this);
        StatusBarUtils.applyHeaderContentInsets(this, binding.header.getRoot(), binding.getRoot());

        binding.header.tvHeaderTitle.setText("Voucher của tôi");
        binding.header.btnBack.setOnClickListener(v -> finish());

        binding.rvVouchers.setLayoutManager(new LinearLayoutManager(this));
        binding.emptyState.tvEmptyText.setText("Bạn chưa có voucher nào");
        binding.emptyState.ivEmptyIcon.setImageResource(R.drawable.ic_gift);

        String userOid = Session.getUserOid(this);
        if (userOid == null) {
            binding.emptyState.getRoot().setVisibility(View.VISIBLE);
            return;
        }
        ApiClient.get().getUserVouchers(userOid).enqueue(new Callback<List<ApiVoucher>>() {
            @Override
            public void onResponse(Call<List<ApiVoucher>> call, Response<List<ApiVoucher>> resp) {
                if (binding == null) return;
                List<Voucher> vouchers = new ArrayList<>();
                if (resp.isSuccessful() && resp.body() != null) {
                    for (ApiVoucher a : resp.body()) {
                        int remaining = Math.max(a.usageLimit - a.usedCount, 0);
                        String expiry = a.endDate != null && a.endDate.length() >= 10 ? a.endDate.substring(0, 10) : a.endDate;
                        vouchers.add(new Voucher(0, a.code, a.discountType, a.discountValue,
                                a.minOrderAmount, expiry, remaining));
                    }
                }
                binding.rvVouchers.setAdapter(new VoucherAdapter(vouchers));
                binding.emptyState.getRoot().setVisibility(vouchers.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onFailure(Call<List<ApiVoucher>> call, Throwable t) {
                if (binding == null) return;
                binding.emptyState.getRoot().setVisibility(View.VISIBLE);
            }
        });
    }
}
