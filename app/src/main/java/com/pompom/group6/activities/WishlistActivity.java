package com.pompom.group6.activities;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import com.pompom.group6.R;
import com.pompom.group6.adapters.ProductAdapter;
import com.pompom.group6.databinding.ActivityWishlistBinding;
import com.pompom.group6.models.Product;
import com.pompom.group6.network.ApiClient;
import com.pompom.group6.network.ProductMapper;
import com.pompom.group6.network.Session;
import com.pompom.group6.network.dto.ApiProduct;
import com.pompom.group6.utils.StatusBarUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WishlistActivity extends SwipeBackActivity {

    private ActivityWishlistBinding binding;
    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWishlistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        StatusBarUtils.applyPinkHeader(this);
        StatusBarUtils.applyHeaderContentInsets(this, binding.header.getRoot(), binding.getRoot());

        binding.header.tvHeaderTitle.setText("Yêu thích");
        binding.header.btnBack.setOnClickListener(v -> finish());

        adapter = new ProductAdapter();
        binding.rvWishlist.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvWishlist.setAdapter(adapter);

        binding.emptyState.tvEmptyText.setText("Danh sách yêu thích của bạn đang trống");
        binding.emptyState.ivEmptyIcon.setImageResource(R.drawable.ic_heart);

        loadWishlist();
    }

    private void loadWishlist() {
        String userOid = Session.getUserOid(this);
        if (userOid == null) {
            binding.emptyState.getRoot().setVisibility(View.VISIBLE);
            return;
        }
        ApiClient.get().getWishlist(userOid).enqueue(new Callback<List<ApiProduct>>() {
            @Override
            public void onResponse(Call<List<ApiProduct>> call, Response<List<ApiProduct>> resp) {
                if (binding == null) return;
                List<Product> products = new ArrayList<>();
                if (resp.isSuccessful() && resp.body() != null) {
                    for (ApiProduct a : resp.body()) products.add(ProductMapper.toProduct(a));
                }
                adapter.setProducts(products);
                binding.emptyState.getRoot().setVisibility(products.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onFailure(Call<List<ApiProduct>> call, Throwable t) {
                if (binding == null) return;
                binding.emptyState.getRoot().setVisibility(View.VISIBLE);
            }
        });
    }
}
