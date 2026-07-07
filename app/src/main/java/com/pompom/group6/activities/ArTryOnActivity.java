package com.pompom.group6.activities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;
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

    public static final String EXTRA_PRODUCT_NAME = "extra_product_name";
    public static final String EXTRA_PRODUCT_PRICE = "extra_product_price";

    /** Mở AR Try-on cho một sản phẩm cụ thể (vd từ nút "Thử ngay" ở màn chi tiết sản phẩm) —
     * hiện đúng tên/giá sản phẩm đó; giá giữ cố định dù đổi qua các tông demo khác nhau. */
    public static void start(Context context, String productName, String priceFormatted) {
        Intent intent = new Intent(context, ArTryOnActivity.class);
        intent.putExtra(EXTRA_PRODUCT_NAME, productName);
        intent.putExtra(EXTRA_PRODUCT_PRICE, priceFormatted);
        context.startActivity(intent);
    }

    private ActivityArTryOnBinding binding;
    private String fixedPrice; // giá sản phẩm thật khi mở từ chi tiết sản phẩm — null nếu mở từ Hub AI

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.TRANSPARENT);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        binding = ActivityArTryOnBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        applyInsets();
        applyProductExtras();
        setupShades();
        setupListeners();

        if (hasCameraPermission()) {
            startCamera();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    /** Đẩy top bar xuống dưới status bar thật và chừa đáy cho bottom panel khỏi bị thanh điều
     * hướng hệ thống che — dùng inset thật thay vì margin cố định (32dp/40dp cũ), vốn không đủ
     * trên các máy có status bar cao hơn (notch, punch-hole...).
     * THAY (không cộng thêm vào) padding gốc trong XML — cộng dồn sẽ đẩy phần thông tin ra quá
     * xa thanh điều hướng, đúng như code cũ đang bị. */
    private void applyInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.getRoot(), (v, insets) -> {
            androidx.core.graphics.Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            binding.topBar.setPadding(binding.topBar.getPaddingLeft(), bars.top,
                    binding.topBar.getPaddingRight(), binding.topBar.getPaddingBottom());
            binding.bottomPanel.setPadding(binding.bottomPanel.getPaddingLeft(), binding.bottomPanel.getPaddingTop(),
                    binding.bottomPanel.getPaddingRight(), bars.bottom);
            return insets;
        });
    }

    /** Nếu được mở từ một sản phẩm cụ thể thì hiện đúng tên/giá sản phẩm đó, không thì giữ
     * tên/giá demo mặc định trong layout (khi mở từ Hub AI, không gắn với sản phẩm nào). */
    private void applyProductExtras() {
        String name = getIntent().getStringExtra(EXTRA_PRODUCT_NAME);
        fixedPrice = getIntent().getStringExtra(EXTRA_PRODUCT_PRICE);
        if (!TextUtils.isEmpty(name)) binding.tvProductName.setText(name);
        if (!TextUtils.isEmpty(fixedPrice)) binding.tvPrice.setText(fixedPrice);
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
        // Sản phẩm thật giữ giá cố định của nó khi đổi tông demo; chỉ demo chung (mở từ Hub AI)
        // mới đổi giá theo từng tông vì các tông đó vốn không gắn với sản phẩm cụ thể nào.
        if (TextUtils.isEmpty(fixedPrice)) {
            binding.tvPrice.setText(formatPrice(shade.getPrice()));
        }
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
