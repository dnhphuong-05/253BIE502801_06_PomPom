package com.pompom.group6.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.chip.Chip;
import com.pompom.group6.R;
import com.pompom.group6.adapters.ProductAdapter;
import com.pompom.group6.database.AiSessionDAO;
import com.pompom.group6.database.ProductDAO;
import com.pompom.group6.databinding.ActivityAiResultBinding;
import com.pompom.group6.models.AiSession;
import com.pompom.group6.models.Product;
import com.pompom.group6.utils.StatusBarUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/**
 * Kết quả một lượt dùng AI Dermatologist hoặc AI Makeup Artist — luôn đọc lại từ SQLite qua
 * {@link AiSessionDAO} bằng {@link #EXTRA_SESSION_ID} (dùng chung cho cả lúc vừa kết thúc lượt
 * gọi lẫn lúc mở lại từ màn Lịch sử, vì dữ liệu đã được lưu trước khi mở màn này).
 */
public class AiResultActivity extends SwipeBackActivity {

    private static final String EXTRA_SESSION_ID = "extra_session_id";

    public static void start(Context context, long sessionId) {
        Intent intent = new Intent(context, AiResultActivity.class);
        intent.putExtra(EXTRA_SESSION_ID, sessionId);
        context.startActivity(intent);
    }

    private ActivityAiResultBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.applyPinkHeader(this);

        binding = ActivityAiResultBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        long sessionId = getIntent().getLongExtra(EXTRA_SESSION_ID, -1);
        AiSession session = sessionId != -1
                ? new AiSessionDAO(this).getSessionById(sessionId) : null;
        if (session == null) {
            Toast.makeText(this, "Không tìm thấy kết quả này", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bindSession(session);
        loadSuggestedProducts();

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnDone.setOnClickListener(v -> finish());
        binding.btnViewHistory.setOnClickListener(v -> {
            startActivity(new Intent(this, AiHistoryActivity.class));
            finish();
        });
    }

    private void bindSession(AiSession session) {
        binding.tvCreatedAt.setText(formatDate(session.getCreatedAt()));

        if (AiSession.TYPE_DERMATOLOGIST.equals(session.getAiType())) {
            binding.tvTitle.setText("Kết quả khám da");
            binding.tvRecommendationLabel.setText("Lời khuyên từ bác sĩ AI");
            bindDermatologistResult(session);
        } else {
            binding.tvTitle.setText("Kết quả buổi trang điểm");
            binding.tvRecommendationLabel.setText("Nhận xét từ AI Makeup Artist");
            bindMakeupResult(session);
        }

        String recommendation = AiSession.TYPE_DERMATOLOGIST.equals(session.getAiType())
                ? session.getRecommendationSkincare() : session.getOutputData();
        binding.tvRecommendationText.setText(!TextUtils.isEmpty(recommendation)
                ? recommendation : "Chưa có nhận xét cho lượt này.");
    }

    private void bindDermatologistResult(AiSession session) {
        binding.cardMetrics.setVisibility(android.view.View.VISIBLE);
        binding.tvConfidence.setText("Độ tin cậy " + Math.round(session.getConfidence() * 100) + "%");

        java.util.Map<String, Integer> metrics = parseMetrics(session.getSkinAnalysis());
        setMetric(binding.pbOil, binding.tvOilVal, metrics.get("oil"));
        setMetric(binding.pbAcne, binding.tvAcneVal, metrics.get("acne"));
        setMetric(binding.pbPores, binding.tvPoresVal, metrics.get("pores"));
        setMetric(binding.pbWrinkles, binding.tvWrinklesVal, metrics.get("wrinkles"));
        setMetric(binding.pbTone, binding.tvToneVal, metrics.get("tone"));
    }

    private void setMetric(android.widget.ProgressBar bar, android.widget.TextView label, Integer value) {
        int v = value != null ? value : 0;
        bar.setProgress(v);
        label.setText(v + "%");
    }

    /** Parse chuỗi "Oil:85,Acne:62,Pores:72,Wrinkles:30,Tone:66" do AiCallActivity lưu lại. */
    private java.util.Map<String, Integer> parseMetrics(String skinAnalysis) {
        java.util.Map<String, Integer> result = new java.util.HashMap<>();
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

    private void bindMakeupResult(AiSession session) {
        binding.cardMakeupSummary.setVisibility(android.view.View.VISIBLE);
        binding.tvMakeupSummary.setText(!TextUtils.isEmpty(session.getOutputData())
                ? session.getOutputData() : "Bạn đã trải nghiệm thử trang điểm ảo cùng AI.");

        binding.cgShadesTried.removeAllViews();
        String shadesCsv = session.getMakeupProductsUsed();
        if (!TextUtils.isEmpty(shadesCsv)) {
            for (String shade : shadesCsv.split(",")) {
                if (shade.trim().isEmpty()) continue;
                Chip chip = new Chip(this);
                chip.setText(shade.trim());
                chip.setChipBackgroundColorResource(R.color.brand_pink_light);
                chip.setTextColor(getResources().getColor(R.color.brand_pink));
                chip.setChipStrokeWidth(0);
                chip.setClickable(false);
                binding.cgShadesTried.addView(chip);
            }
        }
    }

    private void loadSuggestedProducts() {
        List<Product> products = new ProductDAO(this).getBestSellers(6);
        binding.rvSuggestedProducts.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvSuggestedProducts.setAdapter(new ProductAdapter(products));
    }

    private String formatDate(String rawCreatedAt) {
        if (TextUtils.isEmpty(rawCreatedAt)) return "";
        try {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat output = new SimpleDateFormat("HH:mm 'ngày' dd/MM/yyyy", Locale.getDefault());
            return output.format(input.parse(rawCreatedAt));
        } catch (Exception e) {
            return rawCreatedAt;
        }
    }
}
