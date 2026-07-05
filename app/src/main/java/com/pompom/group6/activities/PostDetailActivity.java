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
    private CommunityDAO communityDAO;
    private int postId;

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

        postId = getIntent().getIntExtra("post_id", -1);
        communityDAO = new CommunityDAO(this);

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
        if (postId == -1) return;

        CommunityPost post = communityDAO.getPostById(postId);
        if (post != null) {
            binding.tvAuthorName.setText(post.getUserName());
            binding.tvPostTitle.setText(post.getContent());
            binding.tvPostContent.setText(post.getContent() + "\n\nCảm ơn mọi người đã xem bài viết của mình!");
            binding.tvLikeCount.setText(formatCount(post.getLikeCount()));
            binding.tvCommentCount.setText(String.valueOf(post.getCommentCount()));
            binding.tvCommentSectionTitle.setText("Bình luận (" + post.getCommentCount() + ")");

            Glide.with(this).load(post.getImageUrl()).into(binding.ivPostImage);
            Glide.with(this).load(post.getUserAvatar()).circleCrop().placeholder(R.drawable.logo_pompom).into(binding.ivAuthorAvatar);

            // Load Tagged Products
            List<Product> taggedProducts = communityDAO.getTaggedProducts(postId);
            if (!taggedProducts.isEmpty()) {
                binding.layoutTaggedProducts.setVisibility(View.VISIBLE);
                ProductTagAdapter tagAdapter = new ProductTagAdapter(taggedProducts);
                binding.rvTaggedProducts.setAdapter(tagAdapter);
            } else {
                binding.layoutTaggedProducts.setVisibility(View.GONE);
            }

            // Load Comments
            List<Comment> comments = communityDAO.getCommentsForPost(postId);
            CommentAdapter commentAdapter = new CommentAdapter(comments);
            binding.rvComments.setLayoutManager(new LinearLayoutManager(this));
            binding.rvComments.setAdapter(commentAdapter);
        }
    }

    private String formatCount(int count) {
        if (count >= 1000) {
            return String.format("%.1fK", count / 1000.0);
        }
        return String.valueOf(count);
    }
}
