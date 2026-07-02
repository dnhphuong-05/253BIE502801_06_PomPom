package com.pompom.group6.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.pompom.group6.databinding.ActivityNotificationBinding;
import com.pompom.group6.utils.StatusBarUtils;

public class NotificationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityNotificationBinding binding = ActivityNotificationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Transparent status bar: this screen's background shows through.
        StatusBarUtils.applyTransparent(this);

        binding.btnBack.setOnClickListener(v -> finish());
    }
}
