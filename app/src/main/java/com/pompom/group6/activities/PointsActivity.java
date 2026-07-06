package com.pompom.group6.activities;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.pompom.group6.R;
import com.pompom.group6.adapters.PointsTransactionAdapter;
import com.pompom.group6.databinding.ActivityPointsBinding;
import com.pompom.group6.models.PointsTransaction;
import com.pompom.group6.network.ApiClient;
import com.pompom.group6.network.Session;
import com.pompom.group6.network.dto.ApiPointsTransaction;
import com.pompom.group6.network.dto.ApiUser;
import com.pompom.group6.utils.StatusBarUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PointsActivity extends SwipeBackActivity {

    private ActivityPointsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPointsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        StatusBarUtils.applyPinkHeader(this);
        StatusBarUtils.applyHeaderContentInsets(this, binding.header.getRoot(), binding.getRoot());

        binding.header.tvHeaderTitle.setText("Điểm của tôi");
        binding.header.btnBack.setOnClickListener(v -> finish());

        binding.rvPoints.setLayoutManager(new LinearLayoutManager(this));
        binding.emptyState.tvEmptyText.setText("Chưa có giao dịch điểm nào");
        binding.emptyState.ivEmptyIcon.setImageResource(R.drawable.ic_loyalty);

        String userOid = Session.getUserOid(this);
        if (userOid == null) {
            binding.emptyState.getRoot().setVisibility(View.VISIBLE);
            return;
        }

        loadHeader(userOid);
        loadTransactions(userOid);
    }

    /** Điểm + hạng thành viên (từ MongoDB). */
    private void loadHeader(String userOid) {
        ApiClient.get().getUser(userOid).enqueue(new Callback<ApiUser>() {
            @Override
            public void onResponse(Call<ApiUser> call, Response<ApiUser> resp) {
                if (binding == null || !resp.isSuccessful() || resp.body() == null) return;
                ApiUser u = resp.body();
                binding.tvPoints.setText(String.format(Locale.getDefault(), "%,d", u.points));
                binding.tvLevel.setText(u.membershipLevel);
            }
            @Override public void onFailure(Call<ApiUser> call, Throwable t) {}
        });
    }

    /** Lịch sử giao dịch điểm (từ MongoDB). */
    private void loadTransactions(String userOid) {
        ApiClient.get().getUserPoints(userOid).enqueue(new Callback<List<ApiPointsTransaction>>() {
            @Override
            public void onResponse(Call<List<ApiPointsTransaction>> call, Response<List<ApiPointsTransaction>> resp) {
                if (binding == null) return;
                List<PointsTransaction> list = new ArrayList<>();
                if (resp.isSuccessful() && resp.body() != null) {
                    for (ApiPointsTransaction t : resp.body()) {
                        list.add(new PointsTransaction(t.pointsChange, t.reason, t.createdAt));
                    }
                }
                binding.rvPoints.setAdapter(new PointsTransactionAdapter(list));
                binding.emptyState.getRoot().setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onFailure(Call<List<ApiPointsTransaction>> call, Throwable t) {
                if (binding == null) return;
                binding.emptyState.getRoot().setVisibility(View.VISIBLE);
            }
        });
    }
}
