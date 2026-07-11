package com.pompom.group6.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.LinearLayoutManager;

import com.pompom.group6.MainActivity;
import com.pompom.group6.adapters.SearchProductAdapter;
import com.pompom.group6.databinding.ActivitySkinRecommendationBinding;
import com.pompom.group6.models.Product;
import com.pompom.group6.network.ApiClient;
import com.pompom.group6.network.ProductMapper;
import com.pompom.group6.network.dto.ApiProduct;
import com.pompom.group6.utils.StatusBarUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Gợi ý sản phẩm theo loại da — nối trực tiếp "Hồ sơ làn da" ở màn Me với dữ liệu sản phẩm
 * thật (GET /api/products/by-skin-type/:type). Catalog hiện là mỹ phẩm trang điểm nên gợi ý
 * dựa trên heuristic thực tế của ngành (matte/kiềm dầu cho da dầu, cushion/dưỡng ẩm cho da khô...),
 * KHÔNG phải dữ liệu giả — có thể trả về rỗng thật sự với loại da chưa có sản phẩm phù hợp.
 */
public class SkinRecommendationActivity extends SwipeBackActivity {

    private static final String EXTRA_SKIN_TYPE = "skin_type";
    private static final String EXTRA_SKIN_LABEL = "skin_label";

    private static final Map<String, String> SUBTITLES = new HashMap<>();
    static {
        SUBTITLES.put("oily", "Ưu tiên sản phẩm kiềm dầu, lì, lâu trôi — hợp với da dễ đổ dầu");
        SUBTITLES.put("dry", "Ưu tiên sản phẩm dưỡng ẩm, dạng cushion mềm mịn — hợp với da thiếu ẩm");
        SUBTITLES.put("combination", "Kết hợp cả kiềm dầu và dưỡng ẩm để cân bằng từng vùng da");
        SUBTITLES.put("sensitive", "Ưu tiên sản phẩm dịu nhẹ, ít thành phần gây kích ứng");
        SUBTITLES.put("normal", "Da thường hợp với hầu hết sản phẩm — đây là các sản phẩm được yêu thích nhất");
    }

    private ActivitySkinRecommendationBinding binding;

    public static void start(Context context, String skinTypeCode, String skinTypeLabel) {
        Intent i = new Intent(context, SkinRecommendationActivity.class);
        i.putExtra(EXTRA_SKIN_TYPE, skinTypeCode);
        i.putExtra(EXTRA_SKIN_LABEL, skinTypeLabel);
        context.startActivity(i);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySkinRecommendationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        StatusBarUtils.applyTransparent(this);

        String skinType = getIntent().getStringExtra(EXTRA_SKIN_TYPE);
        String skinLabel = getIntent().getStringExtra(EXTRA_SKIN_LABEL);
        if (skinType == null) skinType = "normal";
        if (skinLabel == null) skinLabel = "bạn";

        // "Phù hợp với" chứ không phải "Gợi ý cho" — đây là lọc theo chất/finish sản phẩm
        // (mỹ phẩm trang điểm), không phải kết quả khám da/chẩn đoán skincare.
        binding.tvTitle.setText("Phù hợp với " + skinLabel);
        binding.tvSubtitle.setText(SUBTITLES.getOrDefault(skinType.toLowerCase(),
                "Gợi ý sản phẩm phù hợp với làn da của bạn"));

        SearchProductAdapter adapter = new SearchProductAdapter();
        binding.rvRecommended.setLayoutManager(new LinearLayoutManager(this));
        binding.rvRecommended.setAdapter(adapter);

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnBrowseAll.setOnClickListener(v -> {
            Intent i = new Intent(this, MainActivity.class);
            i.putExtra(MainActivity.EXTRA_TAB, 1); // Shop
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(i);
            finish();
        });

        loadRecommendations(skinType, adapter);
    }

    private void loadRecommendations(String skinType, SearchProductAdapter adapter) {
        ApiClient.get().getProductsBySkinType(skinType).enqueue(new Callback<List<ApiProduct>>() {
            @Override
            public void onResponse(Call<List<ApiProduct>> call, Response<List<ApiProduct>> resp) {
                if (binding == null) return;
                List<Product> results = new ArrayList<>();
                if (resp.isSuccessful() && resp.body() != null) {
                    for (ApiProduct a : resp.body()) results.add(ProductMapper.toProduct(a));
                }
                adapter.setProducts(results);
                showEmpty(results.isEmpty());
            }

            @Override
            public void onFailure(Call<List<ApiProduct>> call, Throwable t) {
                if (binding == null) return;
                binding.tvEmptyMessage.setText("Không kết nối được máy chủ. Vui lòng thử lại sau.");
                showEmpty(true);
            }
        });
    }

    private void showEmpty(boolean empty) {
        binding.rvRecommended.setVisibility(empty ? View.GONE : View.VISIBLE);
        binding.emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
    }
}
