package com.pompom.group6.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pompom.group6.R;
import com.pompom.group6.activities.ProductDetailActivity;
import com.pompom.group6.activities.ReelPlayerActivity;
import com.pompom.group6.databinding.ItemReelCardBinding;
import com.pompom.group6.databinding.ItemReelProductTagBinding;
import com.pompom.group6.network.dto.ApiProductTag;
import com.pompom.group6.network.dto.ApiReel;

import java.util.List;
import java.util.Locale;

public class ReelAdapter extends RecyclerView.Adapter<ReelAdapter.ReelViewHolder> {
    private final List<ApiReel> reels;

    public ReelAdapter(List<ApiReel> reels) {
        this.reels = reels;
    }

    @NonNull
    @Override
    public ReelViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReelCardBinding binding = ItemReelCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ReelViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ReelViewHolder holder, int position) {
        ApiReel reel = reels.get(position);
        Glide.with(holder.itemView.getContext())
                .load(reel.thumbnailUrl)
                .placeholder(R.drawable.logo_pompom)
                .into(holder.binding.ivReelThumbnail);

        int minutes = reel.duration / 60;
        int seconds = reel.duration % 60;
        holder.binding.tvReelDuration.setText(String.format(Locale.US, "%d:%02d", minutes, seconds));
        holder.binding.tvReelSource.setText(capitalize(reel.source));

        if (reel.author != null) {
            holder.binding.tvReelAuthorName.setText(reel.author.name);
            holder.binding.ivReelVerified.setVisibility(reel.author.verified ? View.VISIBLE : View.GONE);
            Glide.with(holder.itemView.getContext())
                    .load(reel.author.avatarUrl)
                    .placeholder(R.drawable.ic_avatar)
                    .into(holder.binding.ivReelAuthorAvatar);
        } else {
            holder.binding.tvReelAuthorName.setText("");
            holder.binding.ivReelVerified.setVisibility(View.GONE);
        }

        holder.binding.tvReelCaption.setText(reel.caption);
        holder.binding.tvReelStats.setText(String.format(Locale.getDefault(),
                "%s lượt xem  •  %s thích  •  %d bình luận",
                formatCount(reel.viewCount), formatCount(reel.likeCount), reel.commentCount));

        if (reel.productTags != null && !reel.productTags.isEmpty()) {
            holder.binding.rvReelProductTags.setVisibility(View.VISIBLE);
            holder.binding.rvReelProductTags.setLayoutManager(
                    new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            holder.binding.rvReelProductTags.setAdapter(new TaggedProductAdapter(reel.productTags));
        } else {
            holder.binding.rvReelProductTags.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ReelPlayerActivity.class);
            intent.putExtra(ReelPlayerActivity.EXTRA_REEL, reel);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return reels != null ? reels.size() : 0;
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return "";
        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

    private String formatCount(long count) {
        if (count >= 1_000_000) return String.format(Locale.US, "%.1fM", count / 1_000_000.0);
        if (count >= 1_000) return String.format(Locale.US, "%.1fK", count / 1_000.0);
        return String.valueOf(count);
    }

    static class ReelViewHolder extends RecyclerView.ViewHolder {
        final ItemReelCardBinding binding;

        ReelViewHolder(ItemReelCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    /** Chip sản phẩm gắn thẻ trong reel — tap mở ProductDetailActivity. Dùng chung cho ReelPlayerActivity. */
    public static class TaggedProductAdapter extends RecyclerView.Adapter<TaggedProductAdapter.TagViewHolder> {
        private final List<ApiProductTag> tags;

        public TaggedProductAdapter(List<ApiProductTag> tags) {
            this.tags = tags;
        }

        @NonNull
        @Override
        public TagViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemReelProductTagBinding binding = ItemReelProductTagBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new TagViewHolder(binding);
        }

        @Override
        public void onBindViewHolder(@NonNull TagViewHolder holder, int position) {
            ApiProductTag tag = tags.get(position);
            holder.binding.tvTagName.setText(tag.name);
            Glide.with(holder.itemView.getContext())
                    .load(tag.thumbnailUrl)
                    .placeholder(R.drawable.logo_pompom)
                    .into(holder.binding.ivTagThumbnail);
            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
                intent.putExtra("product_id", tag.id);
                v.getContext().startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return tags != null ? tags.size() : 0;
        }

        static class TagViewHolder extends RecyclerView.ViewHolder {
            final ItemReelProductTagBinding binding;

            TagViewHolder(ItemReelProductTagBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}
