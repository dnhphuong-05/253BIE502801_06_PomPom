package com.pompom.group6.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.pompom.group6.adapters.AiSessionAdapter;
import com.pompom.group6.database.AiSessionDAO;
import com.pompom.group6.databinding.ActivityAiHistoryBinding;
import com.pompom.group6.models.AiSession;
import com.pompom.group6.utils.StatusBarUtils;

import java.util.List;

/** Danh sách các lượt đã dùng AI Dermatologist/AI Makeup Artist, mới nhất trước. */
public class AiHistoryActivity extends SwipeBackActivity {

    private static final String EXTRA_FILTER_TYPE = "extra_filter_type";

    public static void start(Context context) {
        context.startActivity(new Intent(context, AiHistoryActivity.class));
    }

    /** @param aiType lọc theo loại (vd AiSession.TYPE_DERMATOLOGIST cho "Beauty report"). */
    public static void startFiltered(Context context, String aiType) {
        Intent intent = new Intent(context, AiHistoryActivity.class);
        intent.putExtra(EXTRA_FILTER_TYPE, aiType);
        context.startActivity(intent);
    }

    private ActivityAiHistoryBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.applyPinkHeader(this);

        binding = ActivityAiHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        String filterType = getIntent().getStringExtra(EXTRA_FILTER_TYPE);
        if (AiSession.TYPE_DERMATOLOGIST.equals(filterType)) {
            binding.tvTitle.setText("Beauty Report");
        }

        List<AiSession> sessions = new AiSessionDAO(this).getHistory(filterType, 50);
        if (sessions.isEmpty()) {
            binding.layoutEmpty.setVisibility(View.VISIBLE);
            binding.rvSessions.setVisibility(View.GONE);
            return;
        }

        binding.rvSessions.setLayoutManager(new LinearLayoutManager(this));
        binding.rvSessions.setAdapter(new AiSessionAdapter(sessions,
                session -> AiResultActivity.start(this, session.getId())));
    }
}
