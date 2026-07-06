package com.pompom.group6.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Toast;
import android.view.inputmethod.EditorInfo;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.pompom.group6.R;
import com.pompom.group6.adapters.CommentAdapter;
import com.pompom.group6.databinding.ActivityArticleDetailBinding;
import com.pompom.group6.models.Comment;
import com.pompom.group6.network.ApiClient;
import com.pompom.group6.network.Session;
import com.pompom.group6.network.dto.ApiComment;
import com.pompom.group6.utils.StatusBarUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Chi tiết bài viết — dùng chung cho Blog thương hiệu và Tips từ chuyên gia. */
public class ArticleDetailActivity extends SwipeBackActivity {

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
        StatusBarUtils.applyHeaderContentInsets(this, binding.header.getRoot(), binding.getRoot());

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

        setupComments(articleId);
    }

    /**
     * Bình luận thật, lưu qua MongoDB — dùng chung endpoint
     * {@code /api/community/posts/:id/comments} với chính id của blog/bài tips
     * (Comment.post_id chỉ là tham chiếu id chung, không ràng buộc phải là CommunityPost).
     */
    private void setupComments(String articleId) {
        binding.rvComments.setLayoutManager(new LinearLayoutManager(this));

        if (articleId == null) {
            binding.tvCommentSectionTitle.setVisibility(View.GONE);
            binding.rvComments.setVisibility(View.GONE);
            binding.tvCommentEmpty.setVisibility(View.GONE);
            binding.commentInputBar.setVisibility(View.GONE);
            return;
        }

        loadComments(articleId);

        binding.btnSendComment.setOnClickListener(v -> submitComment(articleId));
        binding.etComment.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                submitComment(articleId);
                return true;
            }
            return false;
        });
    }

    private void loadComments(String articleId) {
        ApiClient.get().getPostComments(articleId).enqueue(new Callback<List<ApiComment>>() {
            @Override
            public void onResponse(Call<List<ApiComment>> call, Response<List<ApiComment>> resp) {
                if (!resp.isSuccessful() || resp.body() == null) return;
                List<Comment> comments = new ArrayList<>();
                for (ApiComment c : resp.body()) {
                    comments.add(new Comment(0, 0, c.authorName, c.authorAvatar, c.content, c.createdAt));
                }
                binding.rvComments.setAdapter(new CommentAdapter(comments));
                binding.tvCommentSectionTitle.setText("Bình luận (" + comments.size() + ")");
                binding.tvCommentEmpty.setVisibility(comments.isEmpty() ? View.VISIBLE : View.GONE);
            }
            @Override public void onFailure(Call<List<ApiComment>> call, Throwable t) {}
        });
    }

    private void submitComment(String articleId) {
        String userOid = Session.getUserOid(this);
        if (userOid == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để bình luận", Toast.LENGTH_SHORT).show();
            return;
        }
        String content = binding.etComment.getText().toString().trim();
        if (content.isEmpty()) return;

        Map<String, String> body = new HashMap<>();
        body.put("user_id", userOid);
        body.put("content", content);

        ApiClient.get().addComment(articleId, body).enqueue(new Callback<ApiComment>() {
            @Override
            public void onResponse(Call<ApiComment> call, Response<ApiComment> resp) {
                if (!resp.isSuccessful()) return;
                binding.etComment.setText("");
                loadComments(articleId);
            }
            @Override public void onFailure(Call<ApiComment> call, Throwable t) {
                Toast.makeText(ArticleDetailActivity.this, "Không gửi được bình luận", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
