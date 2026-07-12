package com.pompom.group6.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayoutMediator;
import com.pompom.group6.R;
import com.pompom.group6.adapters.CommentAdapter;
import com.pompom.group6.adapters.PostImageSliderAdapter;
import com.pompom.group6.databinding.ActivityPostDetailBinding;
import com.pompom.group6.models.Comment;
import com.pompom.group6.network.ApiClient;
import com.pompom.group6.network.Session;
import com.pompom.group6.network.dto.ApiCommunityPost;
import com.pompom.group6.utils.TimeUtils;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDetailActivity extends SwipeBackActivity {

    private ActivityPostDetailBinding binding;
    private String postId;
    private String authorId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Màn này không cần edge-to-edge: để hệ thống tự chừa khoảng (padding) cho status bar
        // và nav bar như bình thường — cách này đã chứng minh ổn định trên máy thật, tránh
        // header bị lệch màu với status bar và nội dung bị đè xuống thanh điều hướng dưới.
        getWindow().setStatusBarColor(Color.WHITE);
        getWindow().setNavigationBarColor(Color.WHITE);
        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(true);
        controller.setAppearanceLightNavigationBars(true);

        binding = ActivityPostDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        postId = getIntent().getStringExtra("post_id");

        setupListeners();
        loadPostData();
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
    }

    /** Theo dõi/bỏ theo dõi tác giả — lưu thật qua API, tạo thông báo cho người được theo dõi. */
    private void setupFollowButton() {
        String viewerId = Session.getUserOid(this);
        boolean canFollow = authorId != null && viewerId != null && !authorId.equals(viewerId);
        binding.btnFollow.setVisibility(canFollow ? View.VISIBLE : View.GONE);
        if (!canFollow) return;

        renderFollowButton(false);
        ApiClient.get().getFollowStatus(authorId, viewerId).enqueue(new Callback<Map<String, Boolean>>() {
            @Override
            public void onResponse(Call<Map<String, Boolean>> call, Response<Map<String, Boolean>> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    renderFollowButton(Boolean.TRUE.equals(resp.body().get("following")));
                }
            }
            @Override public void onFailure(Call<Map<String, Boolean>> call, Throwable t) {}
        });

        binding.btnFollow.setOnClickListener(v -> {
            boolean isFollowed = v.getTag() != null && (boolean) v.getTag();
            if (isFollowed) {
                ApiClient.get().unfollowUser(authorId, viewerId).enqueue(new Callback<Map<String, Boolean>>() {
                    @Override
                    public void onResponse(Call<Map<String, Boolean>> call, Response<Map<String, Boolean>> resp) {
                        if (resp.isSuccessful()) {
                            renderFollowButton(false);
                            Toast.makeText(PostDetailActivity.this, "Đã hủy theo dõi", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override public void onFailure(Call<Map<String, Boolean>> call, Throwable t) {}
                });
            } else {
                Map<String, String> body = new java.util.HashMap<>();
                body.put("follower_id", viewerId);
                ApiClient.get().followUser(authorId, body).enqueue(new Callback<Map<String, Boolean>>() {
                    @Override
                    public void onResponse(Call<Map<String, Boolean>> call, Response<Map<String, Boolean>> resp) {
                        if (resp.isSuccessful()) {
                            renderFollowButton(true);
                            Toast.makeText(PostDetailActivity.this, "Đã theo dõi người dùng này", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override public void onFailure(Call<Map<String, Boolean>> call, Throwable t) {}
                });
            }
        });
    }

    private void renderFollowButton(boolean following) {
        MaterialButton btn = binding.btnFollow;
        btn.setTag(following);
        if (following) {
            btn.setText("Đang theo dõi");
            btn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.text_secondary));
            btn.setTextColor(Color.WHITE);
        } else {
            btn.setText("+ Theo dõi");
            btn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.brand_pink_light));
            btn.setTextColor(ContextCompat.getColor(this, R.color.brand_pink));
        }
    }

    private void loadPostData() {
        if (postId == null) return;

        // 1) Bài viết + tác giả (từ MongoDB) — nội dung, số thích/bình luận/chia sẻ đều thật.
        ApiClient.get().getCommunityPost(postId).enqueue(new Callback<ApiCommunityPost>() {
            @Override
            public void onResponse(Call<ApiCommunityPost> call, Response<ApiCommunityPost> resp) {
                if (binding == null || !resp.isSuccessful() || resp.body() == null) return;
                ApiCommunityPost p = resp.body();
                authorId = p.userId;
                binding.tvAuthorName.setText(p.authorName);
                binding.tvPostTitle.setText(p.content);
                binding.tvPostContent.setText(p.content);
                binding.tvPostTime.setText(TimeUtils.relativeTime(p.createdAt));
                binding.tvLikeCount.setText(formatCount(p.likeCount));
                binding.tvShareCount.setText(formatCount(p.shareCount));
                // Số bình luận hiển thị dựa trên danh sách bình luận thật tải được bên dưới
                // (không dùng comment_count lưu sẵn — có thể lệch với số bản ghi Comment thật).

                bindImages(p.images);
                Glide.with(PostDetailActivity.this).load(p.authorAvatar).circleCrop()
                        .placeholder(R.drawable.logo_pompom).into(binding.ivAuthorAvatar);

                setupFollowButton();
            }
            @Override public void onFailure(Call<ApiCommunityPost> call, Throwable t) {}
        });

        // 2) Chia sẻ — tăng lượt chia sẻ thật rồi mở hộp thoại chia sẻ hệ thống.
        binding.btnShare.setOnClickListener(v -> {
            ApiClient.get().sharePost(postId).enqueue(new Callback<Map<String, Integer>>() {
                @Override
                public void onResponse(Call<Map<String, Integer>> call, Response<Map<String, Integer>> resp) {
                    if (binding != null && resp.isSuccessful() && resp.body() != null && resp.body().get("share_count") != null) {
                        binding.tvShareCount.setText(formatCount(resp.body().get("share_count")));
                    }
                }
                @Override public void onFailure(Call<Map<String, Integer>> call, Throwable t) {}
            });
        });

        // 3) Bình luận thật.
        binding.rvComments.setLayoutManager(new LinearLayoutManager(this));
        loadComments();
        setupCommentInput();
    }

    private void loadComments() {
        ApiClient.get().getPostComments(postId).enqueue(new Callback<List<com.pompom.group6.network.dto.ApiComment>>() {
            @Override
            public void onResponse(Call<List<com.pompom.group6.network.dto.ApiComment>> call,
                                   Response<List<com.pompom.group6.network.dto.ApiComment>> resp) {
                if (binding == null || !resp.isSuccessful() || resp.body() == null) return;
                List<Comment> comments = new java.util.ArrayList<>();
                for (com.pompom.group6.network.dto.ApiComment c : resp.body()) {
                    comments.add(new Comment(0, 0, c.authorName, c.authorAvatar, c.content,
                            TimeUtils.relativeTime(c.createdAt)));
                }
                binding.rvComments.setAdapter(new CommentAdapter(comments));
                binding.tvCommentCount.setText(String.valueOf(comments.size()));
                binding.tvCommentSectionTitle.setText("Bình luận (" + comments.size() + ")");
            }
            @Override public void onFailure(Call<List<com.pompom.group6.network.dto.ApiComment>> call, Throwable t) {}
        });
    }

    /** Bấm Enter/nút gửi trên bàn phím để đăng bình luận thật, lưu qua MongoDB. */
    private void setupCommentInput() {
        binding.etComment.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                submitComment();
                return true;
            }
            return false;
        });
    }

    private void submitComment() {
        String userOid = Session.getUserOid(this);
        if (userOid == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để bình luận", Toast.LENGTH_SHORT).show();
            return;
        }
        String content = binding.etComment.getText().toString().trim();
        if (content.isEmpty()) return;

        Map<String, String> body = new java.util.HashMap<>();
        body.put("user_id", userOid);
        body.put("content", content);
        ApiClient.get().addComment(postId, body).enqueue(new Callback<com.pompom.group6.network.dto.ApiComment>() {
            @Override
            public void onResponse(Call<com.pompom.group6.network.dto.ApiComment> call,
                                   Response<com.pompom.group6.network.dto.ApiComment> resp) {
                if (binding == null || !resp.isSuccessful()) return;
                binding.etComment.setText("");
                loadComments();
            }
            @Override public void onFailure(Call<com.pompom.group6.network.dto.ApiComment> call, Throwable t) {
                Toast.makeText(PostDetailActivity.this, "Không gửi được bình luận", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** Slider ảnh kiểu Instagram (vuốt + chấm chỉ vị trí) — ẩn hẳn cả khối khi bài không có ảnh
     * (trước đây luôn hiện 1 ImageView tĩnh dù bài không có ảnh, và chỉ load được ảnh đầu tiên). */
    private void bindImages(List<String> images) {
        if (images == null || images.isEmpty()) {
            binding.layoutPostImages.setVisibility(View.GONE);
            return;
        }
        binding.layoutPostImages.setVisibility(View.VISIBLE);
        binding.vpPostImages.setAdapter(new PostImageSliderAdapter(images));
        if (images.size() > 1) {
            binding.tabIndicator.setVisibility(View.VISIBLE);
            new TabLayoutMediator(binding.tabIndicator, binding.vpPostImages, (tab, pos) -> {}).attach();
        } else {
            binding.tabIndicator.setVisibility(View.GONE);
        }
    }

    private String formatCount(int count) {
        if (count >= 1000) {
            return String.format("%.1fK", count / 1000.0);
        }
        return String.valueOf(count);
    }
}
