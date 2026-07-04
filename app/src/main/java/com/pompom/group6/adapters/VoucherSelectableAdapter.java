package com.pompom.group6.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pompom.group6.databinding.ItemVoucherSelectableBinding;
import com.pompom.group6.models.Voucher;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VoucherSelectableAdapter extends RecyclerView.Adapter<VoucherSelectableAdapter.VoucherViewHolder> {

    private List<Voucher> items;
    private int selectedPosition = RecyclerView.NO_POSITION;

    public VoucherSelectableAdapter(List<Voucher> items) {
        this.items = new ArrayList<>(items);
    }

    public void updateList(List<Voucher> newItems) {
        this.items = new ArrayList<>(newItems);
        selectedPosition = RecyclerView.NO_POSITION;
        notifyDataSetChanged();
    }

    /** Returns the currently selected voucher, or null if none selected. */
    public Voucher getSelectedVoucher() {
        if (selectedPosition >= 0 && selectedPosition < items.size()) {
            return items.get(selectedPosition);
        }
        return null;
    }

    @NonNull
    @Override
    public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemVoucherSelectableBinding binding = ItemVoucherSelectableBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new VoucherViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherViewHolder holder, int position) {
        Voucher voucher = items.get(position);
        boolean isSelected = (position == selectedPosition);

        // ── Bind fields from existing Voucher model ──────────────────────────────
        holder.binding.tvVoucherCode.setText(voucher.getCode());

        // Build title from discountType + discountValue
        String title;
        if ("percent".equalsIgnoreCase(voucher.getDiscountType())) {
            title = "Giảm " + (int) voucher.getDiscountValue() + "% cho đơn từ "
                    + formatAmount(voucher.getMinOrderAmount());
        } else {
            title = "Giảm " + formatAmount(voucher.getDiscountValue()) + " cho đơn từ "
                    + formatAmount(voucher.getMinOrderAmount());
        }
        holder.binding.tvVoucherTitle.setText(title);

        // Discount label (right panel)
        String label = "percent".equalsIgnoreCase(voucher.getDiscountType())
                ? "Giảm " + (int) voucher.getDiscountValue() + "%"
                : "Giảm " + formatAmount(voucher.getDiscountValue());
        holder.binding.tvVoucherValue.setText(label);

        // Max discount – use discountValue as cap proxy
        holder.binding.tvMaxDiscount.setText("Giảm tối đa " + formatAmount(voucher.getDiscountValue()));

        holder.binding.tvExpiry.setText("HSD: " + voucher.getExpiryDate());
        holder.binding.tvRemaining.setText("Còn " + voucher.getRemainingCount() + " lượt");

        // ── Selection highlight ───────────────────────────────────────────────────
        if (isSelected) {
            holder.binding.getRoot().setStrokeColor(Color.parseColor("#FF69B4"));
            holder.binding.getRoot().setStrokeWidth(dpToPx(holder, 2));
            holder.binding.tvVoucherCode.setTextColor(Color.parseColor("#FF69B4"));
        } else {
            holder.binding.getRoot().setStrokeColor(Color.parseColor("#FCE4E4"));
            holder.binding.getRoot().setStrokeWidth(dpToPx(holder, 1));
            holder.binding.tvVoucherCode.setTextColor(Color.parseColor("#E91E63"));
        }

        // ── Single-select on click ────────────────────────────────────────────────
        holder.itemView.setOnClickListener(v -> {
            int oldPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            if (oldPosition != RecyclerView.NO_POSITION) notifyItemChanged(oldPosition);
            notifyItemChanged(selectedPosition);
        });
    }

    private String formatAmount(double amount) {
        return String.format(Locale.US, "%,.0f", amount).replace(",", ".") + "đ";
    }

    private int dpToPx(VoucherViewHolder holder, int dp) {
        float density = holder.itemView.getContext().getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class VoucherViewHolder extends RecyclerView.ViewHolder {
        final ItemVoucherSelectableBinding binding;

        VoucherViewHolder(ItemVoucherSelectableBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
