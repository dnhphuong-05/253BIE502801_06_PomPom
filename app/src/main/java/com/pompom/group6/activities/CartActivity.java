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

import com.google.android.material.snackbar.Snackbar;
import com.pompom.group6.adapters.CartItemAdapter;
import com.pompom.group6.databinding.ActivityCartBinding;
import com.pompom.group6.fragments.VoucherSelectionBottomSheet;
import com.pompom.group6.models.CartItem;
import com.pompom.group6.utils.CartManager;
import com.pompom.group6.utils.StatusBarUtils;

import java.util.List;
import java.util.Locale;

public class CartActivity extends SwipeBackActivity implements CartManager.CartChangeListener {

    private ActivityCartBinding binding;
    private CartManager cartManager;
    private CartItemAdapter adapter;
    private boolean suppressSelectAll = false;
    private boolean suppressSync = false;

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

        restoreCartFromServerIfEmpty();
    }

    /**
     * Khôi phục giỏ hàng từ server khi đã đăng nhập và giỏ local đang trống
     * (ví dụ mở app trên máy khác) — để giỏ hàng theo được nhiều thiết bị.
     */
    private void restoreCartFromServerIfEmpty() {
        String userOid = com.pompom.group6.network.Session.getUserOid(this);
        if (userOid == null || !cartManager.isEmpty()) return;

        com.pompom.group6.network.ApiClient.get().getCart(userOid)
                .enqueue(new retrofit2.Callback<com.pompom.group6.network.dto.ApiCart>() {
                    @Override
                    public void onResponse(retrofit2.Call<com.pompom.group6.network.dto.ApiCart> call,
                                           retrofit2.Response<com.pompom.group6.network.dto.ApiCart> resp) {
                        if (binding == null || !resp.isSuccessful() || resp.body() == null || resp.body().items == null) return;
                        List<CartItem> restored = new java.util.ArrayList<>();
                        for (com.pompom.group6.network.dto.ApiCart.ApiCartItem it : resp.body().items) {
                            String priceStr = String.format(Locale.getDefault(), "%.0fđ", it.price);
                            CartItem ci = new CartItem(
                                    it.productId != null ? Math.abs(it.productId.hashCode()) : 0,
                                    it.productName != null ? it.productName : "Sản phẩm",
                                    priceStr, it.thumbnailUrl, it.quantity);
                            ci.setProductOid(it.productId);
                            ci.setVariantId(it.variantId);
                            ci.setVariantName(it.variantName);
                            restored.add(ci);
                        }
                        if (!restored.isEmpty()) {
                            cartManager.replaceAll(restored);
                            setupRecyclerView();
                            refreshUI();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<com.pompom.group6.network.dto.ApiCart> call, Throwable t) {}
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        cartManager.removeListener(this);
    }

    /** Đồng bộ số lượng lên giỏ server (nếu đã đăng nhập & là sản phẩm cloud). */
    private void syncQuantityToServer(CartItem item, int newQty) {
        String userOid = com.pompom.group6.network.Session.getUserOid(this);
        if (userOid == null || item.getProductOid() == null) return;
        com.pompom.group6.network.ApiClient.get()
                .setCartQuantity(new com.pompom.group6.network.dto.CartItemRequest(userOid, item.getProductOid(), newQty))
                .enqueue(new retrofit2.Callback<Void>() {
                    @Override public void onResponse(retrofit2.Call<Void> c, retrofit2.Response<Void> r) {}
                    @Override public void onFailure(retrofit2.Call<Void> c, Throwable t) {}
                });
    }

    /** Đồng bộ việc xoá sản phẩm lên giỏ server. */
    private void syncRemoveToServer(CartItem item) {
        String userOid = com.pompom.group6.network.Session.getUserOid(this);
        if (userOid == null || item.getProductOid() == null) return;
        com.pompom.group6.network.ApiClient.get().removeCartByProduct(userOid, item.getProductOid())
                .enqueue(new retrofit2.Callback<Void>() {
                    @Override public void onResponse(retrofit2.Call<Void> c, retrofit2.Response<Void> r) {}
                    @Override public void onFailure(retrofit2.Call<Void> c, Throwable t) {}
                });
    }

    private void setupRecyclerView() {
        List<CartItem> items = cartManager.getItems();
        adapter = new CartItemAdapter(items, new CartItemAdapter.CartItemListener() {
            @Override
            public void onQuantityChanged(CartItem item, int newQty) {
                cartManager.updateQuantity(item.getProductId(), newQty);
                syncQuantityToServer(item, newQty);
            }

            @Override
            public void onRemove(CartItem item) {
                cartManager.removeItem(item.getProductId());
                syncRemoveToServer(item);
            }
        });
        adapter.setSelectionEnabled(true);
        adapter.setSelectionListener(this::onSelectionChanged);

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

        // ── Multi-delete ──────────────────────────────────────────────────────────
        binding.ivDeleteSelected.setOnClickListener(v -> multiDelete());
        binding.cbSelectAll.setOnCheckedChangeListener((b, checked) -> {
            if (suppressSelectAll) return;
            if (checked) adapter.selectAll();
            else adapter.clearSelection();
        });
    }

    // ── Selection / multi-delete ──────────────────────────────────────────────────

    private void onSelectionChanged(int count) {
        binding.ivDeleteSelected.setVisibility(count > 0 ? View.VISIBLE : View.GONE);
        binding.tvSelectedCount.setText("Đã chọn " + count);
        suppressSelectAll = true;
        binding.cbSelectAll.setChecked(adapter.isAllSelected());
        suppressSelectAll = false;
    }

    private void multiDelete() {
        List<CartItemAdapter.RemovedEntry> removed = adapter.removeSelected();
        if (removed.isEmpty()) return;
        updateTotalsAndEmpty();

        Snackbar sb = Snackbar.make(binding.getRoot(),
                "Đã xóa " + removed.size() + " sản phẩm", Snackbar.LENGTH_LONG);
        sb.setAnchorView(binding.footerContainer);
        sb.setActionTextColor(getResources().getColor(com.pompom.group6.R.color.brand_pink, null));
        sb.setAction("HOÀN TÁC", v -> {
            adapter.restore(removed);
            updateTotalsAndEmpty();
        });
        sb.addCallback(new Snackbar.Callback() {
            @Override
            public void onDismissed(Snackbar transientBottomBar, int event) {
                if (event != DISMISS_EVENT_ACTION) {
                    // Không hoàn tác → xoá vĩnh viễn khỏi CartManager
                    suppressSync = true;
                    for (CartItemAdapter.RemovedEntry e : removed) {
                        cartManager.removeItem(e.item.getProductId());
                        syncRemoveToServer(e.item);
                    }
                    suppressSync = false;
                    refreshUI();
                }
            }
        });
        sb.show();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    private void refreshUI() {
        // Đồng bộ dữ liệu hiển thị từ CartManager (fix: line không biến mất sau khi xoá)
        adapter.setItems(cartManager.getItems());
        updateTotalsAndEmpty();
    }

    /** Tính tổng tiền + trạng thái rỗng dựa trên DANH SÁCH ĐANG HIỂN THỊ (adapter). */
    private void updateTotalsAndEmpty() {
        List<CartItem> items = adapter.getItems();
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

            long total = 0;
            for (CartItem it : items) total += (long) it.getSubtotal();
            binding.tvTotalPrice.setText(formatPriceSpan(total));
        }
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
        if (suppressSync) return;
        refreshUI();
    }
}
