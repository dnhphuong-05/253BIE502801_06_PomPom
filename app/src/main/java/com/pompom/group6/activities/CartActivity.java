package com.pompom.group6.activities;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pompom.group6.adapters.CartItemAdapter;
import com.pompom.group6.databinding.ActivityCartBinding;
import com.pompom.group6.models.CartItem;
import com.pompom.group6.utils.CartManager;

import java.util.List;
import java.util.Locale;

public class CartActivity extends AppCompatActivity implements CartManager.CartChangeListener {

    private ActivityCartBinding binding;
    private CartManager cartManager;
    private CartItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCartBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.promoBar.setSelected(true);

        cartManager = CartManager.getInstance(this);
        cartManager.addListener(this);

        setupRecyclerView();
        setupListeners();
        refreshUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cartManager.removeListener(this);
    }

    private void setupRecyclerView() {
        List<CartItem> items = cartManager.getItems();
        adapter = new CartItemAdapter(items, new CartItemAdapter.CartItemListener() {
            @Override
            public void onQuantityChanged(CartItem item, int newQty) {
                cartManager.updateQuantity(item.getProductId(), newQty);
            }

            @Override
            public void onRemove(CartItem item) {
                cartManager.removeItem(item.getProductId());
            }
        });

        binding.rvCartItems.setLayoutManager(new LinearLayoutManager(this));
        binding.rvCartItems.setAdapter(adapter);
    }

    private void setupListeners() {
        // MODULE 1: Back arrow → finish()
        binding.ivBack.setOnClickListener(v -> finish());

        binding.btnCheckout.setOnClickListener(v -> {
            // Future: Navigate to Checkout screen
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void refreshUI() {
        List<CartItem> items = cartManager.getItems();
        if (items.isEmpty()) {
            binding.rvCartItems.setVisibility(View.GONE);
            binding.layoutEmpty.setVisibility(View.VISIBLE);
            binding.btnCheckout.setText("Giỏ hàng trống");
            binding.btnCheckout.setEnabled(false);
            binding.btnCheckout.setAlpha(0.5f);
            binding.tvTotalPrice.setText("0đ");
        } else {
            binding.rvCartItems.setVisibility(View.VISIBLE);
            binding.layoutEmpty.setVisibility(View.GONE);
            binding.btnCheckout.setEnabled(true);
            binding.btnCheckout.setAlpha(1.0f);
            binding.btnCheckout.setText("THANH TOÁN");

            // Total price in Vietnamese format
            long totalLong = (long) cartManager.getTotalPrice();
            binding.tvTotalPrice.setText(formatPriceSpan(totalLong));
        }
        adapter.notifyDataSetChanged();
    }

    /**
     * Format price as dot-separated thousands with smaller "đ" suffix.
     * e.g. 350000 → "350.000đ" (đ rendered at 0.65× size)
     */
    private SpannableString formatPriceSpan(long value) {
        String formatted = String.format(Locale.US, "%,d", value).replace(",", ".");
        String full = formatted + "đ";
        SpannableString span = new SpannableString(full);
        span.setSpan(new RelativeSizeSpan(0.65f), full.length() - 1, full.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return span;
    }

    @Override
    public void onCartChanged(int count) {
        refreshUI();
    }
}
