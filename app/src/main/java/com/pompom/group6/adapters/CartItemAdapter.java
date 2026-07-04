package com.pompom.group6.adapters;

import android.content.Context;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pompom.group6.databinding.ItemProductCartBinding;
import com.pompom.group6.models.CartItem;

import java.util.List;
import java.util.Locale;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartViewHolder> {

    public interface CartItemListener {
        void onQuantityChanged(CartItem item, int newQty);
        void onRemove(CartItem item);
    }

    private final List<CartItem> items;
    private final CartItemListener listener;

    public CartItemAdapter(List<CartItem> items, CartItemListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemProductCartBinding binding = ItemProductCartBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CartViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = items.get(position);
        Context ctx = holder.itemView.getContext();

        // ── Image ────────────────────────────────────────────────────────────────
        Glide.with(ctx)
                .load(item.getImageUrl())
                .placeholder(android.R.color.transparent)
                .error(com.pompom.group6.R.drawable.favicon_pompom)
                .into(holder.binding.ivCartProduct);

        // ── Title ────────────────────────────────────────────────────────────────
        holder.binding.tvCartTitle.setText(item.getTitle());

        // ── Variant label ────────────────────────────────────────────────────────
        holder.binding.tvCartVariant.setText("Màu sắc: Mặc định");

        // ── Quantity ─────────────────────────────────────────────────────────────
        holder.binding.tvCartQuantity.setText(String.valueOf(item.getQuantity()));

        // ── Dynamic price (unit × qty) ────────────────────────────────────────────
        updatePrice(holder, item);

        // ── Minus button ─────────────────────────────────────────────────────────
        holder.binding.btnCartMinus.setOnClickListener(v -> {
            int newQty = item.getQuantity() - 1;
            if (newQty <= 0) {
                confirmDelete(ctx, item);
            } else {
                if (listener != null) listener.onQuantityChanged(item, newQty);
            }
        });

        // ── Plus button ──────────────────────────────────────────────────────────
        holder.binding.btnCartPlus.setOnClickListener(v -> {
            int newQty = item.getQuantity() + 1;
            if (listener != null) listener.onQuantityChanged(item, newQty);
        });

        // ── Delete button ────────────────────────────────────────────────────────
        holder.binding.btnDeleteItem.setOnClickListener(v -> confirmDelete(ctx, item));
    }

    // ── Helpers ───────────────────────────────────────────────────────────────────

    private void updatePrice(@NonNull CartViewHolder holder, CartItem item) {
        long unitPrice = parsePriceLong(item.getPrice());
        long total = unitPrice * item.getQuantity();
        holder.binding.tvCartItemPrice.setText(formatPriceSpan(total));
    }

    private long parsePriceLong(String priceStr) {
        if (priceStr == null) return 0;
        String digits = priceStr.replaceAll("[^0-9]", "");
        if (digits.isEmpty()) return 0;
        try { return Long.parseLong(digits); } catch (NumberFormatException e) { return 0; }
    }

    private SpannableString formatPriceSpan(long value) {
        String formatted = String.format(Locale.US, "%,d", value).replace(",", ".");
        String full = formatted + "đ";
        SpannableString span = new SpannableString(full);
        span.setSpan(new RelativeSizeSpan(0.65f), full.length() - 1, full.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return span;
    }

    private void confirmDelete(Context ctx, CartItem item) {
        new AlertDialog.Builder(ctx)
                .setTitle("Xác nhận xóa")
                .setMessage("Xác nhận xóa sản phẩm này khỏi giỏ hàng?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    if (listener != null) listener.onRemove(item);
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    // ── ViewHolder ────────────────────────────────────────────────────────────────

    static class CartViewHolder extends RecyclerView.ViewHolder {
        final ItemProductCartBinding binding;

        CartViewHolder(ItemProductCartBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
