package com.pompom.group6.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.pompom.group6.R;

import java.util.List;

/**
 * Horizontal rail of rounded thumbnails for the product image slider.
 * Tapping a thumbnail selects it (thicker pink border) and notifies the listener
 * so the main ViewPager can switch to the matching page.
 */
public class ThumbnailAdapter extends RecyclerView.Adapter<ThumbnailAdapter.ThumbViewHolder> {

    public interface OnThumbnailClickListener {
        void onThumbnailClick(int position);
    }

    private final List<String> images;
    private final OnThumbnailClickListener listener;
    private int selectedPosition = 0;

    public ThumbnailAdapter(List<String> images, OnThumbnailClickListener listener) {
        this.images = images;
        this.listener = listener;
    }

    /** Highlights the given position (called when the main pager changes). */
    public void setSelectedPosition(int position) {
        if (position == selectedPosition) return;
        int old = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(old);
        notifyItemChanged(selectedPosition);
    }

    @NonNull
    @Override
    public ThumbViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_thumbnail, parent, false);
        return new ThumbViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ThumbViewHolder holder, int position) {
        String url = images.get(position);
        Glide.with(holder.itemView.getContext())
                .load(url != null ? url.trim() : null)
                .placeholder(R.drawable.logo_pompom)
                .error(R.drawable.logo_pompom)
                .into(holder.ivThumb);

        boolean selected = position == selectedPosition;
        holder.cardThumb.setStrokeColor(selected
                ? Color.parseColor("#E8989A") : Color.parseColor("#F0F0F0"));
        holder.cardThumb.setStrokeWidth(selected ? 5 : 1);
        holder.itemView.setAlpha(selected ? 1f : 0.6f);

        holder.itemView.setOnClickListener(v -> {
            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;
            setSelectedPosition(pos);
            if (listener != null) listener.onThumbnailClick(pos);
        });
    }

    @Override
    public int getItemCount() {
        return images != null ? images.size() : 0;
    }

    static class ThumbViewHolder extends RecyclerView.ViewHolder {
        MaterialCardView cardThumb;
        ImageView ivThumb;

        ThumbViewHolder(@NonNull View itemView) {
            super(itemView);
            cardThumb = itemView.findViewById(R.id.cardThumb);
            ivThumb = itemView.findViewById(R.id.ivThumb);
        }
    }
}
