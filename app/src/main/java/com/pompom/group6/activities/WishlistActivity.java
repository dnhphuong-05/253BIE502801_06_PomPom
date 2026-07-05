package com.pompom.group6.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.pompom.group6.R;
import com.pompom.group6.adapters.ProductAdapter;
import com.pompom.group6.database.UserDAO;
import com.pompom.group6.databinding.ActivityWishlistBinding;
import com.pompom.group6.models.Product;
import com.pompom.group6.utils.UiUtils;

import java.util.List;

public class WishlistActivity extends SwipeBackActivity {

    private ActivityWishlistBinding binding;
    private UserDAO userDAO;
    private int userId;
    private ProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWishlistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        UiUtils.applyPinkStatusBar(this);

        binding.header.tvHeaderTitle.setText("Yêu thích");
        binding.header.btnBack.setOnClickListener(v -> finish());

        userDAO = new UserDAO(this);
        SharedPreferences prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        userId = prefs.getInt("user_id", 1);

        adapter = new ProductAdapter();
        binding.rvWishlist.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvWishlist.setAdapter(adapter);

        binding.emptyState.tvEmptyText.setText("Danh sách yêu thích của bạn đang trống");
        binding.emptyState.ivEmptyIcon.setImageResource(R.drawable.ic_heart);

        loadWishlist();
    }

    private void loadWishlist() {
        List<Product> products = userDAO.getWishlistProducts(userId);
        adapter.setProducts(products);
        binding.emptyState.getRoot().setVisibility(products.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
