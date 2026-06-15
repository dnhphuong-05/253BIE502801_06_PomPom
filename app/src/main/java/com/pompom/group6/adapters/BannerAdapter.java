package com.pompom.group6.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pompom.group6.databinding.ItemBannerBinding;
import com.pompom.group6.models.Banner;

import java.util.List;

public class BannerAdapter extends RecyclerView.Adapter<BannerAdapter.BannerViewHolder> {

    private final List<Banner> banners;

    public BannerAdapter(List<Banner> banners) {
        this.banners = banners;
    }

    @NonNull
    @Override
    public BannerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBannerBinding binding = ItemBannerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new BannerViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BannerViewHolder holder, int position) {
        holder.bind(banners.get(position));
    }

    @Override
    public int getItemCount() {
        return banners.size();
    }

    public static class BannerViewHolder extends RecyclerView.ViewHolder {
        private final ItemBannerBinding binding;

        public BannerViewHolder(ItemBannerBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Banner banner) {
            if (banner.getImageUrl() != null && !banner.getImageUrl().isEmpty()) {
                Glide.with(binding.getRoot().getContext())
                        .load(banner.getImageUrl())
                        .into(binding.ivBanner);
            } else if (banner.getImageRes() != 0) {
                binding.ivBanner.setImageResource(banner.getImageRes());
            }
            binding.tvBannerTitle.setText(banner.getTitle());
            
            // Initial state for zoom effect
            binding.ivBanner.setScaleX(1.2f);
            binding.ivBanner.setScaleY(1.2f);
        }

        public void startZoomAnimation() {
            binding.ivBanner.animate().cancel();
            binding.ivBanner.setScaleX(1.2f);
            binding.ivBanner.setScaleY(1.2f);
            binding.ivBanner.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(4000)
                    .setInterpolator(new LinearInterpolator())
                    .start();
        }
    }
}
