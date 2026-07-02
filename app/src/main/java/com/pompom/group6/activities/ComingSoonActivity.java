package com.pompom.group6.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.pompom.group6.databinding.ActivityComingSoonBinding;
import com.pompom.group6.utils.StatusBarUtils;

public class ComingSoonActivity extends AppCompatActivity {

    public static final String EXTRA_FEATURE_NAME = "feature_name";

    public static void start(Context context, String featureName) {
        Intent intent = new Intent(context, ComingSoonActivity.class);
        intent.putExtra(EXTRA_FEATURE_NAME, featureName);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityComingSoonBinding binding = ActivityComingSoonBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Transparent status bar: this screen's background shows through.
        StatusBarUtils.applyTransparent(this);

        binding.btnBack.setOnClickListener(v -> finish());

        String featureName = getIntent().getStringExtra(EXTRA_FEATURE_NAME);
        if (featureName != null && !featureName.isEmpty()) {
            binding.tvFeatureName.setText(featureName);
        }
    }
}
