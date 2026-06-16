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
import com.pompom.group6.activities.PostDetailActivity;
import com.pompom.group6.models.CommunityPost;

import java.util.ArrayList;
import java.util.List;
import android.content.Intent;

public class CommunityPostAdapter extends RecyclerView.Adapter<CommunityPostAdapter.PostViewHolder> {

    private final List<CommunityPost> posts = new ArrayList<>();

    public void setPosts(List<CommunityPost> newPosts) {
        posts.clear();
        posts.addAll(newPosts);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_community_post_card, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        CommunityPost post = posts.get(position);
        holder.tvTitle.setText(post.getContent());
        holder.tvUserName.setText(post.getUserName());
        holder.tvLikes.setText(formatCount(post.getLikeCount()));
        holder.tvComments.setText(String.valueOf(post.getCommentCount()));

        Glide.with(holder.itemView.getContext())
                .load(post.getImageUrl())
                .placeholder(R.drawable.logo_pompom)
                .into(holder.ivPostImage);

        Glide.with(holder.itemView.getContext())
                .load(post.getUserAvatar())
                .circleCrop()
                .placeholder(R.drawable.logo_pompom)
                .into(holder.ivUserAvatar);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PostDetailActivity.class);
            intent.putExtra("post_id", post.getPostId());
            v.getContext().startActivity(intent);
        });
    }

    private String formatCount(int count) {
        if (count >= 1000) {
            return String.format("%.1fK", count / 1000.0);
        }
        return String.valueOf(count);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPostImage, ivUserAvatar;
        TextView tvTitle, tvUserName, tvLikes, tvComments;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPostImage = itemView.findViewById(R.id.ivPostImage);
            ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
            tvTitle = itemView.findViewById(R.id.tvPostTitle);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvLikes = itemView.findViewById(R.id.tvLikeCount);
            tvComments = itemView.findViewById(R.id.tvCommentCount);
        }
    }
}
