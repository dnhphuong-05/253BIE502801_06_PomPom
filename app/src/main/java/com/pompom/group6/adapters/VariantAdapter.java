package com.pompom.group6.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pompom.group6.databinding.ItemColorVariantBinding;
import com.pompom.group6.models.ProductVariant;

import java.util.List;

public class VariantAdapter extends RecyclerView.Adapter<VariantAdapter.VariantViewHolder> {

    private List<ProductVariant> variants;
    private int selectedPosition = 0;
    private OnVariantSelectedListener listener;
    private boolean isDisplayOnly = false; // Cờ kiểm tra chế độ chỉ hiển thị

    public interface OnVariantSelectedListener {
        void onVariantSelected(ProductVariant variant);
    }

    // Constructor gốc dùng cho Bottom Sheet (Cần chọn biến thể)
    public VariantAdapter(List<ProductVariant> variants, OnVariantSelectedListener listener) {
        this.variants = variants;
        this.listener = listener;
        this.isDisplayOnly = false;
    }

    // CONSTRUCTOR MỚI: Dùng cho ProductDetailActivity (fix lỗi dòng 282 bị đỏ)
    public VariantAdapter(List<ProductVariant> variants, boolean isDisplayOnly) {
        this.variants = variants;
        this.isDisplayOnly = isDisplayOnly;
        // Nếu chỉ hiển thị thì mặc định không chọn item nào hết
        this.selectedPosition = -1;
    }

    @NonNull
    @Override
    public VariantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemColorVariantBinding binding = ItemColorVariantBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new VariantViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VariantViewHolder holder, int position) {
        ProductVariant variant = variants.get(position);
        holder.binding.tvVariantName.setText(variant.getName());

        Glide.with(holder.itemView.getContext())
                .load(variant.getImageUrl())
                .placeholder(android.R.color.transparent)
                .into(holder.binding.ivVariant);

        // ── Chế độ chỉ hiển thị (Display Only) ở màn hình chi tiết ──
        if (isDisplayOnly) {
            // Định dạng cố định trạng thái bình thường, không highlight, không scale
            holder.binding.cardColor.setStrokeColor(android.graphics.Color.parseColor("#F0F0F0"));
            holder.binding.cardColor.setStrokeWidth(2);
            holder.itemView.setScaleX(1.0f);
            holder.itemView.setScaleY(1.0f);

            // Vô hiệu hóa sự kiện click luôn, không cho bấm
            holder.itemView.setOnClickListener(null);
            return;
        }

        // ── Trạng thái tương tác: màu chọn viền dày + rõ, màu khác làm mờ ──
        if (position == selectedPosition) {
            holder.binding.cardColor.setStrokeColor(android.graphics.Color.parseColor("#E8989A"));
            holder.binding.cardColor.setStrokeWidth(6);
            holder.itemView.setAlpha(1.0f);
            holder.itemView.setScaleX(1.08f);
            holder.itemView.setScaleY(1.08f);
        } else {
            holder.binding.cardColor.setStrokeColor(android.graphics.Color.parseColor("#F0F0F0"));
            holder.binding.cardColor.setStrokeWidth(2);
            holder.itemView.setAlpha(0.4f);
            holder.itemView.setScaleX(1.0f);
            holder.itemView.setScaleY(1.0f);
        }

        // Click logic kèm hiệu ứng hoạt họa bounce
        holder.itemView.setOnClickListener(v -> {
            v.animate()
                    .scaleX(1.18f)
                    .scaleY(1.18f)
                    .setDuration(110)
                    .withEndAction(() ->
                            v.animate()
                                    .scaleX(1.08f)
                                    .scaleY(1.08f)
                                    .setDuration(90)
                                    .start())
                    .start();

            int oldPosition = selectedPosition;
            selectedPosition = holder.getAdapterPosition();
            notifyItemChanged(oldPosition);
            notifyItemChanged(selectedPosition);

            if (listener != null) {
                listener.onVariantSelected(variant);
            }
        });
    }

    @Override
    public int getItemCount() {
        return variants != null ? variants.size() : 0;
    }

    static class VariantViewHolder extends RecyclerView.ViewHolder {
        ItemColorVariantBinding binding;

        public VariantViewHolder(ItemColorVariantBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}