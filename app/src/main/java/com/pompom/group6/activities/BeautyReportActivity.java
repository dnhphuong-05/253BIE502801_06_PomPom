package com.pompom.group6.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.pompom.group6.R;
import com.pompom.group6.adapters.AiSessionAdapter;
import com.pompom.group6.database.AiSessionDAO;
import com.pompom.group6.databinding.ActivityBeautyReportBinding;
import com.pompom.group6.models.AiSession;
import com.pompom.group6.utils.StatusBarUtils;

import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Beauty Report — tổng hợp XU HƯỚNG qua tất cả lượt khám AI Dermatologist đã lưu (khác với màn
 * Lịch sử vốn chỉ liệt kê từng lượt riêng lẻ): trung bình 5 chỉ số + so sánh lần đầu/lần gần nhất.
 */
public class BeautyReportActivity extends SwipeBackActivity {

    public static void start(Context context) {
        context.startActivity(new Intent(context, BeautyReportActivity.class));
    }

    private ActivityBeautyReportBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.applyPinkHeader(this);

        binding = ActivityBeautyReportBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.btnBack.setOnClickListener(v -> finish());

        List<AiSession> sessions = new AiSessionDAO(this).getHistory(AiSession.TYPE_DERMATOLOGIST, 200);
        if (sessions.isEmpty()) {
            binding.layoutEmpty.setVisibility(View.VISIBLE);
            binding.btnStartCall.setOnClickListener(v -> {
                startActivity(new Intent(this, AiCallActivity.class));
                finish();
            });
        } else {
            binding.scrollContent.setVisibility(View.VISIBLE);
            bindReport(sessions);
        }
    }

    private void bindReport(List<AiSession> sessions) {
        // getHistory() trả về mới nhất trước (ORDER BY id DESC).
        AiSession newest = sessions.get(0);
        AiSession oldest = sessions.get(sessions.size() - 1);

        binding.tvSessionCount.setText("Đã khám da " + sessions.size() + " lần");
        binding.tvDateRange.setText(sessions.size() > 1
                ? "Từ " + formatDate(oldest.getCreatedAt()) + " đến " + formatDate(newest.getCreatedAt())
                : "Lần đầu tiên: " + formatDate(newest.getCreatedAt()));

        Map<String, Integer> sum = new HashMap<>();
        for (AiSession s : sessions) {
            Map<String, Integer> metrics = parseMetrics(s.getSkinAnalysis());
            for (Map.Entry<String, Integer> e : metrics.entrySet()) {
                sum.merge(e.getKey(), e.getValue(), Integer::sum);
            }
        }
        Map<String, Integer> oldestMetrics = parseMetrics(oldest.getSkinAnalysis());
        Map<String, Integer> newestMetrics = parseMetrics(newest.getSkinAnalysis());

        // Với oil/acne/pores/wrinkles: giảm là tốt hơn. Với tone: tăng là tốt hơn.
        int avgOil = bindMetricRow("oil", sum, sessions.size(), oldestMetrics, newestMetrics, binding.tvAvgOil, binding.tvTrendOil, false);
        int avgAcne = bindMetricRow("acne", sum, sessions.size(), oldestMetrics, newestMetrics, binding.tvAvgAcne, binding.tvTrendAcne, false);
        int avgPores = bindMetricRow("pores", sum, sessions.size(), oldestMetrics, newestMetrics, binding.tvAvgPores, binding.tvTrendPores, false);
        int avgWrinkles = bindMetricRow("wrinkles", sum, sessions.size(), oldestMetrics, newestMetrics, binding.tvAvgWrinkles, binding.tvTrendWrinkles, false);
        int avgTone = bindMetricRow("tone", sum, sessions.size(), oldestMetrics, newestMetrics, binding.tvAvgTone, binding.tvTrendTone, true);

        binding.radarChart.setValues(new int[]{avgOil, avgAcne, avgPores, avgWrinkles, avgTone});

        String recommendation = newest.getRecommendationSkincare();
        binding.tvLatestRecommendation.setText(!TextUtils.isEmpty(recommendation)
                ? recommendation : "Chưa có nhận xét.");

        List<AiSession> recent = sessions.subList(0, Math.min(5, sessions.size()));
        binding.rvRecentSessions.setLayoutManager(new LinearLayoutManager(this));
        binding.rvRecentSessions.setAdapter(new AiSessionAdapter(recent,
                session -> AiResultActivity.start(this, session.getId())));

        binding.tvViewAll.setOnClickListener(v ->
                AiHistoryActivity.startFiltered(this, AiSession.TYPE_DERMATOLOGIST));
    }

    /** @return giá trị trung bình (0-100) — dùng để vẽ radar chart. */
    private int bindMetricRow(String key, Map<String, Integer> sum, int count,
                               Map<String, Integer> oldestMetrics, Map<String, Integer> newestMetrics,
                               TextView tvAvg, TextView tvTrend, boolean higherIsBetter) {
        int avg = Math.round(sum.getOrDefault(key, 0) / (float) count);
        tvAvg.setText("Trung bình " + avg + "%");

        Integer oldVal = oldestMetrics.get(key);
        Integer newVal = newestMetrics.get(key);
        if (oldVal == null || newVal == null || oldVal.equals(newVal)) {
            tvTrend.setText("— Không đổi");
            tvTrend.setTextColor(getResources().getColor(R.color.text_secondary));
            return avg;
        }
        int delta = newVal - oldVal;
        boolean improved = higherIsBetter ? delta > 0 : delta < 0;
        String arrow = delta > 0 ? "▲" : "▼";
        tvTrend.setText(arrow + " " + Math.abs(delta) + "%");
        tvTrend.setTextColor(getResources().getColor(improved ? R.color.ai_hub_advisor_btn : R.color.ai_hub_dermatologist_btn));
        return avg;
    }

    private Map<String, Integer> parseMetrics(String skinAnalysis) {
        Map<String, Integer> result = new HashMap<>();
        if (TextUtils.isEmpty(skinAnalysis)) return result;
        for (String part : skinAnalysis.split(",")) {
            String[] kv = part.trim().split(":");
            if (kv.length == 2) {
                try {
                    result.put(kv[0].trim().toLowerCase(Locale.ROOT), Integer.parseInt(kv[1].trim()));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return result;
    }

    private String formatDate(String rawCreatedAt) {
        if (TextUtils.isEmpty(rawCreatedAt)) return "";
        try {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat output = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return output.format(input.parse(rawCreatedAt));
        } catch (Exception e) {
            return rawCreatedAt;
        }
    }
}
