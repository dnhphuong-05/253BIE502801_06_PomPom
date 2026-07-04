package com.pompom.group6.adapters;

import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pompom.group6.R;
import com.pompom.group6.models.Order;

import java.util.List;
import java.util.Locale;

public class GuestOrderAdapter extends RecyclerView.Adapter<GuestOrderAdapter.ViewHolder> {

    public interface OnOrderClickListener {
        void onViewDetail(Order order);
    }

    private List<Order> orders;
    private final OnOrderClickListener listener;

    public GuestOrderAdapter(List<Order> orders, OnOrderClickListener listener) {
        this.orders = orders;
        this.listener = listener;
    }

    public void updateOrders(List<Order> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_guest_order, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder h, int position) {
        Order order = orders.get(position);

        // Order number
        h.tvOrderNumber.setText("#" + order.getOrderNumber());

        // Status chip — colour-coded
        String statusLabel = order.getStatusLabel();
        h.tvOrderStatus.setText(statusLabel);
        applyStatusStyle(h.tvOrderStatus, order.getStatus());

        // Product name (first item)
        String name = order.getFirstItemName();
        h.tvProductName.setText(name != null ? name : "Sản phẩm");

        // Product image
        String imgUrl = order.getFirstItemImage();
        if (imgUrl != null && !imgUrl.isEmpty()) {
            try {
                Glide.with(h.itemView.getContext())
                        .load(imgUrl)
                        .placeholder(R.drawable.favicon_pompom)
                        .error(R.drawable.favicon_pompom)
                        .centerCrop()
                        .into(h.ivProductImage);
            } catch (Exception e) {
                h.ivProductImage.setImageResource(R.drawable.favicon_pompom);
            }
        } else {
            h.ivProductImage.setImageResource(R.drawable.favicon_pompom);
        }

        // Estimated delivery date (simple logic based on status + createdAt)
        h.tvDeliveryDate.setText(estimateDelivery(order));

        // Amount
        long amount = (long) order.getFinalAmount();
        h.tvOrderAmount.setText(formatPrice(amount));

        // Detail button
        h.btnViewDetail.setOnClickListener(v -> {
            if (listener != null) listener.onViewDetail(order);
        });
    }

    @Override
    public int getItemCount() {
        return orders == null ? 0 : orders.size();
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private void applyStatusStyle(TextView tv, String status) {
        if (status == null) return;
        switch (status) {
            case "confirmed":
                tv.setBackgroundColor(Color.parseColor("#E3F2FD"));
                tv.setTextColor(Color.parseColor("#1565C0"));
                break;
            case "shipping":
                tv.setBackgroundColor(Color.parseColor("#FFF3E0"));
                tv.setTextColor(Color.parseColor("#E65100"));
                break;
            case "delivered":
            case "completed":
                tv.setBackgroundColor(Color.parseColor("#E8F5E9"));
                tv.setTextColor(Color.parseColor("#2E7D32"));
                break;
            case "cancelled":
                tv.setBackgroundColor(Color.parseColor("#FFEBEE"));
                tv.setTextColor(Color.parseColor("#C62828"));
                break;
            case "returned":
                tv.setBackgroundColor(Color.parseColor("#F3E5F5"));
                tv.setTextColor(Color.parseColor("#6A1B9A"));
                break;
            default:
                // pending / processing — use brand pink
                tv.setBackgroundColor(Color.parseColor("#FCE4E4"));
                tv.setTextColor(Color.parseColor("#D81B60"));
                break;
        }
    }

    private String estimateDelivery(Order order) {
        // Very simple: if delivered/completed show actual; else show "+3 ngày" from createdAt
        String status = order.getStatus();
        if ("delivered".equals(status) || "completed".equals(status)) {
            return "Đã giao hàng";
        }
        if ("cancelled".equals(status)) {
            return "Đơn đã hủy";
        }
        if ("returned".equals(status)) {
            return "Đang hoàn trả";
        }
        // Parse createdAt "YYYY-MM-DD HH:mm:ss" and add 3 days
        try {
            String created = order.getCreatedAt();
            if (created != null && created.length() >= 10) {
                String datePart = created.substring(0, 10); // "YYYY-MM-DD"
                String[] parts = datePart.split("-");
                int year = Integer.parseInt(parts[0]);
                int month = Integer.parseInt(parts[1]);
                int day = Integer.parseInt(parts[2]) + 3;
                // Simple overflow
                int[] daysInMonth = {0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31};
                if (day > daysInMonth[month]) {
                    day -= daysInMonth[month];
                    month++;
                    if (month > 12) { month = 1; year++; }
                }
                return String.format(Locale.US, "Dự kiến: %02d/%02d/%04d", day, month, year);
            }
        } catch (Exception ignored) {}
        return "Dự kiến: vài ngày tới";
    }

    private SpannableString formatPrice(long amount) {
        String formatted = String.format(Locale.US, "%,d", amount).replace(",", ".");
        String full = formatted + "đ";
        SpannableString span = new SpannableString(full);
        span.setSpan(
                new RelativeSizeSpan(0.7f),
                full.length() - 1,
                full.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );
        return span;
    }

    // ── ViewHolder ────────────────────────────────────────────────────────────

    static class ViewHolder extends RecyclerView.ViewHolder {
        final TextView tvOrderNumber;
        final TextView tvOrderStatus;
        final ImageView ivProductImage;
        final TextView tvProductName;
        final TextView tvDeliveryDate;
        final TextView tvOrderAmount;
        final View btnViewDetail;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderNumber  = itemView.findViewById(R.id.tvOrderNumber);
            tvOrderStatus  = itemView.findViewById(R.id.tvOrderStatus);
            ivProductImage = itemView.findViewById(R.id.ivProductImage);
            tvProductName  = itemView.findViewById(R.id.tvProductName);
            tvDeliveryDate = itemView.findViewById(R.id.tvDeliveryDate);
            tvOrderAmount  = itemView.findViewById(R.id.tvOrderAmount);
            btnViewDetail  = itemView.findViewById(R.id.btnViewDetail);
        }
    }
}
