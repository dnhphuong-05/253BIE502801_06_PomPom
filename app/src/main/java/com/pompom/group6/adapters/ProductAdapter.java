package com.pompom.group6.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pompom.group6.R;
import com.pompom.group6.models.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {

    private List<Product> productList;
    private boolean isHorizontal = false;

    public ProductAdapter(List<Product> productList) {
        this.productList = productList;
    }

    public void setHorizontal(boolean horizontal) {
        this.isHorizontal = horizontal;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutId = isHorizontal ? R.layout.item_product_horizontal : R.layout.item_product;
        View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
        return new ProductViewHolder(view);
    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle, tvPrice, tvRanking, tvRatingText;
        View layoutRanking;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivProductImage);
            tvTitle = itemView.findViewById(R.id.tvProductTitle);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            tvRanking = itemView.findViewById(R.id.tvRanking);
            tvRatingText = itemView.findViewById(R.id.tvRatingText);
            layoutRanking = itemView.findViewById(R.id.layoutRanking);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = productList.get(position);
        holder.tvTitle.setText(product.getTitle());
        holder.tvPrice.setText(product.getPrice());
        
        if (product.getImageUrl() != null && !product.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(holder.ivImage);
        } else if (product.getImageResId() != 0) {
            holder.ivImage.setImageResource(product.getImageResId());
        }

        // Show ranking if in horizontal best seller mode
        if (isHorizontal && holder.layoutRanking != null) {
            holder.layoutRanking.setVisibility(View.VISIBLE);
            holder.tvRanking.setText("TOP\n" + (position + 1));
            
            // Mock rating text for design consistency
            if (holder.tvRatingText != null) {
                holder.tvRatingText.setText("4.9 (1.2k)");
            }
        } else if (holder.layoutRanking != null) {
            holder.layoutRanking.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return productList != null ? productList.size() : 0;
    }
}
