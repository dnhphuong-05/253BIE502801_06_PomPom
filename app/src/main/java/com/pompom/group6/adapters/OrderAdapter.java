package com.pompom.group6.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pompom.group6.R;
import com.pompom.group6.databinding.ItemOrderBinding;
import com.pompom.group6.models.Order;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.VH> {

    private final List<Order> orders;

    public OrderAdapter(List<Order> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemOrderBinding b = ItemOrderBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(b);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Order o = orders.get(position);
        holder.b.tvOrderNumber.setText(o.getOrderNumber());
        holder.b.tvOrderStatus.setText(o.getStatusLabel());
        holder.b.tvProductName.setText(o.getFirstItemName() != null ? o.getFirstItemName() : "Sản phẩm");
        holder.b.tvItemCount.setText(o.getItemCount() + " sản phẩm");
        holder.b.tvOrderDate.setText(formatDate(o.getCreatedAt()));
        holder.b.tvOrderTotal.setText(String.format(Locale.getDefault(), "%,.0fđ", o.getFinalAmount()));

        Glide.with(holder.itemView.getContext())
                .load(o.getFirstItemImage())
                .placeholder(R.drawable.logo_pompom)
                .into(holder.b.ivOrderProduct);
    }

    private String formatDate(String raw) {
        if (raw == null) return "";
        try {
            SimpleDateFormat in = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            Date date = in.parse(raw);
            if (date != null) {
                return new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(date);
            }
        } catch (Exception ignored) {
        }
        return raw.length() >= 10 ? raw.substring(0, 10) : raw;
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final ItemOrderBinding b;

        VH(ItemOrderBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }
}
