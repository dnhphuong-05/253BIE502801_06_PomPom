package com.pompom.group6.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.pompom.group6.adapters.NotificationAdapter;
import com.pompom.group6.databinding.ActivityNotificationBinding;
import com.pompom.group6.network.ApiClient;
import com.pompom.group6.network.Session;
import com.pompom.group6.network.dto.ApiNotification;
import com.pompom.group6.utils.StatusBarUtils;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Thông báo thật: người khác thích/bình luận bài viết của bạn, hoặc bắt đầu theo dõi bạn. */
public class NotificationActivity extends SwipeBackActivity {

    private ActivityNotificationBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        StatusBarUtils.applyTransparent(this);

        binding.btnBack.setOnClickListener(v -> finish());
        binding.rvNotifications.setLayoutManager(new LinearLayoutManager(this));

        loadNotifications();
    }

    private void loadNotifications() {
        String userOid = Session.getUserOid(this);
        if (userOid == null) {
            showEmpty("Đăng nhập để xem thông báo");
            return;
        }

        ApiClient.get().getNotifications(userOid).enqueue(new Callback<List<ApiNotification>>() {
            @Override
            public void onResponse(Call<List<ApiNotification>> call, Response<List<ApiNotification>> resp) {
                if (binding == null) return;
                List<ApiNotification> list = resp.isSuccessful() ? resp.body() : null;
                if (list == null || list.isEmpty()) {
                    showEmpty("Chưa có thông báo nào");
                    return;
                }
                binding.layoutEmptyState.setVisibility(View.GONE);
                binding.rvNotifications.setVisibility(View.VISIBLE);
                binding.rvNotifications.setAdapter(new NotificationAdapter(list, NotificationActivity.this::openNotification));
            }

            @Override
            public void onFailure(Call<List<ApiNotification>> call, Throwable t) {
                showEmpty("Không tải được thông báo — kiểm tra kết nối mạng");
            }
        });
    }

    /** Thích/bình luận -> mở đúng bài viết; theo dõi/khuyến mãi hiện chưa có màn đích nên bỏ qua. */
    private void openNotification(ApiNotification n) {
        if (("like".equals(n.type) || "comment".equals(n.type)) && n.postId != null) {
            Intent intent = new Intent(this, PostDetailActivity.class);
            intent.putExtra("post_id", n.postId);
            startActivity(intent);
        }
    }

    private void showEmpty(String message) {
        if (binding == null) return;
        binding.rvNotifications.setVisibility(View.GONE);
        binding.layoutEmptyState.setVisibility(View.VISIBLE);
        binding.tvEmptyState.setText(message);
    }
}
