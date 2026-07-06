package com.pompom.group6.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.pompom.group6.R;
import com.pompom.group6.databinding.ActivityArticleDetailBinding;
import com.pompom.group6.utils.StatusBarUtils;

/** Chi tiết bài viết — dùng chung cho Blog thương hiệu và Tips từ chuyên gia. */
public class ArticleDetailActivity extends AppCompatActivity {

    public static final String EXTRA_TITLE = "extra_title";
    public static final String EXTRA_COVER_IMAGE = "extra_cover_image";
    public static final String EXTRA_CONTENT_HTML = "extra_content_html";
    public static final String EXTRA_META = "extra_meta";
    public static final String EXTRA_AUTHOR_AVATAR = "extra_author_avatar";
    public static final String EXTRA_EXPERT_ID = "extra_expert_id";
    public static final String EXTRA_ARTICLE_ID = "extra_article_id";

    private ActivityArticleDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityArticleDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        StatusBarUtils.applyPinkHeader(this);

        binding.header.btnBack.setOnClickListener(v -> finish());
        binding.header.tvHeaderTitle.setText("Bài viết");

        String title = getIntent().getStringExtra(EXTRA_TITLE);
        String coverImage = getIntent().getStringExtra(EXTRA_COVER_IMAGE);
        String contentHtml = getIntent().getStringExtra(EXTRA_CONTENT_HTML);
        String meta = getIntent().getStringExtra(EXTRA_META);
        String authorAvatar = getIntent().getStringExtra(EXTRA_AUTHOR_AVATAR);
        String expertId = getIntent().getStringExtra(EXTRA_EXPERT_ID);
        String articleId = getIntent().getStringExtra(EXTRA_ARTICLE_ID);

        binding.tvArticleTitle.setText(title);
        binding.tvArticleMeta.setText(meta);
        binding.tvArticleContent.setText(contentHtml != null
                ? Html.fromHtml(contentHtml, Html.FROM_HTML_MODE_LEGACY)
                : "");

        Glide.with(this).load(coverImage).placeholder(R.drawable.logo_pompom).into(binding.ivArticleCover);
        Glide.with(this).load(authorAvatar).placeholder(R.drawable.ic_avatar).into(binding.ivArticleAuthorAvatar);

        // Luôn hiện nút liên hệ tư vấn — bài blog thì để người dùng tự chọn chuyên gia,
        // bài tips thì đã có sẵn chuyên gia phụ trách (expertId).
        binding.btnConsultFromArticle.setVisibility(View.VISIBLE);
        binding.btnConsultFromArticle.setOnClickListener(v -> {
            Intent intent = new Intent(this, ConsultationRequestActivity.class);
            if (expertId != null) intent.putExtra(ConsultationRequestActivity.EXTRA_EXPERT_ID, expertId);
            if (articleId != null) intent.putExtra(ConsultationRequestActivity.EXTRA_ARTICLE_ID, articleId);
            startActivity(intent);
        });
    }
}
