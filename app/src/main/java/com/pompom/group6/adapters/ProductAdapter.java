package com.pompom.group6.adapters;

import android.content.Intent;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pompom.group6.R;
import com.pompom.group6.activities.ProductDetailActivity;
import com.pompom.group6.models.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_VERTICAL = 0;
    private static final int VIEW_TYPE_HORIZONTAL = 1;
    private static final int VIEW_TYPE_LOADING = 2;

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
        if (position == products.size()) {
            return VIEW_TYPE_LOADING;
        }
        return isHorizontal ? VIEW_TYPE_HORIZONTAL : VIEW_TYPE_VERTICAL;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_LOADING) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(view);
        } else if (viewType == VIEW_TYPE_HORIZONTAL) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product_list, parent, false);
            return new ProductViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_product, parent, false);
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

            // 1. Handle Discount Badge
            if (productHolder.tvDiscountBadge != null) {
                if (product.getDiscountPercent() > 0) {
                    productHolder.tvDiscountBadge.setVisibility(View.VISIBLE);
                    productHolder.tvDiscountBadge.setText(String.format(Locale.getDefault(), "%d%% OFF", product.getDiscountPercent()));
                } else {
                    productHolder.tvDiscountBadge.setVisibility(View.GONE);
                }
            }

            // 2. Handle Original Price (Strike-through)
            if (productHolder.tvOriginalPrice != null) {
                if (product.getOriginalPrice() != null) {
                    productHolder.tvOriginalPrice.setVisibility(View.VISIBLE);
                    productHolder.tvOriginalPrice.setText(product.getOriginalPrice());
                    productHolder.tvOriginalPrice.setPaintFlags(productHolder.tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    productHolder.tvOriginalPrice.setVisibility(View.GONE);
                }
            }

            // 3. Handle Rating Bar (if present in layout)
            if (productHolder.ratingBar != null) {
                productHolder.ratingBar.setRating(product.getRating());
            }

            // 4. Handle Review Count
            if (productHolder.tvReviewCount != null) {
                productHolder.tvReviewCount.setText(String.format(Locale.getDefault(), "%d reviews", product.getReviewCount()));
            } else if (productHolder.tvRatingText != null) {
                // For layouts like item_product_horizontal that use "4.9 (1.2k)" style
                productHolder.tvRatingText.setText(String.format(Locale.getDefault(), "%.1f (%d)", 
                        product.getRating(), product.getReviewCount()));
            }

            // 5. Load Image
            Glide.with(holder.itemView.getContext())
                    .load(product.getImageUrl())
                    .placeholder(R.drawable.logo_pompom)
                    .into(productHolder.ivImage);

            // 6. Click Listeners
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
                intent.putExtra("product_id", product.getId());
                v.getContext().startActivity(intent);
            });

            if (productHolder.btnAddToCart != null) {
                productHolder.btnAddToCart.setOnClickListener(v -> {
                    Toast.makeText(v.getContext(), "Added " + product.getTitle() + " to bag", Toast.LENGTH_SHORT).show();
                });
            }
        }
    }

    @Override
    public int getItemCount() {
        return products.size() + (isLoading ? 1 : 0);
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle, tvPrice, tvOriginalPrice, tvDiscountBadge, tvReviewCount, tvRatingText;
        RatingBar ratingBar;
        View btnAddToCart;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivProductImage);
            tvTitle = itemView.findViewById(R.id.tvProductTitle);
            tvPrice = itemView.findViewById(R.id.tvProductPrice);
            tvOriginalPrice = itemView.findViewById(R.id.tvOriginalPrice);
            tvDiscountBadge = itemView.findViewById(R.id.tvDiscountBadge);
            tvReviewCount = itemView.findViewById(R.id.tvReviewCount);
            tvRatingText = itemView.findViewById(R.id.tvRatingText);
            ratingBar = itemView.findViewById(R.id.ratingBar);
            btnAddToCart = itemView.findViewById(R.id.btnAddToCart);
        }
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
