package com.pompom.group6.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pompom.group6.adapters.CartItemAdapter;
import com.pompom.group6.databinding.ActivityCartBinding;
import com.pompom.group6.fragments.VoucherSelectionBottomSheet;
import com.pompom.group6.models.CartItem;
import com.pompom.group6.utils.CartManager;
import com.pompom.group6.utils.StatusBarUtils;

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

        // Edge-to-edge: transparent status bar, then push the header content below
        // the status bar (pink fills the status-bar area → synced with the header)
        // and keep the checkout footer above the navigation bar.
        StatusBarUtils.applyTransparent(this);
        ViewCompat.setOnApplyWindowInsetsListener(binding.headerBar, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), bars.top, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });
        ViewCompat.setOnApplyWindowInsetsListener(binding.footerContainer, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), v.getPaddingTop(), v.getPaddingRight(), bars.bottom);
            return insets;
        });

        cartManager = CartManager.getInstance(this);
        cartManager.addListener(this);

        // ── Marquee: must call setSelected in Java, never in XML ──────────────────
        binding.tvPromoMarquee.setSelected(true);

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
        // Back arrow
        binding.ivBack.setOnClickListener(v -> finish());

        // ── Voucher row → open VoucherSelectionBottomSheet ────────────────────────
        binding.layoutVoucher.setOnClickListener(v ->
                VoucherSelectionBottomSheet.show(getSupportFragmentManager(),
                        voucherCode -> {
                            // Update the voucher hint label with the selected code
                            if (voucherCode != null && !voucherCode.isEmpty()) {
                                binding.tvSelectedVoucher.setText(voucherCode);
                                binding.tvSelectedVoucher.setTextColor(
                                        getResources().getColor(com.pompom.group6.R.color.brand_pink, null));
                            } else {
                                binding.tvSelectedVoucher.setText("Chọn hoặc nhập mã");
                                binding.tvSelectedVoucher.setTextColor(
                                        getResources().getColor(com.pompom.group6.R.color.text_secondary, null));
                            }
                        }));

        // ── Checkout → CheckoutActivity ───────────────────────────────────────────
        binding.btnCheckout.setOnClickListener(v -> {
            Intent intent = new Intent(this, CheckoutActivity.class);
            startActivity(intent);
            overridePendingTransition(
                    com.pompom.group6.R.anim.slide_in_right,
                    com.pompom.group6.R.anim.slide_out_left);
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
