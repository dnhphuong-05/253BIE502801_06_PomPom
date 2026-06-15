package com.pompom.group6.adapters;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pompom.group6.R;
import com.pompom.group6.models.PromotionProduct;

import java.util.List;
import java.util.Locale;

public class FlashSaleAdapter extends RecyclerView.Adapter<FlashSaleAdapter.ViewHolder> {

    private final List<PromotionProduct> products;

    public FlashSaleAdapter(List<PromotionProduct> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_flash_sale_product, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PromotionProduct product = products.get(position);
        
        holder.tvName.setText(product.getName());
        holder.tvSalePrice.setText(String.format(Locale.getDefault(), "%,.0fđ", product.getSalePrice()));
        holder.tvOriginalPrice.setText(String.format(Locale.getDefault(), "%,.0fđ", product.getOriginalPrice()));
        holder.tvOriginalPrice.setPaintFlags(holder.tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.tvDiscountBadge.setText("-" + product.getDiscountPercent() + "%");
        
        int progress = (int) ((product.getSoldCount() / (double) product.getTotalStock()) * 100);
        holder.pbStock.setProgress(progress);
        holder.tvSoldInfo.setText(String.format(Locale.getDefault(), "Đã bán %d/%d", product.getSoldCount(), product.getTotalStock()));

        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.promotion1)
                    .into(holder.ivImage);
        }
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvName, tvSalePrice, tvOriginalPrice, tvDiscountBadge, tvSoldInfo;
        ProgressBar pbStock;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivProductImage);
            tvName = itemView.findViewById(R.id.tvProductName);
            tvSalePrice = itemView.findViewById(R.id.tvSalePrice);
            tvOriginalPrice = itemView.findViewById(R.id.tvOriginalPrice);
            tvDiscountBadge = itemView.findViewById(R.id.tvDiscountBadge);
            tvSoldInfo = itemView.findViewById(R.id.tvSoldInfo);
            pbStock = itemView.findViewById(R.id.pbStock);
        }
    }
}