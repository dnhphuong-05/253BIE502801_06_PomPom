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
import com.pompom.group6.databinding.ItemExpertArticleCardBinding;
import com.pompom.group6.databinding.ItemExpertArticleCardFullBinding;
import com.pompom.group6.network.dto.ApiExpertArticle;

import java.util.List;

/** fullWidth=false: thẻ 220dp dùng cho carousel ngang cũ. fullWidth=true: thẻ match_parent + huy hiệu "Đã xác minh" dùng cho tab "Tips bác sĩ". */
public class ExpertArticleAdapter extends RecyclerView.Adapter<ExpertArticleAdapter.ArticleViewHolder> {
    private final List<ApiExpertArticle> articles;
    private final boolean fullWidth;

    public ExpertArticleAdapter(List<ApiExpertArticle> articles) {
        this(articles, false);
    }

    public ExpertArticleAdapter(List<ApiExpertArticle> articles, boolean fullWidth) {
        this.articles = articles;
        this.fullWidth = fullWidth;
    }

    @NonNull
    @Override
    public ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (fullWidth) {
            return new ArticleViewHolder(ItemExpertArticleCardFullBinding.inflate(inflater, parent, false));
        }
        return new ArticleViewHolder(ItemExpertArticleCardBinding.inflate(inflater, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleViewHolder holder, int position) {
        ApiExpertArticle article = articles.get(position);
        Glide.with(holder.itemView.getContext())
                .load(article.coverImage)
                .placeholder(R.drawable.logo_pompom)
                .into(holder.ivArticleCover);
        Glide.with(holder.itemView.getContext())
                .load(article.expertAvatar)
                .placeholder(R.drawable.ic_avatar)
                .into(holder.ivArticleExpertAvatar);
        holder.tvArticleExpertName.setText(article.expertName);
        holder.tvArticleExpertTitle.setText(article.expertTitle);
        holder.tvArticleTitle.setText(article.title);
        holder.tvArticleReadTime.setText(article.readTime + " phút đọc");

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

    /** Trỏ tới view trong 1 trong 2 layout (carousel/full) — cùng id nên field access giống nhau. */
    static class ArticleViewHolder extends RecyclerView.ViewHolder {
        final ImageView ivArticleCover;
        final ImageView ivArticleExpertAvatar;
        final TextView tvArticleExpertName;
        final TextView tvArticleExpertTitle;
        final TextView tvArticleTitle;
        final TextView tvArticleReadTime;

        ArticleViewHolder(ItemExpertArticleCardBinding binding) {
            super(binding.getRoot());
            ivArticleCover = binding.ivArticleCover;
            ivArticleExpertAvatar = binding.ivArticleExpertAvatar;
            tvArticleExpertName = binding.tvArticleExpertName;
            tvArticleExpertTitle = binding.tvArticleExpertTitle;
            tvArticleTitle = binding.tvArticleTitle;
            tvArticleReadTime = binding.tvArticleReadTime;
        }

        ArticleViewHolder(ItemExpertArticleCardFullBinding binding) {
            super(binding.getRoot());
            ivArticleCover = binding.ivArticleCover;
            ivArticleExpertAvatar = binding.ivArticleExpertAvatar;
            tvArticleExpertName = binding.tvArticleExpertName;
            tvArticleExpertTitle = binding.tvArticleExpertTitle;
            tvArticleTitle = binding.tvArticleTitle;
            tvArticleReadTime = binding.tvArticleReadTime;
        }
    }
}
