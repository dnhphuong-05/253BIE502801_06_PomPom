package com.pompom.group6.adapters;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.graphics.Paint;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.pompom.group6.R;
import com.pompom.group6.activities.ProductDetailActivity;
import com.pompom.group6.fragments.ProductOptionsBottomSheetDialog;
import com.pompom.group6.models.Product;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class ProductAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    // isHorizontal=false → 2-col grid → item_product_horizontal (VIEW_TYPE_VERTICAL)
    // isHorizontal=true  → 1-col list → item_product_vertical   (VIEW_TYPE_HORIZONTAL)
    // useGridCarousel=true → carousel cuộn ngang, card dài cố định bề rộng → item_product_grid_carousel (VIEW_TYPE_GRID_CAROUSEL)
    private static final int VIEW_TYPE_VERTICAL      = 0; // 2-col grid
    private static final int VIEW_TYPE_HORIZONTAL    = 1; // 1-col list
    private static final int VIEW_TYPE_LOADING       = 2;
    private static final int VIEW_TYPE_GRID_CAROUSEL = 3; // carousel ngang, card dài như lưới Shop

    private final List<Product> products = new ArrayList<>();
    private final Set<String> wishlistedIds = new HashSet<>();
    private boolean isLoading = false;
    private boolean isHorizontal = false;
    private boolean useGridCarousel = false;

    public ProductAdapter() {}

    public ProductAdapter(List<Product> products) {
        this.products.addAll(products);
    }

    // ── Data helpers ──────────────────────────────────────────────────────

    public void setHorizontal(boolean horizontal) {
        this.isHorizontal = horizontal;
    }

    /** Card dài (giống lưới 2 cột ở Shop) nhưng rộng cố định, dùng cho carousel cuộn ngang trên Home. */
    public void setUseGridCarousel(boolean useGridCarousel) {
        this.useGridCarousel = useGridCarousel;
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

    // ── ViewType ──────────────────────────────────────────────────────────

    @Override
    public int getItemViewType(int position) {
        if (position == products.size()) return VIEW_TYPE_LOADING;
        if (useGridCarousel) return VIEW_TYPE_GRID_CAROUSEL;
        return isHorizontal ? VIEW_TYPE_HORIZONTAL : VIEW_TYPE_VERTICAL;
    }

    // ── Create / Bind ─────────────────────────────────────────────────────

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inf = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_LOADING) {
            View v = inf.inflate(R.layout.item_loading, parent, false);
            return new LoadingViewHolder(v);
        } else if (viewType == VIEW_TYPE_HORIZONTAL) {
            // 1-col row card
            View v = inf.inflate(R.layout.item_product_vertical, parent, false);
            return new ProductViewHolder(v);
        } else if (viewType == VIEW_TYPE_GRID_CAROUSEL) {
            // Card dài, rộng cố định — carousel cuộn ngang (VD: Sản phẩm bán chạy ở Home)
            View v = inf.inflate(R.layout.item_product_grid_carousel, parent, false);
            return new ProductViewHolder(v);
        } else {
            // 2-col grid card
            View v = inf.inflate(R.layout.item_product_horizontal, parent, false);
            return new ProductViewHolder(v);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (!(holder instanceof ProductViewHolder)) return;
        ProductViewHolder h = (ProductViewHolder) holder;
        Product product = products.get(position);

        // Title
        h.tvTitle.setText(product.getTitle());

        // Price (formatted with dot-separated thousands + smaller "đ")
        h.tvPrice.setText(formatPriceSpan(product.getPrice()));

        // Original price (struck through, shown only when discount present)
        if (h.tvOriginalPrice != null) {
            if (product.getOriginalPrice() != null && !product.getOriginalPrice().isEmpty()) {
                h.tvOriginalPrice.setVisibility(View.VISIBLE);
                h.tvOriginalPrice.setText(formatPriceSpan(product.getOriginalPrice()));
                h.tvOriginalPrice.setPaintFlags(
                        h.tvOriginalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            } else {
                h.tvOriginalPrice.setVisibility(View.GONE);
            }
        }

        // Discount badge
        if (h.tvDiscountBadge != null) {
            if (product.getDiscountPercent() > 0) {
                h.tvDiscountBadge.setVisibility(View.VISIBLE);
                h.tvDiscountBadge.setText(
                        String.format(Locale.US, "-%d%%", product.getDiscountPercent()));
            } else {
                h.tvDiscountBadge.setVisibility(View.GONE);
            }
        }

        // Rating bar
        if (h.ratingBar != null) {
            h.ratingBar.setRating(product.getRating());
        }

        // Rating text (just the number, e.g. "4.7")
        if (h.tvRatingText != null) {
            h.tvRatingText.setText(String.format(Locale.US, "%.1f", product.getRating()));
        }

        // Review count (e.g. "(23 đánh giá)")
        if (h.tvReviewCount != null) {
            h.tvReviewCount.setText(
                    String.format(Locale.US, "(%d đánh giá)", product.getReviewCount()));
        }

        // Sales volume (mock, deterministic per product id)
        if (h.tvSalesVolume != null) {
            int fakeSales = 10 + ((product.seed() * 31) % 190);
            h.tvSalesVolume.setText(fakeSales + "+ đã bán");
        }

        // Product image
        Glide.with(holder.itemView.getContext())
                .load(product.getImageUrl())
                .placeholder(R.drawable.logo_pompom)
                .into(h.ivImage);

        // Wishlist heart
        bindWishlistHeart(h, product);

        // Item click → ProductDetailActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
            intent.putExtra("product_id", product.getId());
            v.getContext().startActivity(intent);
        });

        // Add-to-cart button — opens BottomSheet for variant/qty selection (An toàn không crash)
        if (h.btnAddToCart != null) {
            h.btnAddToCart.setOnClickListener(v -> {
                Context context = v.getContext();

                // Vòng lặp giải bọc ContextWrapper để tìm FragmentActivity gốc tránh crash
                while (context instanceof ContextWrapper) {
                    if (context instanceof FragmentActivity) {
                        break;
                    }
                    context = ((ContextWrapper) context).getBaseContext();
                }

                if (context instanceof FragmentActivity) {
                    FragmentManager fm = ((FragmentActivity) context).getSupportFragmentManager();
                    ProductOptionsBottomSheetDialog.show(fm,
                            product.getId(),
                            product.getTitle(),
                            product.getPrice(),
                            product.getImageUrl(),
                            ProductOptionsBottomSheetDialog.ACTION_ADD_TO_CART);
                } else {
                    Toast.makeText(v.getContext(), "Không thể mở tùy chọn sản phẩm", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return products.size() + (isLoading ? 1 : 0);
    }

    // ── Heart toggle ──────────────────────────────────────────────────────

    private void bindWishlistHeart(ProductViewHolder h, Product product) {
        if (h.ivWishlistHeart == null || h.cardWishlist == null) return;

        boolean isWishlisted = wishlistedIds.contains(product.getId());
        h.ivWishlistHeart.setImageResource(
                isWishlisted ? R.drawable.ic_heart_solid : R.drawable.ic_heart_outline);

        h.cardWishlist.setOnClickListener(v -> {
            boolean nowWishlisted = !wishlistedIds.contains(product.getId());
            if (nowWishlisted) {
                wishlistedIds.add(product.getId());
            } else {
                wishlistedIds.remove(product.getId());
            }
            // Bounce animation
            Animation anim = AnimationUtils.loadAnimation(v.getContext(), R.anim.heart_scale);
            h.ivWishlistHeart.startAnimation(anim);
            h.ivWishlistHeart.setImageResource(
                    nowWishlisted ? R.drawable.ic_heart_solid : R.drawable.ic_heart_outline);
            Toast.makeText(v.getContext(),
                    nowWishlisted ? "Đã thêm vào yêu thích" : "Đã xóa khỏi yêu thích",
                    Toast.LENGTH_SHORT).show();
        });
    }

    // ── Price formatter ───────────────────────────────────────────────────

    /**
     * Converts a price string like "125000đ" or "125.000đ" into a SpannableString
     * where the number uses dot-separated thousands and the "đ" suffix is smaller (0.65×).
     */
    private SpannableString formatPriceSpan(String rawPrice) {
        if (rawPrice == null || rawPrice.isEmpty()) return new SpannableString("0đ");

        // Strip everything except digits
        String digits = rawPrice.replaceAll("[^0-9]", "");
        long value;
        try {
            value = Long.parseLong(digits);
        } catch (NumberFormatException e) {
            return new SpannableString(rawPrice);
        }

        // Format with dot-thousands, e.g. 125.000
        String formatted = String.format(Locale.US, "%,d", value).replace(",", ".");
        String full = formatted + "đ";

        SpannableString span = new SpannableString(full);
        // Make "đ" smaller
        int suffixStart = full.length() - 1;
        span.setSpan(new RelativeSizeSpan(0.65f), suffixStart, full.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        return span;
    }

    // ── ViewHolders ───────────────────────────────────────────────────────

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle, tvPrice, tvOriginalPrice, tvDiscountBadge, tvRatingText, tvReviewCount, tvSalesVolume;
        RatingBar ratingBar;
        View btnAddToCart;
        ImageView ivWishlistHeart;
        MaterialCardView cardWishlist;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage           = itemView.findViewById(R.id.ivProductImage);
            tvTitle           = itemView.findViewById(R.id.tvProductTitle);
            tvPrice           = itemView.findViewById(R.id.tvProductPrice);
            tvOriginalPrice   = itemView.findViewById(R.id.tvOriginalPrice);
            tvDiscountBadge   = itemView.findViewById(R.id.tvDiscountBadge);
            tvRatingText      = itemView.findViewById(R.id.tvRatingText);
            tvReviewCount     = itemView.findViewById(R.id.tvReviewCount);
            tvSalesVolume     = itemView.findViewById(R.id.tvSalesVolume);
            ratingBar         = itemView.findViewById(R.id.ratingBar);
            btnAddToCart      = itemView.findViewById(R.id.btnAddToCart);
            ivWishlistHeart   = itemView.findViewById(R.id.ivWishlistHeart);
            cardWishlist      = itemView.findViewById(R.id.cardWishlist);
        }
    }

    static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}