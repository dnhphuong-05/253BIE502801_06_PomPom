package com.pompom.group6.activities;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

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
import com.pompom.group6.databinding.ActivityArTryOnBinding;
import com.pompom.group6.models.Shade;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * AR Try-on – demo screen.
 * Front camera preview with a simulated lip overlay tinted to the selected shade.
 * Lets the user preview a lipstick color on their face before adding it to the cart.
 * The overlay position is fixed for the demo (no face tracking).
 */
public class ArTryOnActivity extends SwipeBackActivity {

    private static final int CAMERA_PERMISSION_CODE = 202;
    private static final DecimalFormat PRICE_FORMAT =
            new DecimalFormat("#,###", new DecimalFormatSymbols(Locale.GERMANY)); // dot grouping

    private ActivityArTryOnBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.TRANSPARENT);

        binding = ActivityArTryOnBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupShades();
        setupListeners();

        if (hasCameraPermission()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    private void setupShades() {
        List<Shade> shades = new ArrayList<>();
        shades.add(new Shade("Hồng Nude", R.color.shade_nude_pink, 320000));
        shades.add(new Shade("Đào", R.color.shade_peach, 350000));
        shades.add(new Shade("Cam đất", R.color.shade_terracotta, 350000));
        shades.add(new Shade("Đỏ Ruby", R.color.shade_ruby, 380000));
        shades.add(new Shade("Hồng Baby", R.color.shade_baby_pink, 320000));
        shades.add(new Shade("Mận", R.color.shade_plum, 380000));

        ShadeAdapter adapter = new ShadeAdapter(shades, (shade, position) -> applyShade(shade));
        binding.rvShades.setLayoutManager(
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvShades.setAdapter(adapter);

        applyShade(shades.get(0));
    }

    private void applyShade(Shade shade) {
        int color = ContextCompat.getColor(this, shade.getColorRes());
        binding.vLipOverlay.setBackgroundTintList(ColorStateList.valueOf(color));
        binding.tvShadeName.setText(getString(R.string.tryon_shade_prefix, shade.getName()));
        binding.tvPrice.setText(formatPrice(shade.getPrice()));
    }

    private String formatPrice(double price) {
        return PRICE_FORMAT.format(price) + "đ";
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnAddToCart.setOnClickListener(v -> {
            String shadeName = binding.tvShadeName.getText().toString();
            Toast.makeText(this, "Đã thêm " + shadeName + " vào giỏ hàng", Toast.LENGTH_SHORT).show();
        });
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
                // Best-effort preview for the demo.
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
