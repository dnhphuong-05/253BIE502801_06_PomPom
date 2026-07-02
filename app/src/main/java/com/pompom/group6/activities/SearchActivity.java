package com.pompom.group6.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pompom.group6.adapters.SearchProductAdapter;
import com.pompom.group6.database.ProductDAO;
import com.pompom.group6.databinding.ActivitySearchBinding;
import com.pompom.group6.models.Product;
import com.pompom.group6.utils.StatusBarUtils;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private ActivitySearchBinding binding;
    private ProductDAO productDAO;
    private SearchProductAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Transparent status bar: this screen's background shows through.
        StatusBarUtils.applyTransparent(this);

        productDAO = new ProductDAO(this);
        adapter = new SearchProductAdapter();
        binding.rvSearchResults.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSearchResults.setAdapter(adapter);

        binding.btnBack.setOnClickListener(v -> finish());

        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                runSearch(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });

        binding.etSearch.requestFocus();
    }

    private void runSearch(String keyword) {
        if (keyword.isEmpty()) {
            adapter.setProducts(new ArrayList<>());
            binding.rvSearchResults.setVisibility(View.GONE);
            binding.tvEmptyState.setText("Nhập từ khóa để tìm sản phẩm");
            binding.tvEmptyState.setVisibility(View.VISIBLE);
            return;
        }

        List<Product> results = productDAO.searchProducts(keyword);
        adapter.setProducts(results);

        if (results.isEmpty()) {
            binding.rvSearchResults.setVisibility(View.GONE);
            binding.tvEmptyState.setText("Không tìm thấy sản phẩm phù hợp");
            binding.tvEmptyState.setVisibility(View.VISIBLE);
        } else {
            binding.rvSearchResults.setVisibility(View.VISIBLE);
            binding.tvEmptyState.setVisibility(View.GONE);
        }
    }
}
