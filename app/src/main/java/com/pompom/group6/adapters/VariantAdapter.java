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

    public interface OnVariantSelectedListener {
        void onVariantSelected(ProductVariant variant);
    }

    public VariantAdapter(List<ProductVariant> variants, OnVariantSelectedListener listener) {
        this.variants = variants;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VariantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemColorVariantBinding binding = ItemColorVariantBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new VariantViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VariantViewHolder holder, int position) {
        ProductVariant variant = variants.get(position);
        holder.binding.tvVariantName.setText(variant.getName());

        Glide.with(holder.itemView.getContext())
                .load(variant.getImageUrl())
                .into(holder.binding.ivVariant);

        // Selection highlight
        if (position == selectedPosition) {
            holder.binding.cardColor.setStrokeColor(android.graphics.Color.parseColor("#FF69B4")); // Pink
            holder.binding.cardColor.setStrokeWidth(4);
        } else {
            holder.binding.cardColor.setStrokeColor(android.graphics.Color.parseColor("#F0F0F0"));
            holder.binding.cardColor.setStrokeWidth(2);
        }

        holder.itemView.setOnClickListener(v -> {
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
