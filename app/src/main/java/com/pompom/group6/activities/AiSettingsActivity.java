package com.pompom.group6.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.pompom.group6.database.AiSessionDAO;
import com.pompom.group6.databinding.ActivityAiSettingsBinding;
import com.pompom.group6.utils.AiSettings;
import com.pompom.group6.utils.StatusBarUtils;

/** Cài đặt AI: bật/tắt đọc to câu trả lời, âm thanh khi chạm mascot, và xoá lịch sử AI. */
public class AiSettingsActivity extends SwipeBackActivity {

    private ActivityAiSettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.applyPinkHeader(this);

        binding = ActivityAiSettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());

        binding.switchAutoSpeak.setChecked(AiSettings.isAutoSpeakEnabled(this));
        binding.switchAutoSpeak.setOnCheckedChangeListener((btn, checked) ->
                AiSettings.setAutoSpeakEnabled(this, checked));

        binding.switchMascotSound.setChecked(AiSettings.isMascotSoundEnabled(this));
        binding.switchMascotSound.setOnCheckedChangeListener((btn, checked) ->
                AiSettings.setMascotSoundEnabled(this, checked));

        binding.btnClearHistory.setOnClickListener(v -> confirmClearHistory());
    }

    private void confirmClearHistory() {
        new AlertDialog.Builder(this)
                .setTitle("Xoá lịch sử AI?")
                .setMessage("Toàn bộ kết quả khám da và trang điểm đã lưu sẽ bị xoá vĩnh viễn. Bạn có chắc chắn không?")
                .setPositiveButton("Xoá", (dialog, which) -> {
                    new AiSessionDAO(this).deleteAllHistory();
                    Toast.makeText(this, "Đã xoá lịch sử AI", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Huỷ", null)
                .show();
    }
}
