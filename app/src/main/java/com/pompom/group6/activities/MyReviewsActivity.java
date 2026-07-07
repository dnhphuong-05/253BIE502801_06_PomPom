package com.pompom.group6.activities;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.pompom.group6.R;
import com.pompom.group6.adapters.MyReviewAdapter;
import com.pompom.group6.databinding.ActivityMyReviewsBinding;
import com.pompom.group6.network.ApiClient;
import com.pompom.group6.network.Session;
import com.pompom.group6.network.dto.ApiMyReview;
import com.pompom.group6.utils.StatusBarUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Màn "Đánh giá của tôi": đọc thật từ GET /api/users/:id/reviews. */
public class MyReviewsActivity extends SwipeBackActivity {

    private ActivityMyReviewsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyReviewsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        StatusBarUtils.applyPinkHeader(this);
        StatusBarUtils.applyHeaderContentInsets(this, binding.header.getRoot(), binding.getRoot());

        binding.header.tvHeaderTitle.setText("Đánh giá của tôi");
        binding.header.btnBack.setOnClickListener(v -> finish());

        binding.rvReviews.setLayoutManager(new LinearLayoutManager(this));
        binding.emptyState.tvEmptyText.setText("Bạn chưa viết đánh giá nào");
        binding.emptyState.ivEmptyIcon.setImageResource(R.drawable.ic_star);

        String userOid = Session.getUserOid(this);
        if (userOid == null) {
            binding.emptyState.getRoot().setVisibility(View.VISIBLE);
            return;
        }
        loadReviews(userOid);
    }

    private void loadReviews(String userOid) {
        ApiClient.get().getUserReviews(userOid).enqueue(new Callback<List<ApiMyReview>>() {
            @Override
            public void onResponse(Call<List<ApiMyReview>> call, Response<List<ApiMyReview>> resp) {
                if (binding == null) return;
                List<ApiMyReview> list = resp.isSuccessful() && resp.body() != null
                        ? resp.body() : new java.util.ArrayList<>();
                binding.rvReviews.setAdapter(new MyReviewAdapter(list));
                binding.emptyState.getRoot().setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onFailure(Call<List<ApiMyReview>> call, Throwable t) {
                if (binding == null) return;
                binding.emptyState.getRoot().setVisibility(View.VISIBLE);
            }
        });
    }
}
