package com.pompom.group6.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pompom.group6.R;
import com.pompom.group6.activities.PostDetailActivity;
import com.pompom.group6.models.CommunityPost;

import java.util.List;
import java.util.Locale;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {

    private final List<CommunityPost> posts;

    public PostAdapter(List<CommunityPost> posts) {
        this.posts = posts;
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_community_highlight, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        CommunityPost post = posts.get(position);
        holder.tvContent.setText(post.getContent());
        holder.tvUserName.setText(post.getUserName());
        
        int engagement = post.getLikeCount() + post.getCommentCount();
        String stats = engagement >= 1000 ? 
            String.format(Locale.getDefault(), "%.1fK", engagement / 1000.0) : 
            String.valueOf(engagement);
        holder.tvLikes.setText(stats);

        if (post.getImageUrl() != null && !post.getImageUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(post.getImageUrl())
                    .placeholder(R.drawable.promotion2)
                    .into(holder.ivImage);
        }

        if (post.getUserAvatar() != null && !post.getUserAvatar().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(post.getUserAvatar())
                    .circleCrop()
                    .placeholder(R.drawable.ic_launcher_background)
                    .into(holder.ivUserAvatar);
        }
        
        // Show HOT label for first 2 posts or if engagement is very high
        holder.layoutHot.setVisibility((position < 2 || engagement > 50) ? View.VISIBLE : View.GONE);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PostDetailActivity.class);
            intent.putExtra("post_id", post.getPostId());
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage, ivUserAvatar;
        TextView tvContent, tvLikes, tvUserName;
        View layoutHot;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivPostImage);
            ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
            tvContent = itemView.findViewById(R.id.tvPostContent);
            tvLikes = itemView.findViewById(R.id.tvLikeCount);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            layoutHot = itemView.findViewById(R.id.layoutHotLabel);
        }
    }
}
