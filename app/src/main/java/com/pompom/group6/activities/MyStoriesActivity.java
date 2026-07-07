package com.pompom.group6.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.GridLayoutManager;

import com.pompom.group6.R;
import com.pompom.group6.adapters.StoryThumbAdapter;
import com.pompom.group6.databinding.ActivityMyStoriesBinding;
import com.pompom.group6.network.ApiClient;
import com.pompom.group6.network.Session;
import com.pompom.group6.network.dto.ApiNearbyPost;
import com.pompom.group6.utils.StatusBarUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** "Story đã đăng": lưới story (nearby-post 24h) của user; bấm để xem toàn màn hình. */
public class MyStoriesActivity extends SwipeBackActivity {

    private ActivityMyStoriesBinding binding;
    private StoryThumbAdapter adapter;
    private final ArrayList<ApiNearbyPost> stories = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMyStoriesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        StatusBarUtils.applyPinkHeader(this);
        StatusBarUtils.applyHeaderContentInsets(this, binding.header.getRoot(), binding.getRoot());

        binding.header.tvHeaderTitle.setText("Story đã đăng");
        binding.header.btnBack.setOnClickListener(v -> finish());

        // Tạo adapter sẵn để RecyclerView layout ngay (tránh phải cuộn mới hiện).
        adapter = new StoryThumbAdapter(this::openViewer);
        binding.rvStories.setLayoutManager(new GridLayoutManager(this, 2));
        binding.rvStories.setAdapter(adapter);

        binding.emptyState.tvEmptyText.setText("Bạn chưa đăng story nào\n(Story chỉ tồn tại trong 24 giờ)");
        binding.emptyState.ivEmptyIcon.setImageResource(R.drawable.ic_images);

        loadStories();
    }

    private void loadStories() {
        String userOid = Session.getUserOid(this);
        if (userOid == null) {
            binding.emptyState.getRoot().setVisibility(View.VISIBLE);
            return;
        }
        ApiClient.get().getUserStories(userOid).enqueue(new Callback<List<ApiNearbyPost>>() {
            @Override
            public void onResponse(Call<List<ApiNearbyPost>> call, Response<List<ApiNearbyPost>> resp) {
                if (binding == null) return;
                stories.clear();
                if (resp.isSuccessful() && resp.body() != null) stories.addAll(resp.body());
                adapter.setStories(stories);
                binding.emptyState.getRoot().setVisibility(stories.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onFailure(Call<List<ApiNearbyPost>> call, Throwable t) {
                if (binding == null) return;
                binding.emptyState.getRoot().setVisibility(View.VISIBLE);
            }
        });
    }

    private void openViewer(int position) {
        if (position < 0 || position >= stories.size()) return;
        Intent i = new Intent(this, StoryViewerActivity.class);
        i.putExtra(StoryViewerActivity.EXTRA_POSTS, stories);
        i.putExtra(StoryViewerActivity.EXTRA_START_INDEX, position);
        startActivity(i);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Story có thể hết hạn/tạo mới khi quay lại -> nạp lại cho chính xác.
        loadStories();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
