package com.pompom.group6.activities;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;

import com.pompom.group6.R;
import com.pompom.group6.databinding.ActivityLanguageBinding;
import com.pompom.group6.databinding.ItemLanguageOptionBinding;
import com.pompom.group6.utils.StatusBarUtils;

/**
 * "Ngôn ngữ": chọn 1 trong 4 ngôn ngữ. Áp dụng bằng per-app locale của AppCompat
 * ({@link AppCompatDelegate#setApplicationLocales}) — lựa chọn được lưu và tự áp dụng lại
 * khi mở lại app (nhờ AppLocalesMetadataHolderService autoStoreLocales=true trong Manifest).
 */
public class LanguageActivity extends SwipeBackActivity {

    /** Mã ngôn ngữ (BCP-47) tương ứng thư mục values-xx. */
    private static final String[] TAGS = {"vi", "en", "ja", "zh"};
    /** Tên hiển thị để nguyên bản ngữ (không dịch) để người dùng dễ nhận ra. */
    private static final String[] NAMES = {"Tiếng Việt", "English", "日本語", "中文"};

    private ActivityLanguageBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLanguageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        StatusBarUtils.applyPinkHeader(this);
        StatusBarUtils.applyHeaderContentInsets(this, binding.header.getRoot(), binding.getRoot());

        binding.header.tvHeaderTitle.setText(R.string.lang_title);
        binding.header.btnBack.setOnClickListener(v -> finish());

        buildOptions();
    }

    private void buildOptions() {
        String current = currentTag();
        binding.optionsContainer.removeAllViews();
        for (int i = 0; i < TAGS.length; i++) {
            ItemLanguageOptionBinding row = ItemLanguageOptionBinding.inflate(
                    LayoutInflater.from(this), binding.optionsContainer, false);
            row.tvLangName.setText(NAMES[i]);
            // "vi" là ngôn ngữ mặc định của app -> gắn nhãn.
            row.tvDefaultTag.setVisibility(i == 0 ? View.VISIBLE : View.GONE);
            row.ivCheck.setVisibility(TAGS[i].equals(current) ? View.VISIBLE : View.INVISIBLE);

            final String tag = TAGS[i];
            row.getRoot().setOnClickListener(v -> applyLanguage(tag, current));
            binding.optionsContainer.addView(row.getRoot());
        }
    }

    /** Mã ngôn ngữ đang áp dụng; rỗng (theo hệ thống) -> coi như mặc định "vi". */
    private String currentTag() {
        LocaleListCompat locales = AppCompatDelegate.getApplicationLocales();
        if (locales.isEmpty() || locales.get(0) == null) return "vi";
        String lang = locales.get(0).getLanguage();
        return lang == null || lang.isEmpty() ? "vi" : lang;
    }

    private void applyLanguage(String tag, String current) {
        if (tag.equals(current)) return;
        Toast.makeText(this, R.string.lang_applied, Toast.LENGTH_SHORT).show();
        // Đặt locale per-app; AppCompat sẽ tự recreate các activity để áp dụng.
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
