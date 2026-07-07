package com.pompom.group6.activities;

import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.pompom.group6.R;
import com.pompom.group6.adapters.CommunityPostAdapter;
import com.pompom.group6.databinding.ActivitySavedPostsBinding;
import com.pompom.group6.models.CommunityPost;
import com.pompom.group6.network.ApiClient;
import com.pompom.group6.network.Session;
import com.pompom.group6.network.dto.ApiCommunityPost;
import com.pompom.group6.utils.StatusBarUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Màn "Nội dung đã lưu": các bài cộng đồng user đã bookmark.
 * Đọc thật từ GET /api/community/posts?saved_by=<uid>&viewer_id=<uid>.
 */
public class SavedPostsActivity extends SwipeBackActivity {

    private ActivitySavedPostsBinding binding;
    private final CommunityPostAdapter adapter = new CommunityPostAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySavedPostsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        StatusBarUtils.applyPinkHeader(this);
        StatusBarUtils.applyHeaderContentInsets(this, binding.header.getRoot(), binding.getRoot());

        binding.header.tvHeaderTitle.setText("Nội dung đã lưu");
        binding.header.btnBack.setOnClickListener(v -> finish());

        binding.rvPosts.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPosts.setAdapter(adapter);
        binding.emptyState.tvEmptyText.setText("Bạn chưa lưu nội dung nào");
        binding.emptyState.ivEmptyIcon.setImageResource(R.drawable.ic_bookmark);

        String userOid = Session.getUserOid(this);
        if (userOid == null) {
            binding.emptyState.getRoot().setVisibility(View.VISIBLE);
            return;
        }
        loadSaved(userOid);
    }

    private void loadSaved(String userOid) {
        // saved_by = bài đã lưu; viewer_id = gắn đúng trạng thái tim/bookmark của người xem.
        ApiClient.get().getCommunityPostsFiltered(50, null, userOid, userOid)
                .enqueue(new Callback<List<ApiCommunityPost>>() {
                    @Override
                    public void onResponse(Call<List<ApiCommunityPost>> call, Response<List<ApiCommunityPost>> resp) {
                        if (binding == null) return;
                        List<CommunityPost> posts = mapPosts(resp.isSuccessful() ? resp.body() : null);
                        adapter.setPosts(posts);
                        binding.emptyState.getRoot().setVisibility(posts.isEmpty() ? View.VISIBLE : View.GONE);
                    }

                    @Override
                    public void onFailure(Call<List<ApiCommunityPost>> call, Throwable t) {
                        if (binding == null) return;
                        binding.emptyState.getRoot().setVisibility(View.VISIBLE);
                    }
                });
    }

    private List<CommunityPost> mapPosts(List<ApiCommunityPost> apiPosts) {
        List<CommunityPost> posts = new ArrayList<>();
        if (apiPosts == null) return posts;
        for (ApiCommunityPost a : apiPosts) {
            String img = a.images != null && !a.images.isEmpty() ? a.images.get(0) : null;
            CommunityPost cp = new CommunityPost(a.id, 0, a.content, img,
                    a.likeCount, a.commentCount, "review", a.authorName, a.authorAvatar);
            if (a.images != null) cp.setImages(a.images);
            cp.setSaved(a.isSaved);
            cp.setLiked(a.isLiked);
            cp.setAuthorId(a.userId);
            posts.add(cp);
        }
        return posts;
    }
}
