package com.pompom.group6.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pompom.group6.R;
import com.pompom.group6.databinding.ItemReelPageBinding;
import com.pompom.group6.network.dto.ApiReel;

import java.util.List;

/**
 * Mỗi trang ViewPager2 là 1 reel; chỉ dùng CHUNG 1 ExoPlayer, gắn vào đúng trang đang
 * hiển thị — giống cách Reels của Facebook/Instagram phát liên tục khi lướt dọc.
 */
public class ReelPagerAdapter extends RecyclerView.Adapter<ReelPagerAdapter.PageViewHolder> {
    private final List<ApiReel> reels;
    private final ExoPlayer player;
    private int activePosition = -1;
    private RecyclerView recyclerView;

    public ReelPagerAdapter(List<ApiReel> reels, ExoPlayer player) {
        this.reels = reels;
        this.player = player;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView rv) {
        super.onAttachedToRecyclerView(rv);
        this.recyclerView = rv;
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReelPageBinding binding = ItemReelPageBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new PageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        ApiReel reel = reels.get(position);

        if (reel.author != null) {
            holder.binding.tvPlayerAuthorName.setText(reel.author.name);
            holder.binding.ivPlayerVerified.setVisibility(reel.author.verified ? View.VISIBLE : View.GONE);
            Glide.with(holder.itemView.getContext())
                    .load(reel.author.avatarUrl)
                    .placeholder(R.drawable.ic_avatar)
                    .into(holder.binding.ivPlayerAuthorAvatar);
        } else {
            holder.binding.tvPlayerAuthorName.setText("");
            holder.binding.ivPlayerVerified.setVisibility(View.GONE);
        }
        holder.binding.tvPlayerCaption.setText(reel.caption);

        if (reel.productTags != null && !reel.productTags.isEmpty()) {
            holder.binding.rvPlayerProductTags.setVisibility(View.VISIBLE);
            holder.binding.rvPlayerProductTags.setLayoutManager(
                    new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            holder.binding.rvPlayerProductTags.setAdapter(new ReelAdapter.TaggedProductAdapter(reel.productTags));
        } else {
            holder.binding.rvPlayerProductTags.setVisibility(View.GONE);
        }

        if (position == activePosition) {
            attachPlayer(holder, reel);
        } else {
            holder.binding.playerView.setPlayer(null);
        }
    }

    @Override
    public void onViewRecycled(@NonNull PageViewHolder holder) {
        super.onViewRecycled(holder);
        holder.binding.playerView.setPlayer(null);
    }

    @Override
    public int getItemCount() {
        return reels != null ? reels.size() : 0;
    }

    /** Gọi khi ViewPager2 đổi trang: phát reel đang hiển thị, gỡ player khỏi trang cũ. */
    public void setActivePosition(int position) {
        if (activePosition == position) return;
        activePosition = position;
        if (recyclerView == null) return;
        for (int i = 0; i < getItemCount(); i++) {
            RecyclerView.ViewHolder vh = recyclerView.findViewHolderForAdapterPosition(i);
            if (vh instanceof PageViewHolder) {
                PageViewHolder page = (PageViewHolder) vh;
                if (i == position) {
                    attachPlayer(page, reels.get(i));
                } else {
                    page.binding.playerView.setPlayer(null);
                }
            }
        }
    }

    private void attachPlayer(PageViewHolder holder, ApiReel reel) {
        holder.binding.playerView.setPlayer(player);
        player.setMediaItem(MediaItem.fromUri(reel.videoUrl));
        player.setRepeatMode(Player.REPEAT_MODE_ONE);
        player.prepare();
        player.setPlayWhenReady(true);
    }

    static class PageViewHolder extends RecyclerView.ViewHolder {
        final ItemReelPageBinding binding;

        PageViewHolder(ItemReelPageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
