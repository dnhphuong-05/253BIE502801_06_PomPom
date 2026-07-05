package com.pompom.group6.activities;

import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.pompom.group6.databinding.ActivityAiChatBinding;
import com.pompom.group6.utils.StatusBarUtils;

public class AiChatActivity extends SwipeBackActivity {

    private ActivityAiChatBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Transparent status bar; keep dark icons as before.
        StatusBarUtils.applyTransparent(this);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

        binding = ActivityAiChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnBack.setOnClickListener(v -> finish());
    }
}