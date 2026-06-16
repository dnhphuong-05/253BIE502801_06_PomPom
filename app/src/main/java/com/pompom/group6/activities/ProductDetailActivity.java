package com.pompom.group6.activities;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayoutMediator;
import com.pompom.group6.R;
import com.pompom.group6.adapters.ProductImageAdapter;
import com.pompom.group6.database.ProductDAO;
import com.pompom.group6.databinding.ActivityProductDetailBinding;
import com.pompom.group6.models.Product;

import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {

    private ActivityProductDetailBinding binding;
    private ProductDAO productDAO;
    private int productId;
    private int quantity = 1;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Full screen with dark status bar icons (black clock/date) and matching nav bar color with action panel
        int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
        }

        getWindow().getDecorView().setSystemUiVisibility(flags);
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
        // Match with the light pink (#FFF5F5) background of the bottom action panel
        getWindow().setNavigationBarColor(android.graphics.Color.parseColor("#FFF5F5"));

        binding = ActivityProductDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        productId = getIntent().getIntExtra("product_id", -1);
        productDAO = new ProductDAO(this);

        setupSwipeBack();
        setupListeners();
        loadProductData();
    }

    private void setupSwipeBack() {
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1 != null && e2 != null) {
                    float diffX = e2.getX() - e1.getX();
                    float diffY = e2.getY() - e1.getY();
                    // Detect swipe from left to right
                    if (Math.abs(diffX) > Math.abs(diffY) && diffX > 150 && Math.abs(velocityX) > 100) {
                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                        return true;
                    }
                }
                return false;
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (gestureDetector != null) {
            gestureDetector.onTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());
        
        binding.btnPlus.setOnClickListener(v -> {
            quantity++;
            binding.tvQuantity.setText(String.valueOf(quantity));
        });

        binding.btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                binding.tvQuantity.setText(String.valueOf(quantity));
            }
        });

        binding.btnAddToCart.setOnClickListener(v -> {
            Toast.makeText(this, "Đã thêm vào giỏ hàng", Toast.LENGTH_SHORT).show();
        });

        binding.btnBuyNow.setOnClickListener(v -> {
            Toast.makeText(this, "Tiến hành thanh toán", Toast.LENGTH_SHORT).show();
        });
        
        binding.btnWishlist.setOnClickListener(v -> {
            Toast.makeText(this, "Đã thêm vào danh sách yêu thích", Toast.LENGTH_SHORT).show();
        });
    }

    private void loadProductData() {
        if (productId != -1) {
            Product product = productDAO.getProductById(productId);
            if (product != null) {
                binding.tvProductName.setText(product.getTitle());
                binding.tvProductPrice.setText(product.getPrice());
                
                if (product.getDescription() != null) {
                    binding.tvProductDesc.setText(product.getDescription());
                }

                // Load images
                List<String> images = productDAO.getProductImages(productId);
                if (!images.isEmpty()) {
                    ProductImageAdapter adapter = new ProductImageAdapter(images);
                    binding.vpProductImages.setAdapter(adapter);
                    new TabLayoutMediator(binding.tabIndicator, binding.vpProductImages, (tab, position) -> {
                    }).attach();
                } else if (product.getImageUrl() != null) {
                    // Fallback to main image
                    ProductImageAdapter adapter = new ProductImageAdapter(java.util.Collections.singletonList(product.getImageUrl()));
                    binding.vpProductImages.setAdapter(adapter);
                    new TabLayoutMediator(binding.tabIndicator, binding.vpProductImages, (tab, position) -> {
                    }).attach();
                }
            }
        }
    }
}
