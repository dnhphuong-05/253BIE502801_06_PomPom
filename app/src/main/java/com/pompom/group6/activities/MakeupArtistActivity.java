package com.pompom.group6.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.common.util.concurrent.ListenableFuture;
import com.pompom.group6.R;
import com.pompom.group6.adapters.ShadeAdapter;
import com.pompom.group6.databinding.ActivityMakeupArtistBinding;
import com.pompom.group6.models.Shade;

import java.util.ArrayList;
import java.util.List;

/**
 * AI Makeup Artist – demo screen.
 * Front camera preview + a "beauty filter" tint that changes with the selected look,
 * plus a scripted step-by-step makeup guide. The guidance/filter are simulated for the demo.
 */
public class MakeupArtistActivity extends SwipeBackActivity {

    private static final int CAMERA_PERMISSION_CODE = 201;
    /** Alpha applied over the preview so a look reads as a soft beauty filter. */
    private static final int FILTER_ALPHA = 0x33;

    private ActivityMakeupArtistBinding binding;
    private String[] guideSteps;
    private int currentStep = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        binding = ActivityMakeupArtistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        guideSteps = getResources().getStringArray(R.array.makeup_guide_steps);

        setupLooks();
        setupGuide();
        setupListeners();

        if (hasCameraPermission()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    private void setupLooks() {
        List<Shade> looks = new ArrayList<>();
        looks.add(new Shade("Nude", R.color.shade_nude_pink, 0));
        looks.add(new Shade("Đào", R.color.shade_peach, 0));
        looks.add(new Shade("Cam đất", R.color.shade_terracotta, 0));
        looks.add(new Shade("Đỏ Ruby", R.color.shade_ruby, 0));
        looks.add(new Shade("Hồng Baby", R.color.shade_baby_pink, 0));
        looks.add(new Shade("Mận", R.color.shade_plum, 0));

        ShadeAdapter adapter = new ShadeAdapter(looks, (shade, position) -> applyFilter(shade));
        binding.rvLooks.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvLooks.setAdapter(adapter);

        // Apply the first look by default.
        applyFilter(looks.get(0));
    }

    private void applyFilter(Shade shade) {
        int base = ContextCompat.getColor(this, shade.getColorRes());
        int tinted = Color.argb(FILTER_ALPHA, Color.red(base), Color.green(base), Color.blue(base));
        binding.vFilterOverlay.setBackgroundColor(tinted);
    }

    private void setupGuide() {
        currentStep = 0;
        binding.tvStepText.setText(guideSteps[currentStep]);
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnEndCall.setOnClickListener(v -> finish());
        binding.btnNextStep.setOnClickListener(v -> nextStep());
    }

    private void nextStep() {
        currentStep = (currentStep + 1) % guideSteps.length;
        binding.tvStepText.setText(guideSteps[currentStep]);
    }

    private boolean hasCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> future = ProcessCameraProvider.getInstance(this);
        future.addListener(() -> {
            try {
                ProcessCameraProvider provider = future.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());
                provider.unbindAll();
                provider.bindToLifecycle(this, CameraSelector.DEFAULT_FRONT_CAMERA, preview);
            } catch (Exception ignored) {
                // Preview is best-effort in the demo; ignore camera init failures.
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_CODE
                && grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startCamera();
        }
    }
}
