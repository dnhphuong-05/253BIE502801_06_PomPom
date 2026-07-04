package com.pompom.group6.adapters;

import android.content.res.ColorStateList;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.pompom.group6.R;
import com.pompom.group6.models.Category;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    public interface OnCategoryClickListener {
        void onCategoryClick(int categoryId, boolean isSelected);
    }

    private final List<Category> categories;
    private final Set<Integer> selectedIds = new HashSet<>();
    private OnCategoryClickListener listener;

    /** Constructor without selection listener (backward compat) */
    public CategoryAdapter(List<Category> categories) {
        this.categories = categories;
    }

    /** Constructor with selection listener (MODULE 3 + multi-select) */
    public CategoryAdapter(List<Category> categories, OnCategoryClickListener listener) {
        this.categories = categories;
        this.listener = listener;
    }

    public void setOnCategoryClickListener(OnCategoryClickListener listener) {
        this.listener = listener;
    }

    // ── Selection management ──────────────────────────────────────────────

    public void clearSelection() {
        selectedIds.clear();
        notifyDataSetChanged();
    }

    public void setSelection(Set<Integer> ids) {
        selectedIds.clear();
        if (ids != null) selectedIds.addAll(ids);
        notifyDataSetChanged();
    }

    public Set<Integer> getSelectedIds() {
        return new HashSet<>(selectedIds);
    }

    // ── Adapter overrides ─────────────────────────────────────────────────

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = categories.get(position);
        holder.tvName.setText(category.getName());

        Glide.with(holder.itemView.getContext())
                .load(category.getImageUrl())
                .placeholder(R.drawable.ai_makeup)
                .into(holder.ivIcon);

        boolean isSelected = selectedIds.contains(category.getId());

        // Stroke color: brand_pink (selected) vs pink_light (unselected)
        int strokeColor = isSelected
                ? holder.itemView.getContext().getColor(R.color.brand_pink)
                : holder.itemView.getContext().getColor(R.color.pink_light);
        int strokeWidth = isSelected ? 3 : 2; // dp — card uses px internally via density
        holder.cardCategory.setStrokeColor(ColorStateList.valueOf(strokeColor));
        holder.cardCategory.setStrokeWidth(
                (int) (strokeWidth * holder.itemView.getContext().getResources().getDisplayMetrics().density));

        // Text: bold + pink (selected) vs normal + text_primary (unselected)
        int textColor = isSelected
                ? holder.itemView.getContext().getColor(R.color.brand_pink)
                : holder.itemView.getContext().getColor(R.color.text_primary);
        holder.tvName.setTextColor(textColor);
        holder.tvName.setTypeface(null, isSelected ? Typeface.BOLD : Typeface.NORMAL);

        holder.itemView.setOnClickListener(v -> {
            boolean nowSelected = !selectedIds.contains(category.getId());
            if (nowSelected) {
                selectedIds.add(category.getId());
            } else {
                selectedIds.remove(category.getId());
            }
            notifyItemChanged(position);
            if (listener != null) {
                listener.onCategoryClick(category.getId(), nowSelected);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    // ── ViewHolder ────────────────────────────────────────────────────────

    static class CategoryViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardCategory;
        android.widget.ImageView ivIcon;
        TextView tvName;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            cardCategory = itemView.findViewById(R.id.cardCategory);
            ivIcon = itemView.findViewById(R.id.ivCategory);
            tvName = itemView.findViewById(R.id.tvCategoryName);
        }
    }
}
