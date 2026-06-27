package com.pompom.group6.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.pompom.group6.databinding.ItemVoucherBinding;
import com.pompom.group6.models.Voucher;
import java.util.List;

public class VoucherAdapter extends RecyclerView.Adapter<VoucherAdapter.VoucherViewHolder> {
    private List<Voucher> vouchers;

    public VoucherAdapter(List<Voucher> vouchers) {
        this.vouchers = vouchers;
    }

    @NonNull
    @Override
    public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemVoucherBinding binding = ItemVoucherBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new VoucherViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherViewHolder holder, int position) {
        Voucher voucher = vouchers.get(position);
        holder.binding.tvVoucherCode.setText(voucher.getCode());
        
        if ("percent".equals(voucher.getDiscountType())) {
            holder.binding.tvVoucherValue.setText("Giảm " + (int)voucher.getDiscountValue() + "%");
            holder.binding.tvVoucherTitle.setText("Giảm " + (int)voucher.getDiscountValue() + "% cho đơn từ " + String.format("%.0fđ", voucher.getMinOrderAmount()));
        } else {
            holder.binding.tvVoucherValue.setText("Giảm " + String.format("%.0fđ", voucher.getDiscountValue()));
            holder.binding.tvVoucherTitle.setText("Giảm " + String.format("%.0fđ", voucher.getDiscountValue()) + " cho đơn từ " + String.format("%.0fđ", voucher.getMinOrderAmount()));
        }

        holder.binding.tvExpiry.setText(voucher.getExpiryDate());
        holder.binding.tvRemaining.setText(voucher.getRemainingCount() + " lượt");
    }

    @Override
    public int getItemCount() {
        return vouchers != null ? vouchers.size() : 0;
    }

    static class VoucherViewHolder extends RecyclerView.ViewHolder {
        ItemVoucherBinding binding;

        public VoucherViewHolder(ItemVoucherBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
