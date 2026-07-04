package com.pompom.group6.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.pompom.group6.R;
import com.pompom.group6.databinding.ItemReviewBinding;
import com.pompom.group6.databinding.ItemReviewImageBinding;
import com.pompom.group6.models.Review;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {
    private List<Review> reviews;

    public ReviewAdapter(List<Review> reviews) {
        this.reviews = reviews;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReviewBinding binding = ItemReviewBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ReviewViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReviewViewHolder holder, int position) {
        Review review = reviews.get(position);
        if (review == null) return;

        holder.binding.tvUserName.setText(formatDisplayName(review.getUserName()));
        holder.binding.reviewRatingBar.setRating(review.getRating());
        holder.binding.tvRatingScore.setText(String.format("%.1f", (float) review.getRating()));
        holder.binding.tvReviewDate.setText(review.getCreatedAt() != null ? review.getCreatedAt() : "");
        holder.binding.tvReviewComment.setText(review.getComment() != null ? review.getComment() : "");

        Glide.with(holder.itemView.getContext())
                .load(review.getUserAvatar())
                .placeholder(R.drawable.ic_avatar)
                .error(R.drawable.ic_avatar)
                .into(holder.binding.ivUserAvatar);

        if (review.getImageUrl() != null && !review.getImageUrl().isEmpty()) {
            String imageUrlString = review.getImageUrl();
            String[] imageArray = imageUrlString.split(",");
            List<String> imageUrls = new ArrayList<>();
            for (String url : imageArray) {
                String trimmedUrl = url.trim();
                if (!trimmedUrl.isEmpty()) {
                    imageUrls.add(trimmedUrl);
                }
            }
            if (!imageUrls.isEmpty()) {
                holder.binding.rvReviewImages.setVisibility(View.VISIBLE);
                ReviewImageAdapter imageAdapter = new ReviewImageAdapter(imageUrls);
                holder.binding.rvReviewImages.setAdapter(imageAdapter);
                holder.binding.rvReviewImages.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            } else {
                holder.binding.rvReviewImages.setVisibility(View.GONE);
            }
        } else {
            holder.binding.rvReviewImages.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return reviews != null ? reviews.size() : 0;
    }

    /**
     * Tên quá dài → viết tắt từ đầu tiên thành chữ cái + "."
     * Ví dụ: "Nguyễn Thảo Nguyên" → "N. Thảo Nguyên".
     */
    private String formatDisplayName(String name) {
        if (name == null || name.trim().isEmpty()) return "Ẩn danh";
        name = name.trim();
        if (name.length() <= 15) return name;
        String[] parts = name.split("\\s+");
        if (parts.length >= 2 && !parts[0].isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append(Character.toUpperCase(parts[0].charAt(0))).append('.');
            for (int i = 1; i < parts.length; i++) {
                sb.append(' ').append(parts[i]);
            }
            return sb.toString();
        }
        return name;
    }

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        ItemReviewBinding binding;

        public ReviewViewHolder(ItemReviewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    static class ReviewImageAdapter extends RecyclerView.Adapter<ReviewImageAdapter.ImageViewHolder> {
        private List<String> images;

        public ReviewImageAdapter(List<String> images) {
            this.images = images;
        }

        @NonNull
        @Override
        public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemReviewImageBinding binding = ItemReviewImageBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
            return new ImageViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
            Glide.with(holder.itemView.getContext())
                    .load(images.get(position).trim())
                    .into(holder.binding.ivReviewImage);
        }

        @Override
        public int getItemCount() {
            return images != null ? images.size() : 0;
        }

        static class ImageViewHolder extends RecyclerView.ViewHolder {
            ItemReviewImageBinding binding;

            public ImageViewHolder(ItemReviewImageBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}
