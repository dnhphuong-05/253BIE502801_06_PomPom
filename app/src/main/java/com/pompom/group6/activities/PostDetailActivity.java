package com.pompom.group6.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.pompom.group6.R;
import com.pompom.group6.adapters.CommentAdapter;
import com.pompom.group6.adapters.ProductTagAdapter;
import com.pompom.group6.database.CommunityDAO;
import com.pompom.group6.databinding.ActivityPostDetailBinding;
import com.pompom.group6.models.Comment;
import com.pompom.group6.models.CommunityPost;
import com.pompom.group6.models.Product;

import java.util.List;

public class PostDetailActivity extends SwipeBackActivity {

    private ActivityPostDetailBinding binding;
    private String postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Full screen / Transparent status bar with dark icons and matching nav bar
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        }

        getWindow().getDecorView().setSystemUiVisibility(flags);
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
        getWindow().setNavigationBarColor(android.graphics.Color.WHITE);

        binding = ActivityPostDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        postId = getIntent().getStringExtra("post_id");

        setupListeners();
        loadPostData();
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
        
        binding.btnFollow.setOnClickListener(v -> {
            boolean isFollowed = v.getTag() != null && (boolean) v.getTag();
            com.google.android.material.button.MaterialButton btn = (com.google.android.material.button.MaterialButton) v;
            
            if (isFollowed) {
                v.setTag(false);
                btn.setText("Theo dõi");
                btn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.brand_pink_light));
                btn.setTextColor(ContextCompat.getColor(this, R.color.brand_pink));
                Toast.makeText(this, "Đã hủy theo dõi", Toast.LENGTH_SHORT).show();
            } else {
                v.setTag(true);
                btn.setText("Đang theo dõi");
                btn.setBackgroundTintList(ContextCompat.getColorStateList(this, R.color.text_secondary));
                btn.setTextColor(android.graphics.Color.WHITE);
                Toast.makeText(this, "Đã theo dõi người dùng này", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadPostData() {
        if (postId == null) return;

        // 1) Bài viết + tác giả (từ MongoDB).
        com.pompom.group6.network.ApiClient.get().getCommunityPost(postId)
                .enqueue(new retrofit2.Callback<com.pompom.group6.network.dto.ApiCommunityPost>() {
                    @Override
                    public void onResponse(retrofit2.Call<com.pompom.group6.network.dto.ApiCommunityPost> call,
                                           retrofit2.Response<com.pompom.group6.network.dto.ApiCommunityPost> resp) {
                        if (binding == null || !resp.isSuccessful() || resp.body() == null) return;
                        com.pompom.group6.network.dto.ApiCommunityPost p = resp.body();
                        binding.tvAuthorName.setText(p.authorName);
                        binding.tvPostTitle.setText(p.content);
                        binding.tvPostContent.setText(p.content + "\n\nCảm ơn mọi người đã xem bài viết của mình!");
                        binding.tvLikeCount.setText(formatCount(p.likeCount));
                        binding.tvCommentCount.setText(String.valueOf(p.commentCount));
                        binding.tvCommentSectionTitle.setText("Bình luận (" + p.commentCount + ")");

                        String img = p.images != null && !p.images.isEmpty() ? p.images.get(0) : null;
                        Glide.with(PostDetailActivity.this).load(img).into(binding.ivPostImage);
                        Glide.with(PostDetailActivity.this).load(p.authorAvatar).circleCrop()
                                .placeholder(R.drawable.logo_pompom).into(binding.ivAuthorAvatar);
                    }
                    @Override public void onFailure(retrofit2.Call<com.pompom.group6.network.dto.ApiCommunityPost> call, Throwable t) {}
                });

        // 2) Sản phẩm được gắn thẻ.
        com.pompom.group6.network.ApiClient.get().getPostTaggedProducts(postId)
                .enqueue(new retrofit2.Callback<List<com.pompom.group6.network.dto.ApiProduct>>() {
                    @Override
                    public void onResponse(retrofit2.Call<List<com.pompom.group6.network.dto.ApiProduct>> call,
                                           retrofit2.Response<List<com.pompom.group6.network.dto.ApiProduct>> resp) {
                        if (binding == null || !resp.isSuccessful() || resp.body() == null) return;
                        List<Product> tagged = new java.util.ArrayList<>();
                        for (com.pompom.group6.network.dto.ApiProduct a : resp.body()) {
                            tagged.add(com.pompom.group6.network.ProductMapper.toProduct(a));
                        }
                        if (!tagged.isEmpty()) {
                            binding.layoutTaggedProducts.setVisibility(View.VISIBLE);
                            binding.rvTaggedProducts.setAdapter(new ProductTagAdapter(tagged));
                        } else {
                            binding.layoutTaggedProducts.setVisibility(View.GONE);
                        }
                    }
                    @Override public void onFailure(retrofit2.Call<List<com.pompom.group6.network.dto.ApiProduct>> call, Throwable t) {}
                });

        // 3) Bình luận.
        binding.rvComments.setLayoutManager(new LinearLayoutManager(this));
        com.pompom.group6.network.ApiClient.get().getPostComments(postId)
                .enqueue(new retrofit2.Callback<List<com.pompom.group6.network.dto.ApiComment>>() {
                    @Override
                    public void onResponse(retrofit2.Call<List<com.pompom.group6.network.dto.ApiComment>> call,
                                           retrofit2.Response<List<com.pompom.group6.network.dto.ApiComment>> resp) {
                        if (binding == null || !resp.isSuccessful() || resp.body() == null) return;
                        List<Comment> comments = new java.util.ArrayList<>();
                        for (com.pompom.group6.network.dto.ApiComment c : resp.body()) {
                            comments.add(new Comment(0, 0, c.authorName, c.authorAvatar, c.content, c.createdAt));
                        }
                        binding.rvComments.setAdapter(new CommentAdapter(comments));
                    }
                    @Override public void onFailure(retrofit2.Call<List<com.pompom.group6.network.dto.ApiComment>> call, Throwable t) {}
                });
    }

    private String formatCount(int count) {
        if (count >= 1000) {
            return String.format("%.1fK", count / 1000.0);
        }
        return String.valueOf(count);
    }
}
