package com.pompom.group6.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pompom.group6.R;
import com.pompom.group6.activities.ArticleDetailActivity;
import com.pompom.group6.databinding.ItemExpertArticleCardBinding;
import com.pompom.group6.network.dto.ApiExpertArticle;

import java.util.List;

public class ExpertArticleAdapter extends RecyclerView.Adapter<ExpertArticleAdapter.ArticleViewHolder> {
    private final List<ApiExpertArticle> articles;

    public ExpertArticleAdapter(List<ApiExpertArticle> articles) {
        this.articles = articles;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemExpertArticleCardBinding binding = ItemExpertArticleCardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ArticleViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        ApiExpertArticle article = articles.get(position);
        Glide.with(holder.itemView.getContext())
                .load(article.coverImage)
                .placeholder(R.drawable.logo_pompom)
                .into(holder.binding.ivArticleCover);
        Glide.with(holder.itemView.getContext())
                .load(article.expertAvatar)
                .placeholder(R.drawable.ic_avatar)
                .into(holder.binding.ivArticleExpertAvatar);
        holder.binding.tvArticleExpertName.setText(article.expertName);
        holder.binding.tvArticleExpertTitle.setText(article.expertTitle);
        holder.binding.tvArticleTitle.setText(article.title);
        holder.binding.tvArticleReadTime.setText(article.readTime + " phút đọc");

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ArticleDetailActivity.class);
            intent.putExtra(ArticleDetailActivity.EXTRA_TITLE, article.title);
            intent.putExtra(ArticleDetailActivity.EXTRA_COVER_IMAGE, article.coverImage);
            intent.putExtra(ArticleDetailActivity.EXTRA_CONTENT_HTML, article.content);
            intent.putExtra(ArticleDetailActivity.EXTRA_META, article.expertName + " • " + article.expertTitle);
            intent.putExtra(ArticleDetailActivity.EXTRA_AUTHOR_AVATAR, article.expertAvatar);
            intent.putExtra(ArticleDetailActivity.EXTRA_EXPERT_ID, article.expertId);
            intent.putExtra(ArticleDetailActivity.EXTRA_ARTICLE_ID, article.id);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return articles != null ? articles.size() : 0;
    }

    static class ArticleViewHolder extends RecyclerView.ViewHolder {
        final ItemExpertArticleCardBinding binding;

        ArticleViewHolder(ItemExpertArticleCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
