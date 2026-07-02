package com.pompom.group6.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pompom.group6.R;
import com.pompom.group6.models.Comment;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {

    private final List<Comment> comments;

    public CommentAdapter(List<Comment> comments) {
        this.comments = comments;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        Comment comment = comments.get(position);
        holder.tvName.setText(comment.getUserName());
        holder.tvContent.setText(comment.getContent());
        holder.tvTime.setText(comment.getCreatedAt());
        holder.tvLikes.setText(String.valueOf(comment.getLikes()));
        
        if (comment.getUserRank() != null) {
            holder.tvRank.setText(comment.getUserRank());
            holder.tvRank.setVisibility(View.VISIBLE);
        } else {
            holder.tvRank.setVisibility(View.GONE);
        }

        Glide.with(holder.itemView.getContext())
                .load(comment.getUserAvatar())
                .circleCrop()
                .placeholder(R.drawable.logo_pompom)
                .into(holder.ivAvatar);

        holder.btnLike.setOnClickListener(v -> {
            boolean isLiked = v.getTag() != null && (boolean) v.getTag();
            int currentLikes = comment.getLikes();
            
            if (isLiked) {
                v.setTag(false);
                ((ImageView) v).setImageResource(R.drawable.ic_heart);
                ((ImageView) v).setColorFilter(ContextCompat.getColor(v.getContext(), R.color.text_secondary));
                comment.setLikes(currentLikes - 1);
            } else {
                v.setTag(true);
                ((ImageView) v).setImageResource(R.drawable.ic_heart);
                ((ImageView) v).setColorFilter(ContextCompat.getColor(v.getContext(), R.color.brand_pink));
                comment.setLikes(currentLikes + 1);
            }
            holder.tvLikes.setText(String.valueOf(comment.getLikes()));
        });
    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    static class CommentViewHolder extends RecyclerView.ViewHolder {
        ImageView ivAvatar, btnLike;
        TextView tvName, tvRank, tvContent, tvTime, tvLikes;

        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            ivAvatar = itemView.findViewById(R.id.ivUserAvatar);
            btnLike = itemView.findViewById(R.id.btnLikeComment);
            tvName = itemView.findViewById(R.id.tvUserName);
            tvRank = itemView.findViewById(R.id.tvUserRank);
            tvContent = itemView.findViewById(R.id.tvCommentContent);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvLikes = itemView.findViewById(R.id.tvCommentLikes);
        }
    }
}
