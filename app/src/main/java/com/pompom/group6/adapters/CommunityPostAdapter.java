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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
        
        holder.tvUserName.setText(post.getUserName());
        holder.tvPostTitle.setText(post.getContent());
        holder.tvLikeCount.setText(formatCount(post.getLikeCount()));
        holder.tvCommentCount.setText(String.valueOf(post.getCommentCount()));
        holder.tvShareCount.setText("132"); // Mocked
        
        holder.tvPostTime.setText("2 giờ trước • " + post.getPostType());

        Glide.with(holder.itemView.getContext())
                .load(post.getUserAvatar())
                .circleCrop()
                .placeholder(R.drawable.ic_avatar)
                .into(holder.ivUserAvatar);

        // TRUE MULTI-IMAGE LOGIC
        List<String> imageList = post.getImages();
        
        if (imageList != null && imageList.size() >= 3) {
            // MULTI LAYOUT
            holder.ivPostImageSingle.setVisibility(View.GONE);
            holder.layoutMultiImage.setVisibility(View.VISIBLE);
            
            Glide.with(holder.itemView.getContext()).load(imageList.get(0)).into(holder.ivPostImageMain);
            Glide.with(holder.itemView.getContext()).load(imageList.get(1)).into(holder.ivPostImageSide1);
            Glide.with(holder.itemView.getContext()).load(imageList.get(2)).into(holder.ivPostImageSide2);
        } else if (imageList != null && !imageList.isEmpty()) {
            // SINGLE LAYOUT
            holder.ivPostImageSingle.setVisibility(View.VISIBLE);
            holder.layoutMultiImage.setVisibility(View.GONE);
            
            Glide.with(holder.itemView.getContext()).load(imageList.get(0)).into(holder.ivPostImageSingle);
        } else {
            // NO IMAGE
            holder.ivPostImageSingle.setVisibility(View.GONE);
            holder.layoutMultiImage.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), PostDetailActivity.class);
            intent.putExtra("post_id", post.getPostId());
            v.getContext().startActivity(intent);
        });
    }

    private String formatCount(int count) {
        if (count >= 1000) {
            return String.format(Locale.getDefault(), "%.1fK", count / 1000.0);
        }
        return String.valueOf(count);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView ivUserAvatar, ivPostImageSingle, ivPostImageMain, ivPostImageSide1, ivPostImageSide2;
        TextView tvUserName, tvPostTime, tvPostTitle, tvLikeCount, tvCommentCount, tvShareCount;
        View layoutMultiImage;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
            ivPostImageSingle = itemView.findViewById(R.id.ivPostImageSingle);
            ivPostImageMain = itemView.findViewById(R.id.ivPostImageMain);
            ivPostImageSide1 = itemView.findViewById(R.id.ivPostImageSide1);
            ivPostImageSide2 = itemView.findViewById(R.id.ivPostImageSide2);
            layoutMultiImage = itemView.findViewById(R.id.layoutMultiImage);
            
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvPostTime = itemView.findViewById(R.id.tvPostTime);
            tvPostTitle = itemView.findViewById(R.id.tvPostTitle);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            tvCommentCount = itemView.findViewById(R.id.tvCommentCount);
            tvShareCount = itemView.findViewById(R.id.tvShareCount);
        }
    }
}
