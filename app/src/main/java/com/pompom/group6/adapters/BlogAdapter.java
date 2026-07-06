package com.pompom.group6.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pompom.group6.R;
import com.pompom.group6.activities.ArticleDetailActivity;
import com.pompom.group6.databinding.ItemBlogCardBinding;
import com.pompom.group6.network.dto.ApiBlog;

import java.util.List;

public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogViewHolder> {
    private final List<ApiBlog> blogs;

    public BlogAdapter(List<ApiBlog> blogs) {
        this.blogs = blogs;
    }

    @NonNull
    @Override
    public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemBlogCardBinding binding = ItemBlogCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new BlogViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogViewHolder holder, int position) {
        ApiBlog blog = blogs.get(position);
        Glide.with(holder.itemView.getContext())
                .load(blog.coverImage)
                .placeholder(R.drawable.logo_pompom)
                .into(holder.binding.ivBlogCover);
        holder.binding.tvBlogCategory.setText(blog.category != null ? blog.category : "Thương hiệu");
        holder.binding.tvBlogTitle.setText(blog.title);
        holder.binding.tvBlogExcerpt.setText(blog.excerpt);
        holder.binding.tvBlogReadTime.setText(blog.readTime + " phút đọc");

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ArticleDetailActivity.class);
            intent.putExtra(ArticleDetailActivity.EXTRA_TITLE, blog.title);
            intent.putExtra(ArticleDetailActivity.EXTRA_COVER_IMAGE, blog.coverImage);
            intent.putExtra(ArticleDetailActivity.EXTRA_CONTENT_HTML, blog.content);
            String authorName = blog.author != null ? blog.author.name : "PomPom Team";
            String authorRole = blog.author != null ? blog.author.role : "Thương hiệu";
            String authorAvatar = blog.author != null ? blog.author.avatarUrl : null;
            intent.putExtra(ArticleDetailActivity.EXTRA_META, authorName + " • " + authorRole);
            intent.putExtra(ArticleDetailActivity.EXTRA_AUTHOR_AVATAR, authorAvatar);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return blogs != null ? blogs.size() : 0;
    }

    static class BlogViewHolder extends RecyclerView.ViewHolder {
        final ItemBlogCardBinding binding;

        BlogViewHolder(ItemBlogCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
