package com.pompom.group6.adapters;

import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pompom.group6.R;
import com.pompom.group6.activities.ProductDetailActivity;
import com.pompom.group6.models.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Compact horizontal list adapter used by the search screen.
 * Shows only image + discount badge + name + current/original price.
 */
public class SearchProductAdapter extends RecyclerView.Adapter<SearchProductAdapter.SearchViewHolder> {

    private final List<Product> products = new ArrayList<>();

    public void setProducts(List<Product> newProducts) {
        products.clear();
        products.addAll(newProducts);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_search, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        Product product = products.get(position);

        holder.tvTitle.setText(product.getTitle());
        holder.tvPrice.setText(product.getPrice());

        if (product.getDiscountPercent() > 0) {
            holder.tvDiscountBadge.setVisibility(View.VISIBLE);
            holder.tvDiscountBadge.setText(String.format(Locale.getDefault(), "%d%% OFF", product.getDiscountPercent()));
        } else {
            holder.tvDiscountBadge.setVisibility(View.GONE);
        }

        if (product.getOriginalPrice() != null) {
            holder.tvOriginalPrice.setVisibility(View.VISIBLE);
            holder.tvOriginalPrice.setText(product.getOriginalPrice());
            holder.tvOriginalPrice.setPaintFlags(holder.tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.tvOriginalPrice.setVisibility(View.GONE);
        }

        Glide.with(holder.itemView.getContext())
                .load(product.getImageUrl())
                .placeholder(R.drawable.logo_pompom)
                .into(holder.ivImage);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
            intent.putExtra("product_id", product.getId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class SearchViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle, tvPrice, tvOriginalPrice, tvDiscountBadge;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivProductImage);
            tvTitle = itemView.findViewById(R.id.tvProductTitle);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            tvOriginalPrice = itemView.findViewById(R.id.tvOriginalPrice);
            tvDiscountBadge = itemView.findViewById(R.id.tvDiscountBadge);
        }
    }
}
