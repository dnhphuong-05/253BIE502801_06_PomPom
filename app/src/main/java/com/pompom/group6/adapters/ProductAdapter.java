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
import com.pompom.group6.activities.ProductDetailActivity;
import com.pompom.group6.models.Product;

import java.util.ArrayList;
import java.util.List;
import android.content.Intent;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_ITEM = 0;
    private static final int VIEW_TYPE_LOADING = 1;

    private final List<Product> products = new ArrayList<>();
    private boolean isLoading = false;
    private boolean isHorizontal = false;

    public ProductAdapter() {
    }

    public ProductAdapter(List<Product> products) {
        this.products.addAll(products);
    }

    public void setHorizontal(boolean horizontal) {
        this.isHorizontal = horizontal;
    }

    public void setProducts(List<Product> newProducts) {
        products.clear();
        products.addAll(newProducts);
        notifyDataSetChanged();
    }

    public void addProducts(List<Product> moreProducts) {
        int start = products.size();
        products.addAll(moreProducts);
        notifyItemRangeInserted(start, moreProducts.size());
    }

    public void showLoading() {
        if (!isLoading) {
            isLoading = true;
            notifyItemInserted(products.size());
        }
    }

    public void hideLoading() {
        if (isLoading) {
            isLoading = false;
            notifyItemRemoved(products.size());
        }
    }

    @Override
    public int getItemViewType(int position) {
        return (position == products.size()) ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        } else {
            int layoutId = isHorizontal ? R.layout.item_product_horizontal : R.layout.item_product;
            View view = LayoutInflater.from(parent.getContext()).inflate(layoutId, parent, false);
            return new ProductViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ProductViewHolder) {
            Product product = products.get(position);
            ProductViewHolder productHolder = (ProductViewHolder) holder;
            productHolder.tvTitle.setText(product.getTitle());
            productHolder.tvPrice.setText(product.getPrice());

            Glide.with(holder.itemView.getContext())
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.logo_pompom)
                    .into(productHolder.ivImage);

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
                intent.putExtra("product_id", product.getId());
                v.getContext().startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return products.size() + (isLoading ? 1 : 0);
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle, tvPrice;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivProductImage);
            tvTitle = itemView.findViewById(R.id.tvProductTitle);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
        }
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
