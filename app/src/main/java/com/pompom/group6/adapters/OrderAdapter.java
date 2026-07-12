package com.pompom.group6.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pompom.group6.R;
import com.pompom.group6.databinding.ItemOrderBinding;
import com.pompom.group6.models.Order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.VH> {

    /** Callback cho các nút hành động theo trạng thái (Đánh giá / Mua lại). */
    public interface OnOrderActionListener {
        void onReview(Order order);
        void onRebuy(Order order);
        void onCancel(Order order);
    }

    private final List<Order> orders = new ArrayList<>();
    private OnOrderActionListener actionListener;

    public OrderAdapter() {
    }

    public OrderAdapter(List<Order> orders) {
        if (orders != null) this.orders.addAll(orders);
    }

    public void setOnOrderActionListener(OnOrderActionListener listener) {
        this.actionListener = listener;
    }

    /** Thay toàn bộ dữ liệu và vẽ lại danh sách. */
    public void setOrders(List<Order> newOrders) {
        orders.clear();
        if (newOrders != null) orders.addAll(newOrders);
        notifyDataSetChanged();
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
        applyStatusChip(holder, o.getStatus());
        applyActionRow(holder, o);

        Glide.with(holder.itemView.getContext())
                .load(o.getFirstItemImage())
                .placeholder(R.drawable.logo_pompom)
                .into(holder.b.ivOrderProduct);

        // Bấm vào đơn -> mở màn chi tiết + theo dõi tiến độ (chỉ khi có id từ backend).
        holder.itemView.setOnClickListener(v -> {
            if (o.getOid() == null) return;
            com.pompom.group6.activities.OrderDetailActivity.start(v.getContext(), o.getOid());
        });
    }

    /** Màu chip trạng thái: xanh=đã giao/hoàn thành, xám=huỷ, đỏ=trả hàng, hồng=còn lại. */
    private void applyStatusChip(VH holder, String status) {
        int bg, textColor;
        if (isIn(status, "delivered", "completed")) {
            bg = R.drawable.bg_label_green; textColor = R.color.status_green;
        } else if (isIn(status, "cancelled")) {
            bg = R.drawable.bg_label_grey; textColor = R.color.text_secondary;
        } else if (isIn(status, "returned", "refunded")) {
            bg = R.drawable.bg_label_red; textColor = R.color.status_red;
        } else {
            bg = R.drawable.bg_label_pink; textColor = R.color.white;
        }
        holder.b.tvOrderStatus.setBackgroundResource(bg);
        holder.b.tvOrderStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), textColor));
    }

    /** Hàng nút hành động: Chờ xác nhận/lấy hàng -> [Huỷ đơn]; Đã giao -> [Đánh giá?]+[Mua lại];
     * Đã huỷ -> [Xem chi tiết đơn huỷ]+[Mua lại]; còn lại -> ẩn. */
    private void applyActionRow(VH holder, Order o) {
        boolean delivered = isIn(o.getStatus(), "delivered", "completed");
        boolean cancelled = isIn(o.getStatus(), "cancelled");
        boolean cancellable = isIn(o.getStatus(), "pending", "confirmed", "processing");

        if (cancellable) {
            holder.b.actionRow.setVisibility(android.view.View.VISIBLE);
            holder.b.btnActionPrimary.setVisibility(android.view.View.GONE);
            holder.b.btnActionSecondary.setVisibility(android.view.View.VISIBLE);
            holder.b.btnActionSecondary.setText("Huỷ đơn");
            holder.b.btnActionSecondary.setStrokeColorResource(R.color.status_red);
            holder.b.btnActionSecondary.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_red));
            holder.b.btnActionSecondary.setOnClickListener(v -> {
                if (actionListener != null) actionListener.onCancel(o);
            });
            return;
        }

        if (!delivered && !cancelled) {
            holder.b.actionRow.setVisibility(android.view.View.GONE);
            return;
        }
        holder.b.actionRow.setVisibility(android.view.View.VISIBLE);
        holder.b.btnActionPrimary.setVisibility(android.view.View.VISIBLE);

        // Nút phải: "Mua lại" luôn hiện ở cả 2 trạng thái.
        holder.b.btnActionPrimary.setText("Mua lại");
        holder.b.btnActionPrimary.setStrokeColorResource(R.color.brand_pink);
        holder.b.btnActionPrimary.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.brand_pink));
        holder.b.btnActionPrimary.setOnClickListener(v -> {
            if (actionListener != null) actionListener.onRebuy(o);
        });

        if (delivered) {
            boolean showReview = !o.isReviewed();
            holder.b.btnActionSecondary.setVisibility(showReview ? android.view.View.VISIBLE : android.view.View.GONE);
            if (showReview) {
                holder.b.btnActionSecondary.setText("Đánh giá");
                holder.b.btnActionSecondary.setStrokeColorResource(R.color.brand_pink);
                holder.b.btnActionSecondary.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.brand_pink));
                holder.b.btnActionSecondary.setOnClickListener(v -> {
                    if (actionListener != null) actionListener.onReview(o);
                });
            }
        } else {
            // cancelled
            holder.b.btnActionSecondary.setVisibility(android.view.View.VISIBLE);
            holder.b.btnActionSecondary.setText("Xem chi tiết đơn huỷ");
            holder.b.btnActionSecondary.setStrokeColorResource(R.color.text_secondary);
            holder.b.btnActionSecondary.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_secondary));
            holder.b.btnActionSecondary.setOnClickListener(v -> {
                if (o.getOid() == null) return;
                com.pompom.group6.activities.OrderDetailActivity.start(v.getContext(), o.getOid());
            });
        }
    }

    private boolean isIn(String status, String... options) {
        if (status == null) return false;
        for (String opt : options) if (opt.equals(status)) return true;
        return false;
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
