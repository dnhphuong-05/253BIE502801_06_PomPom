package com.pompom.group6.activities;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.tabs.TabLayoutMediator;
import com.pompom.group6.R;
import com.pompom.group6.adapters.ProductImageAdapter;
import com.pompom.group6.adapters.ReviewAdapter;
import com.pompom.group6.adapters.VariantAdapter;
import com.pompom.group6.adapters.VoucherAdapter;
import com.pompom.group6.database.ProductDAO;
import com.pompom.group6.database.PromotionDAO;
import com.pompom.group6.databinding.ActivityProductDetailBinding;
import com.pompom.group6.models.Product;
import com.pompom.group6.models.ProductVariant;
import com.pompom.group6.models.Review;
import com.pompom.group6.models.Voucher;

import java.util.List;

public class ProductDetailActivity extends AppCompatActivity {

    private ActivityProductDetailBinding binding;
    private ProductDAO productDAO;
    private PromotionDAO promotionDAO;
    private int productId;
    private int quantity = 1;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        android.util.Log.d("ProductDetailActivity", "onCreate() started");

        try {
            // Full screen with dark status bar icons (black clock/date) and matching nav bar color with action panel
            int flags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                flags |= View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR;
            }

            getWindow().getDecorView().setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);
            
            // Set navigation bar color safely
            try {
                getWindow().setNavigationBarColor(android.graphics.Color.parseColor("#FFF5F5"));
            } catch (Exception e) {
                android.util.Log.w("ProductDetailActivity", "Could not set navigation bar color", e);
            }

            // Inflate binding using LayoutInflater from context to be safer
            LayoutInflater inflater = LayoutInflater.from(this);
            binding = ActivityProductDetailBinding.inflate(inflater);
            setContentView(binding.getRoot());

            productId = getIntent().getIntExtra("product_id", -1);
            android.util.Log.d("ProductDetailActivity", "productId=" + productId);
            productDAO = new ProductDAO(this);
            promotionDAO = new PromotionDAO(this);

            setupSwipeBack();
            setupListeners();
            loadProductData();
        } catch (Exception e) {
            android.util.Log.e("ProductDetailActivity", "FATAL ERROR in onCreate", e);
            Toast.makeText(this, "Lỗi khởi tạo: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
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
        android.util.Log.d("ProductDetailActivity", "loadProductData() called with productId=" + productId);
        try {
        if (productId != -1) {
            Product product = productDAO.getProductById(productId);
            android.util.Log.d("ProductDetailActivity", "getProductById returned: " + (product == null ? "null" : product.getTitle()));
            if (product != null) {
                binding.tvProductName.setText(product.getTitle());
                binding.tvProductPrice.setText(product.getPrice());
                
                if (product.getOriginalPrice() != null) {
                    binding.tvOriginalPrice.setVisibility(View.VISIBLE);
                    binding.tvOriginalPrice.setText(product.getOriginalPrice());
                    binding.tvOriginalPrice.setPaintFlags(binding.tvOriginalPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                } else {
                    binding.tvOriginalPrice.setVisibility(View.GONE);
                }

                if (product.getDiscountPercent() > 0) {
                    binding.tvDiscountBadge.setVisibility(View.VISIBLE);
                    binding.tvDiscountBadge.setText("-" + product.getDiscountPercent() + "%");
                } else {
                    binding.tvDiscountBadge.setVisibility(View.GONE);
                }

                binding.tvProductId.setText("Mã SP: #" + String.format("%05d", product.getId()));
                binding.tvStockStatus.setText(product.getStock() > 0 ? "Còn hàng" : "Hết hàng");
                binding.tvStockStatus.setBackgroundColor(product.getStock() > 0 ? android.graphics.Color.parseColor("#E8F5E9") : android.graphics.Color.parseColor("#FFEBEE"));
                binding.tvStockStatus.setTextColor(product.getStock() > 0 ? android.graphics.Color.parseColor("#2E7D32") : android.graphics.Color.parseColor("#C62828"));
                
                binding.tvProductMeta.setText(product.getCategoryName() + " • " + product.getBrandName());
                binding.tvStockCount.setText("Tồn kho: " + product.getStock() + " sản phẩm");
                binding.tvProductSKU.setText("SKU: " + product.getSku());
                binding.tvCategoryName.setText(product.getCategoryName());
                binding.tvBrandName.setText(product.getBrandName());

                if (product.getDescription() != null) {
                    binding.tvProductDesc.setText(product.getDescription());
                }

                // Load rating and reviews
                binding.ratingBar.setRating(product.getRating());
                binding.tvRating.setText(String.format("%.1f (%d đánh giá)", product.getRating(), product.getReviewCount()));
                binding.tvRatingBig.setText(String.format("%.1f", product.getRating()));
                binding.tvReviewSectionTitle.setText("Đánh giá sản phẩm (" + product.getReviewCount() + ")");
                binding.tvReviewCountSmall.setText("(" + product.getReviewCount() + " đánh giá)");

                // Load images
                try {
                    List<String> images = productDAO.getProductImages(productId);
                    if (images != null && !images.isEmpty()) {
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
                } catch (Exception e) {
                    android.util.Log.e("ProductDetailActivity", "Error loading product images", e);
                }

                // Load reviews list
                try {
                    List<Review> reviews = productDAO.getReviewsForProduct(productId);
                    if (reviews != null && !reviews.isEmpty()) {
                        binding.rvReviews.setVisibility(View.VISIBLE);
                        ReviewAdapter reviewAdapter = new ReviewAdapter(reviews);
                        binding.rvReviews.setAdapter(reviewAdapter);
                    } else {
                        binding.rvReviews.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    android.util.Log.e("ProductDetailActivity", "Error loading reviews", e);
                    binding.rvReviews.setVisibility(View.GONE);
                }

                // Load variants
                try {
                    List<ProductVariant> variants = productDAO.getVariantsForProduct(productId);
                    if (variants != null && !variants.isEmpty()) {
                        binding.rvVariants.setVisibility(View.VISIBLE);
                        VariantAdapter variantAdapter = new VariantAdapter(variants, variant -> {
                            Toast.makeText(this, "Chọn: " + variant.getName(), Toast.LENGTH_SHORT).show();
                        });
                        binding.rvVariants.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
                        binding.rvVariants.setAdapter(variantAdapter);
                    } else {
                        binding.rvVariants.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    android.util.Log.e("ProductDetailActivity", "Error loading variants", e);
                    binding.rvVariants.setVisibility(View.GONE);
                }

                // Load vouchers
                try {
                    List<Voucher> vouchers = promotionDAO.getAllVouchers();
                    if (vouchers != null && !vouchers.isEmpty()) {
                        binding.rvVouchers.setVisibility(View.VISIBLE);
                        VoucherAdapter voucherAdapter = new VoucherAdapter(vouchers);
                        binding.rvVouchers.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
                        binding.rvVouchers.setAdapter(voucherAdapter);
                        binding.btnSeeAllVouchers.setText("Xem tất cả (" + vouchers.size() + ")");
                    } else {
                        binding.rvVouchers.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    android.util.Log.e("ProductDetailActivity", "Error loading vouchers", e);
                    binding.rvVouchers.setVisibility(View.GONE);
                }
            } else {
                android.util.Log.e("ProductDetailActivity", "Product is null for id=" + productId);
                Toast.makeText(this, "Không thể tải thông tin sản phẩm", Toast.LENGTH_SHORT).show();
                finish();
            }
        } else {
            android.util.Log.e("ProductDetailActivity", "Invalid productId=-1");
            Toast.makeText(this, "Mã sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
        }
        } catch (Exception e) {
            android.util.Log.e("ProductDetailActivity", "FATAL ERROR in loadProductData", e);
            Toast.makeText(this, "Lỗi: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}
