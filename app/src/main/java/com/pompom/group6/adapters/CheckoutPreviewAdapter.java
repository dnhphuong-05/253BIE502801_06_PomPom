package com.pompom.group6.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pompom.group6.R;
import com.pompom.group6.models.CartItem;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Danh sách sản phẩm trong khung xem trước đơn hàng ở màn thanh toán.
 * Mỗi dòng có hộp kiểm tick sẵn; bỏ tick sẽ yêu cầu xác nhận xoá qua listener.
 */
public class CheckoutPreviewAdapter extends RecyclerView.Adapter<CheckoutPreviewAdapter.VH> {

    public interface Listener {
        /** Người dùng bỏ tick 1 sản phẩm → host hiển thị popup xác nhận xoá. */
        void onUncheckRequested(CartItem item, int position, View anchor);
    }

    private final List<CartItem> items = new ArrayList<>();
    private final Listener listener;
    private final NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    public CheckoutPreviewAdapter(List<CartItem> data, Listener listener) {
        this.items.addAll(data);
        this.listener = listener;
    }

    public void setItems(List<CartItem> data) {
        items.clear();
        items.addAll(data);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_checkout_preview, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        CartItem item = items.get(position);
        h.tvTitle.setText(item.getTitle());
        h.tvQty.setText("SL: " + item.getQuantity());
        h.tvLinePrice.setText(fmt.format((long) item.getSubtotal()) + "₫");

        Glide.with(h.itemView.getContext())
                .load(item.getImageUrl())
                .placeholder(R.drawable.logo_pompom)
                .error(R.drawable.logo_pompom)
                .into(h.ivThumb);

        // Luôn tick sẵn; chỉ báo host khi người dùng bỏ tick
        h.cbSelect.setOnCheckedChangeListener(null);
        h.cbSelect.setChecked(true);
        h.cbSelect.setOnClickListener(v -> {
            if (!h.cbSelect.isChecked() && listener != null) {
                listener.onUncheckRequested(item, h.getAdapterPosition(), h.itemView);
            }
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        CheckBox cbSelect;
        ImageView ivThumb;
        TextView tvTitle, tvQty, tvLinePrice;

        VH(@NonNull View v) {
            super(v);
            cbSelect = v.findViewById(R.id.cbSelect);
            ivThumb = v.findViewById(R.id.ivThumb);
            tvTitle = v.findViewById(R.id.tvTitle);
            tvQty = v.findViewById(R.id.tvQty);
            tvLinePrice = v.findViewById(R.id.tvLinePrice);
        }
    }
}
