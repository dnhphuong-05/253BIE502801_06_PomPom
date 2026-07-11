package com.pompom.group6.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pompom.group6.R;
import com.pompom.group6.activities.ArticleDetailActivity;
import com.pompom.group6.databinding.ItemBlogCardBinding;
import com.pompom.group6.databinding.ItemBlogCardFullBinding;
import com.pompom.group6.network.dto.ApiBlog;

import java.util.List;

/** fullWidth=false: thẻ 220dp dùng cho carousel ngang cũ. fullWidth=true: thẻ match_parent dùng cho tab "Blog thương hiệu". */
public class BlogAdapter extends RecyclerView.Adapter<BlogAdapter.BlogViewHolder> {
    private final List<ApiBlog> blogs;
    private final boolean fullWidth;

    public BlogAdapter(List<ApiBlog> blogs) {
        this(blogs, false);
    }

    public BlogAdapter(List<ApiBlog> blogs, boolean fullWidth) {
        this.blogs = blogs;
        this.fullWidth = fullWidth;
    }

    @NonNull
    @Override
    public BlogViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (fullWidth) {
            return new BlogViewHolder(ItemBlogCardFullBinding.inflate(inflater, parent, false));
        }
        return new BlogViewHolder(ItemBlogCardBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull BlogViewHolder holder, int position) {
        ApiBlog blog = blogs.get(position);
        Glide.with(holder.itemView.getContext())
                .load(blog.coverImage)
                .placeholder(R.drawable.logo_pompom)
                .into(holder.ivBlogCover);
        holder.tvBlogCategory.setText(blog.category != null ? blog.category : "Thương hiệu");
        holder.tvBlogTitle.setText(blog.title);
        holder.tvBlogExcerpt.setText(blog.excerpt);
        holder.tvBlogReadTime.setText(blog.readTime + " phút đọc");

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
            intent.putExtra(ArticleDetailActivity.EXTRA_ARTICLE_ID, blog.id);
            intent.putExtra(ArticleDetailActivity.EXTRA_CATEGORY, blog.category);
            intent.putExtra(ArticleDetailActivity.EXTRA_READ_TIME, blog.readTime);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return blogs != null ? blogs.size() : 0;
    }

    /** Trỏ tới view trong 1 trong 2 layout (carousel/full) — cùng id nên field access giống nhau. */
    static class BlogViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivBlogCover;
        final TextView tvBlogCategory;
        final TextView tvBlogTitle;
        final TextView tvBlogExcerpt;
        final TextView tvBlogReadTime;

        BlogViewHolder(ItemBlogCardBinding binding) {
            super(binding.getRoot());
            ivBlogCover = binding.ivBlogCover;
            tvBlogCategory = binding.tvBlogCategory;
            tvBlogTitle = binding.tvBlogTitle;
            tvBlogExcerpt = binding.tvBlogExcerpt;
            tvBlogReadTime = binding.tvBlogReadTime;
        }

        BlogViewHolder(ItemBlogCardFullBinding binding) {
            super(binding.getRoot());
            ivBlogCover = binding.ivBlogCover;
            tvBlogCategory = binding.tvBlogCategory;
            tvBlogTitle = binding.tvBlogTitle;
            tvBlogExcerpt = binding.tvBlogExcerpt;
            tvBlogReadTime = binding.tvBlogReadTime;
        }
    }
}
