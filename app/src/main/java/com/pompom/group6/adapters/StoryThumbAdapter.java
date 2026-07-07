package com.pompom.group6.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pompom.group6.R;
import com.pompom.group6.databinding.ItemStoryThumbBinding;
import com.pompom.group6.network.dto.ApiNearbyPost;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

/** Lưới thumbnail story do user đã đăng; bấm 1 ô -> mở StoryViewer từ vị trí đó. */
public class StoryThumbAdapter extends RecyclerView.Adapter<StoryThumbAdapter.VH> {

    /** Bấm vào story ở vị trí position. */
    public interface OnStoryClick {
        void onClick(int position);
    }

    private final List<ApiNearbyPost> stories = new ArrayList<>();
    private final OnStoryClick onStoryClick;

    public StoryThumbAdapter(OnStoryClick onStoryClick) {
        this.onStoryClick = onStoryClick;
    }

    public void setStories(List<ApiNearbyPost> newStories) {
        stories.clear();
        if (newStories != null) stories.addAll(newStories);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemStoryThumbBinding b = ItemStoryThumbBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(b);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        ApiNearbyPost s = stories.get(position);

        holder.b.tvCaption.setText(s.caption != null ? s.caption : "");
        holder.b.tvCaption.setVisibility(s.caption != null && !s.caption.isEmpty() ? View.VISIBLE : View.GONE);
        holder.b.tvTime.setText(timeAgo(s.createdAt));
        holder.b.ivPlay.setVisibility("video".equals(s.mediaType) ? View.VISIBLE : View.GONE);

        Glide.with(holder.itemView.getContext())
                .load(s.mediaUrl)
                .placeholder(R.drawable.promotion1)
                .into(holder.b.ivStory);

        holder.itemView.setOnClickListener(v -> {
            if (onStoryClick != null) onStoryClick.onClick(holder.getBindingAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    /** ISO UTC -> "Vừa xong" / "x phút" / "x giờ". */
    private String timeAgo(String isoDate) {
        if (isoDate == null || isoDate.length() < 19) return "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = sdf.parse(isoDate.substring(0, 19));
            if (date == null) return "";
            long minutes = (System.currentTimeMillis() - date.getTime()) / 60000;
            if (minutes < 1) return "Vừa xong";
            if (minutes < 60) return minutes + " phút";
            return (minutes / 60) + " giờ";
        } catch (ParseException e) {
            return "";
        }
    }

    static class VH extends RecyclerView.ViewHolder {
        final ItemStoryThumbBinding b;

        VH(ItemStoryThumbBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }
}
