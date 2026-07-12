package com.pompom.group6.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.pompom.group6.R;
import com.pompom.group6.adapters.ConsultationHistoryAdapter;
import com.pompom.group6.databinding.ActivityConsultationHistoryBinding;
import com.pompom.group6.network.ApiClient;
import com.pompom.group6.network.Session;
import com.pompom.group6.network.dto.ApiConsultationRequest;
import com.pompom.group6.utils.StatusBarUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Màn "Lịch sử tư vấn": đọc thật từ GET /api/consultation-requests?user_id=. */
public class ConsultationHistoryActivity extends SwipeBackActivity {

    private ActivityConsultationHistoryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityConsultationHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        StatusBarUtils.applyPinkHeader(this);
        StatusBarUtils.applyHeaderContentInsets(this, binding.header.getRoot(), binding.getRoot());

        binding.header.tvHeaderTitle.setText("Lịch sử tư vấn");
        binding.header.btnBack.setOnClickListener(v -> finish());

        binding.rvHistory.setLayoutManager(new LinearLayoutManager(this));
        binding.emptyState.tvEmptyText.setText("Bạn chưa gửi yêu cầu tư vấn nào");
        binding.emptyState.ivEmptyIcon.setImageResource(R.drawable.ic_steth);
        binding.emptyState.btnEmptyCta.setText("Gửi yêu cầu tư vấn mới");
        binding.emptyState.btnEmptyCta.setVisibility(View.VISIBLE);
        binding.emptyState.btnEmptyCta.setOnClickListener(v ->
                startActivity(new Intent(this, ConsultationRequestActivity.class)));

        binding.fabNewRequest.setOnClickListener(v ->
                startActivity(new Intent(this, ConsultationRequestActivity.class)));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // onResume luôn chạy ngay sau onCreate ở lần mở đầu tiên, và chạy lại khi quay về
        // từ ConsultationRequestActivity sau khi gửi yêu cầu mới -> 1 nơi duy nhất để tải/làm mới.
        String userOid = Session.getUserOid(this);
        if (userOid == null) {
            binding.emptyState.getRoot().setVisibility(View.VISIBLE);
            return;
        }
        loadHistory(userOid);
    }

    private void loadHistory(String userOid) {
        ApiClient.get().getConsultationRequests(userOid).enqueue(new Callback<List<ApiConsultationRequest>>() {
            @Override
            public void onResponse(Call<List<ApiConsultationRequest>> call, Response<List<ApiConsultationRequest>> resp) {
                if (binding == null) return;
                List<ApiConsultationRequest> list = resp.isSuccessful() && resp.body() != null
                        ? resp.body() : new ArrayList<>();
                binding.rvHistory.setAdapter(new ConsultationHistoryAdapter(list));
                binding.emptyState.getRoot().setVisibility(list.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onFailure(Call<List<ApiConsultationRequest>> call, Throwable t) {
                if (binding == null) return;
                binding.emptyState.getRoot().setVisibility(View.VISIBLE);
            }
        });
    }
}
