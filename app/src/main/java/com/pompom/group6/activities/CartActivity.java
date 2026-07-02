package com.pompom.group6.activities;

import android.os.Bundle;
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
        binding.btnCloseCart.setOnClickListener(v -> closeCart());
        binding.dimOverlay.setOnClickListener(v -> closeCart());
        binding.btnCheckout.setOnClickListener(v -> {
            // Future: Navigate to Checkout
        });
    }

    private void closeCart() {
        finish();
        overridePendingTransition(0, android.R.anim.fade_out);
    }

    @Override
    public void onBackPressed() {
        closeCart();
    }

    private void refreshUI() {
        List<CartItem> items = cartManager.getItems();
        if (items.isEmpty()) {
            binding.rvCartItems.setVisibility(View.GONE);
            binding.layoutEmpty.setVisibility(View.VISIBLE);
            binding.btnCheckout.setText("CART IS EMPTY");
            binding.btnCheckout.setEnabled(false);
            binding.btnCheckout.setAlpha(0.5f);
        } else {
            binding.rvCartItems.setVisibility(View.VISIBLE);
            binding.layoutEmpty.setVisibility(View.GONE);
            binding.btnCheckout.setEnabled(true);
            binding.btnCheckout.setAlpha(1.0f);
            
            double total = cartManager.getTotalPrice();
            binding.btnCheckout.setText(String.format(Locale.getDefault(), "CHECK OUT — $%.2f USD", total));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onCartChanged(int count) {
        refreshUI();
    }
}
