package com.pompom.group6.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Locale;

import com.pompom.group6.R;
import com.pompom.group6.activities.CartActivity;
import com.pompom.group6.activities.GuestOrderActivity;
import com.pompom.group6.activities.ProductConsultationChatActivity;
import com.pompom.group6.fragments.ProductOptionsBottomSheetDialog;
import com.pompom.group6.fragments.VoucherSelectionBottomSheet;
import com.pompom.group6.adapters.ProductAdapter;
import com.pompom.group6.adapters.ProductImageAdapter;
import com.pompom.group6.adapters.ReviewAdapter;
import com.pompom.group6.adapters.ThumbnailAdapter;
import com.pompom.group6.adapters.VariantAdapter;
import com.pompom.group6.adapters.VoucherAdapter;
import com.pompom.group6.database.ProductDAO;
import com.pompom.group6.database.PromotionDAO;
import com.pompom.group6.databinding.ActivityProductDetailBinding;
import com.pompom.group6.models.CartItem;
import com.pompom.group6.models.Product;
import com.pompom.group6.models.ProductVariant;
import com.pompom.group6.models.Review;
import com.pompom.group6.models.Voucher;
import com.pompom.group6.utils.CartManager;

import java.util.List;

public class ProductDetailActivity extends AppCompatActivity
        implements CartManager.CartChangeListener {

    private ActivityProductDetailBinding binding;
    private ProductDAO productDAO;
    private PromotionDAO promotionDAO;
    private CartManager cartManager;
    private String productId;
    private int sqliteId = -1; // id số cho sản phẩm SQLite; -1 nếu là sản phẩm cloud (ObjectId)
    private Product currentProduct;
    private boolean isWishlisted = false; // fix 2A

    // Đánh giá — mặc định chỉ hiện 1 đánh giá, bấm "Xem tất cả" mới hiện hết.
    private final List<Review> allReviews = new java.util.ArrayList<>();
    private boolean reviewsExpanded = false;

    // Edge-swipe-to-exit (vuốt mép trái sang phải để thoát)
    private float swipeDownX, swipeDownY;
    private boolean edgeSwipe, swipeDragging;
    private float edgePx, slopPx;
    private float colorStartPx, colorEndPx; // 10dp bắt đầu đổi màu → 20dp đổi hoàn toàn
    private float iconLiftPx; // nâng icon lên trên ngón tay cho dễ nhìn
    private final android.animation.ArgbEvaluator argbEvaluator = new android.animation.ArgbEvaluator();

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

            productId = getIntent().getStringExtra("product_id");
            sqliteId = (productId != null && productId.matches("\\d+")) ? Integer.parseInt(productId) : -1;
            android.util.Log.d("ProductDetailActivity", "productId=" + productId + " sqliteId=" + sqliteId);
            productDAO = new ProductDAO(this);
            promotionDAO = new PromotionDAO(this);
            cartManager = CartManager.getInstance(this);
            cartManager.addListener(this);
            updateCartBadge(cartManager.getTotalCount());

            setupSwipeBack();
            setupListeners();
            // Sản phẩm cloud (id ObjectId) → nạp từ MongoDB; sản phẩm SQLite (id số) → nạp local.
            if (sqliteId == -1 && productId != null) {
                loadFromApi();
            } else {
                loadProductData();
            }
        } catch (Exception e) {
            android.util.Log.e("ProductDetailActivity", "FATAL ERROR in onCreate", e);
            Toast.makeText(this, "Lỗi khởi tạo: " + e.getMessage(), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (cartManager != null) updateCartBadge(cartManager.getTotalCount());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cartManager != null) cartManager.removeListener(this);
    }

    @Override
    public void onCartChanged(int totalCount) {
        runOnUiThread(() -> updateCartBadge(totalCount));
    }

    private void updateCartBadge(int count) {
        if (binding == null || binding.tvCartBadge == null) return;
        if (count > 0) {
            binding.tvCartBadge.setVisibility(View.VISIBLE);
            binding.tvCartBadge.setText(count > 99 ? "99+" : String.valueOf(count));
        } else {
            binding.tvCartBadge.setVisibility(View.GONE);
        }
    }

    private void setupSwipeBack() {
        float density = getResources().getDisplayMetrics().density;
        edgePx = 32 * density; // chỉ kích hoạt khi ngón chạm sát mép trái màn hình
        slopPx = 8 * density;
        colorStartPx = 20 * density; // kéo tới 20dp thì bắt đầu đổi màu nút
        colorEndPx = 30 * density;   // kéo tới 30dp thì đổi màu hoàn toàn → thả để thoát
        iconLiftPx = 64 * density;   // nâng icon cao hơn ngón cho dễ nhìn
    }

    /**
     * Task 3 — Vuốt từ mép trái sang phải: màn hình kéo theo ngón + hiện mũi tên;
     * thả tay khi kéo đủ xa thì thoát, chưa đủ thì bật lại.
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (binding == null) return super.dispatchTouchEvent(ev);
        int screenW = getResources().getDisplayMetrics().widthPixels;

        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                swipeDownX = ev.getX();
                swipeDownY = ev.getY();
                edgeSwipe = swipeDownX <= edgePx;
                swipeDragging = false;
                break;

            case MotionEvent.ACTION_MOVE:
                if (edgeSwipe && !swipeDragging) {
                    float dx = ev.getX() - swipeDownX;
                    float dy = ev.getY() - swipeDownY;
                    if (dx > slopPx && dx > Math.abs(dy) * 1.5f) {
                        swipeDragging = true;
                        // Hủy sự kiện với các view con để không cuộn nhầm
                        MotionEvent cancel = MotionEvent.obtain(ev);
                        cancel.setAction(MotionEvent.ACTION_CANCEL);
                        super.dispatchTouchEvent(cancel);
                        cancel.recycle();
                    }
                }
                if (swipeDragging) {
                    float dx = Math.max(0, ev.getX() - swipeDownX);
                    // Màn hình GIỮ CỐ ĐỊNH; icon di chuyển TỰ DO theo ngón (cả X và Y).
                    View ind = binding.swipeBackIndicator;
                    float homeCx = ind.getLeft() + ind.getWidth() / 2f;
                    float homeCy = ind.getTop() + ind.getHeight() / 2f;
                    ind.setAlpha(Math.min(1f, dx / (colorStartPx * 0.5f)));
                    ind.setTranslationX(ev.getX() - homeCx);
                    ind.setTranslationY(ev.getY() - homeCy - iconLiftPx);
                    // 10dp bắt đầu đổi màu → 20dp đổi hoàn toàn:
                    // nền + viền: trắng/hồng nhạt → hồng đậm; mũi tên: hồng nhạt → trắng.
                    float t = Math.max(0f, Math.min(1f,
                            (dx - colorStartPx) / (colorEndPx - colorStartPx)));
                    int brandPink = ContextCompat.getColor(this, R.color.brand_pink);
                    int bgColor = (int) argbEvaluator.evaluate(t, android.graphics.Color.WHITE, brandPink);
                    int arrowColor = (int) argbEvaluator.evaluate(t, brandPink, android.graphics.Color.WHITE);
                    binding.swipeBackIndicator.setCardBackgroundColor(bgColor);
                    binding.swipeBackIndicator.setStrokeColor(brandPink);
                    binding.ivSwipeArrow.setColorFilter(arrowColor);
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (swipeDragging) {
                    swipeDragging = false;
                    edgeSwipe = false;
                    float dx = Math.max(0, ev.getX() - swipeDownX);
                    if (dx >= colorEndPx) {
                        finish();
                        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
                    } else {
                        binding.swipeBackIndicator.animate()
                                .alpha(0f).translationX(0f).translationY(0f).setDuration(180).start();
                    }
                    return true;
                }
                edgeSwipe = false;
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());

        // "Thêm vào giỏ hàng" → BottomSheet with variant/qty picker (fix 1B)
        binding.btnAddToCart.setOnClickListener(v -> {
            // Sản phẩm cloud nạp bất đồng bộ (loadFromApi) — bấm quá sớm khi currentProduct
            // chưa kịp gán thì trước đây bottom sheet lặng lẽ không mở, không có phản hồi gì.
            if (currentProduct == null) {
                Toast.makeText(this, "Sản phẩm đang tải, vui lòng thử lại sau giây lát", Toast.LENGTH_SHORT).show();
                return;
            }
            ProductOptionsBottomSheetDialog.show(getSupportFragmentManager(),
                    currentProduct.getId(),
                    currentProduct.getTitle(),
                    currentProduct.getPrice(),
                    currentProduct.getImageUrl(),
                    ProductOptionsBottomSheetDialog.ACTION_ADD_TO_CART_DETAIL);
        });
        binding.btnCartTop.setOnClickListener(v -> {
            Intent intent = new Intent(this, CartActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left); // Tạo hiệu ứng chuyển cảnh mượt mà
        });

        // "Mua ngay" → BottomSheet that goes directly to CheckoutActivity (fix 1B)
        binding.btnBuyNow.setOnClickListener(v -> {
            if (currentProduct == null) {
                Toast.makeText(this, "Sản phẩm đang tải, vui lòng thử lại sau giây lát", Toast.LENGTH_SHORT).show();
                return;
            }
            ProductOptionsBottomSheetDialog.show(getSupportFragmentManager(),
                    currentProduct.getId(),
                    currentProduct.getTitle(),
                    currentProduct.getPrice(),
                    currentProduct.getImageUrl(),
                    ProductOptionsBottomSheetDialog.ACTION_BUY_NOW);
        });

        // Wishlist toggle — ic_heart_outline ↔ ic_heart_filled (fix 2A)
        // Wishlist toggle — ic_heart_outline ↔ ic_heart_filled (fix 2A)
        binding.ivWishlistHeart.setOnClickListener(v -> {
            isWishlisted = !isWishlisted;

            // Thêm hiệu ứng hoạt họa bounce giống y chang dưới adapter cho xịn nè bồ
            android.view.animation.Animation anim = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.heart_scale);
            binding.ivWishlistHeart.startAnimation(anim); // Nhớ kiểm tra ID của ImageView bên trong Card là gì nha (ví dụ: ivWishlistHeart)

            // Đổi resource trên ImageView bên trong, chứ không đổi trực tiếp trên CardView cha nữa
            binding.ivWishlistHeart.setImageResource(
                    isWishlisted ? R.drawable.ic_heart_solid : R.drawable.ic_heart_outline);

            Toast.makeText(this,
                    isWishlisted ? "Đã thêm vào yêu thích" : "Đã xóa khỏi yêu thích",
                    Toast.LENGTH_SHORT).show();
        });

        // "Thử ngay" → AR Try-on (demo camera + tông màu), gắn đúng tên/giá sản phẩm đang xem.
        View.OnClickListener tryOnListener = v -> {
            String name = currentProduct != null ? currentProduct.getTitle() : null;
            String price = currentProduct != null ? formatPricePlain(currentProduct.getPrice()) : null;
            ArTryOnActivity.start(this, name, price);
        };
        binding.cardTryOn.setOnClickListener(tryOnListener);
        binding.btnTryOn.setOnClickListener(tryOnListener);

        // Chat button → ProductConsultationChatActivity
        if (binding.btnChat != null) {
            binding.btnChat.setOnClickListener(v -> {
                Intent chatIntent = new Intent(this, ProductConsultationChatActivity.class);
                if (currentProduct != null) {
                    chatIntent.putExtra(ProductConsultationChatActivity.EXTRA_PRODUCT_NAME, currentProduct.getTitle());
                    chatIntent.putExtra(ProductConsultationChatActivity.EXTRA_PRODUCT_SKU, currentProduct.getSku());
                }
                startActivity(chatIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }

        // Guest order FAB — GONE when logged in, VISIBLE for guests only
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);
        if (isLoggedIn) {
            binding.fabGuestOrder.setVisibility(View.GONE);
        } else {
            binding.fabGuestOrder.setVisibility(View.VISIBLE);
            binding.fabGuestOrder.setOnClickListener(v -> {
                startActivity(new Intent(this, GuestOrderActivity.class));
            });
        }
    }

    /**
     * Task 1 — Slider ảnh chính + rail thumbnail bo tròn, đồng bộ hai chiều.
     */
    private void setupImageSlider(List<String> images) {
        ProductImageAdapter pagerAdapter = new ProductImageAdapter(images);
        binding.vpProductImages.setAdapter(pagerAdapter);

        if (images.size() > 1) {
            binding.rvThumbnails.setVisibility(View.VISIBLE);
            binding.rvThumbnails.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(
                    this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
            ThumbnailAdapter thumbAdapter = new ThumbnailAdapter(images,
                    position -> binding.vpProductImages.setCurrentItem(position, true));
            binding.rvThumbnails.setAdapter(thumbAdapter);

            // Vuốt ảnh chính → cập nhật thumbnail đang chọn.
            binding.vpProductImages.registerOnPageChangeCallback(
                    new androidx.viewpager2.widget.ViewPager2.OnPageChangeCallback() {
                        @Override
                        public void onPageSelected(int position) {
                            thumbAdapter.setSelectedPosition(position);
                            binding.rvThumbnails.smoothScrollToPosition(position);
                        }
                    });
        } else {
            binding.rvThumbnails.setVisibility(View.GONE);
        }
    }

    /** Nạp chi tiết sản phẩm CLOUD (id ObjectId) từ MongoDB qua backend. */
    private void loadFromApi() {
        com.pompom.group6.network.ApiClient.get().getProduct(productId)
                .enqueue(new retrofit2.Callback<com.pompom.group6.network.dto.ApiProduct>() {
                    @Override
                    public void onResponse(retrofit2.Call<com.pompom.group6.network.dto.ApiProduct> call,
                                           retrofit2.Response<com.pompom.group6.network.dto.ApiProduct> resp) {
                        if (binding == null || !resp.isSuccessful() || resp.body() == null) return;
                        com.pompom.group6.network.dto.ApiProduct a = resp.body();
                        Product product = com.pompom.group6.network.ProductMapper.toProduct(a);
                        currentProduct = product;

                        binding.tvProductName.setText(product.getTitle());
                        setPriceHtml(binding.tvProductPrice, product.getPrice());
                        if (product.getOriginalPrice() != null) {
                            binding.tvOriginalPrice.setVisibility(View.VISIBLE);
                            setPriceHtml(binding.tvOriginalPrice, product.getOriginalPrice());
                            binding.tvOriginalPrice.setPaintFlags(
                                    binding.tvOriginalPrice.getPaintFlags() | android.graphics.Paint.STRIKE_THRU_TEXT_FLAG);
                        } else {
                            binding.tvOriginalPrice.setVisibility(View.GONE);
                        }
                        if (product.getDiscountPercent() > 0) {
                            binding.tvDiscountBadge.setVisibility(View.VISIBLE);
                            binding.tvDiscountBadge.setText("-" + product.getDiscountPercent() + "%");
                        } else {
                            binding.tvDiscountBadge.setVisibility(View.GONE);
                        }

                        binding.tvProductId.setText("#" + String.format("%05d", product.seed() % 100000));
                        binding.tvStockStatus.setText(product.getStock() > 0 ? "Còn hàng" : "Hết hàng");
                        binding.tvStockStatus.setBackgroundColor(product.getStock() > 0
                                ? android.graphics.Color.parseColor("#E8F5E9") : android.graphics.Color.parseColor("#FFEBEE"));
                        binding.tvStockStatus.setTextColor(product.getStock() > 0
                                ? android.graphics.Color.parseColor("#2E7D32") : android.graphics.Color.parseColor("#C62828"));
                        binding.tvProductMeta.setText(product.getCategoryName() + " • " + product.getBrandName());
                        int fomoCount = (product.seed() * 7 + 3) % 8 + 1;
                        binding.tvProductSKU.setText("Còn " + fomoCount + " sản phẩm");
                        binding.tvProductSKU.setTextColor(android.graphics.Color.parseColor("#E53935"));
                        binding.tvStockCount.setVisibility(View.GONE);
                        binding.tvCategoryName.setText(product.getCategoryName());
                        binding.tvBrandName.setText(product.getBrandName());
                        if (product.getDescription() != null) binding.tvProductDesc.setText(product.getDescription());

                        binding.ratingBar.setRating(product.getRating());
                        binding.tvRating.setText(String.format("%.1f (%d đánh giá)", product.getRating(), product.getReviewCount()));
                        binding.tvRatingBig.setText(String.format("%.1f", product.getRating()));
                        binding.tvReviewSectionTitle.setText("Đánh giá sản phẩm (" + product.getReviewCount() + ")");
                        binding.tvReviewCountSmall.setText("(" + product.getReviewCount() + " đánh giá)");

                        java.util.List<String> images = a.images != null ? a.images : new java.util.ArrayList<>();
                        if (images.isEmpty() && product.getImageUrl() != null) images.add(product.getImageUrl());
                        if (!images.isEmpty()) setupImageSlider(images);

                        bindCloudVariants(a.variants);
                        loadCloudReviews();
                        loadCloudRelated();
                        loadCloudVouchers();
                    }

                    @Override
                    public void onFailure(retrofit2.Call<com.pompom.group6.network.dto.ApiProduct> call, Throwable t) {
                        if (binding != null) {
                            Toast.makeText(ProductDetailActivity.this,
                                    "Không tải được sản phẩm từ máy chủ", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /** Đánh giá của sản phẩm cloud (từ MongoDB). */
    private void loadCloudReviews() {
        com.pompom.group6.network.ApiClient.get().getProductReviews(productId)
                .enqueue(new retrofit2.Callback<java.util.List<com.pompom.group6.network.dto.ApiReview>>() {
                    @Override
                    public void onResponse(retrofit2.Call<java.util.List<com.pompom.group6.network.dto.ApiReview>> call,
                                           retrofit2.Response<java.util.List<com.pompom.group6.network.dto.ApiReview>> resp) {
                        if (binding == null || !resp.isSuccessful() || resp.body() == null) return;
                        allReviews.clear();
                        for (com.pompom.group6.network.dto.ApiReview r : resp.body()) {
                            String imagesCsv = r.images != null ? android.text.TextUtils.join(",", r.images) : null;
                            allReviews.add(new Review(0, 0, r.userName, r.userAvatar, 0,
                                    r.rating, r.comment, imagesCsv, r.createdAt));
                        }
                        reviewsExpanded = false;
                        bindReviewSummary(allReviews);
                        renderReviewList();
                    }

                    @Override
                    public void onFailure(retrofit2.Call<java.util.List<com.pompom.group6.network.dto.ApiReview>> call, Throwable t) {}
                });
    }

    /** Chỉ hiện 1 đánh giá đầu tiên; bấm "Xem tất cả" mới hiện hết. */
    private void renderReviewList() {
        if (binding == null) return;
        if (allReviews.isEmpty()) {
            binding.rvReviews.setVisibility(View.GONE);
            binding.btnSeeAllReviews.setVisibility(View.GONE);
            return;
        }
        binding.rvReviews.setVisibility(View.VISIBLE);
        binding.rvReviews.setLayoutManager(
                new androidx.recyclerview.widget.LinearLayoutManager(ProductDetailActivity.this));
        binding.rvReviews.setNestedScrollingEnabled(false);
        List<Review> shown = reviewsExpanded ? allReviews : allReviews.subList(0, 1);
        binding.rvReviews.setAdapter(new ReviewAdapter(shown));

        if (allReviews.size() <= 1) {
            binding.btnSeeAllReviews.setVisibility(View.GONE);
        } else {
            binding.btnSeeAllReviews.setVisibility(View.VISIBLE);
            binding.btnSeeAllReviews.setText(reviewsExpanded ? "Thu gọn" : "Xem tất cả");
            binding.btnSeeAllReviews.setOnClickListener(v -> {
                reviewsExpanded = !reviewsExpanded;
                renderReviewList();
            });
        }
    }

    /** Sản phẩm liên quan của sản phẩm cloud (từ MongoDB). */
    private void loadCloudRelated() {
        com.pompom.group6.network.ApiClient.get().getRelatedProducts(productId)
                .enqueue(new retrofit2.Callback<java.util.List<com.pompom.group6.network.dto.ApiProduct>>() {
                    @Override
                    public void onResponse(retrofit2.Call<java.util.List<com.pompom.group6.network.dto.ApiProduct>> call,
                                           retrofit2.Response<java.util.List<com.pompom.group6.network.dto.ApiProduct>> resp) {
                        if (binding == null || !resp.isSuccessful() || resp.body() == null) return;
                        List<Product> related = new java.util.ArrayList<>();
                        for (com.pompom.group6.network.dto.ApiProduct a : resp.body()) {
                            related.add(com.pompom.group6.network.ProductMapper.toProduct(a));
                        }
                        if (!related.isEmpty()) {
                            binding.tvRelatedTitle.setVisibility(View.VISIBLE);
                            binding.rvRelated.setVisibility(View.VISIBLE);
                            binding.rvRelated.setLayoutManager(
                                    new androidx.recyclerview.widget.GridLayoutManager(ProductDetailActivity.this, 2));
                            binding.rvRelated.setNestedScrollingEnabled(false);
                            binding.rvRelated.setAdapter(new ProductAdapter(related));
                        } else {
                            binding.tvRelatedTitle.setVisibility(View.GONE);
                            binding.rvRelated.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<java.util.List<com.pompom.group6.network.dto.ApiProduct>> call, Throwable t) {}
                });
    }

    /** Lựa chọn màu sắc (biến thể) của sản phẩm cloud (từ MongoDB). */
    private void bindCloudVariants(List<com.pompom.group6.network.dto.ApiProductVariant> apiVariants) {
        if (binding == null) return;
        List<ProductVariant> variants = new java.util.ArrayList<>();
        if (apiVariants != null) {
            for (com.pompom.group6.network.dto.ApiProductVariant av : apiVariants) {
                ProductVariant v = new ProductVariant(0, 0,
                        av.variantName != null ? av.variantName : "", "",
                        av.additionalPrice, av.stock, av.imageUrl);
                v.setOid(av.id);
                variants.add(v);
            }
        }
        if (!variants.isEmpty()) {
            binding.rvVariants.setVisibility(View.VISIBLE);
            VariantAdapter variantAdapter = new VariantAdapter(variants, variant -> { });
            binding.rvVariants.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(
                    this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
            binding.rvVariants.setAdapter(variantAdapter);
        } else {
            binding.rvVariants.setVisibility(View.GONE);
        }
    }

    /** Voucher đang hoạt động (từ MongoDB) — áp dụng chung, không lọc theo sản phẩm. */
    private void loadCloudVouchers() {
        com.pompom.group6.network.ApiClient.get().getVouchers()
                .enqueue(new retrofit2.Callback<java.util.List<com.pompom.group6.network.dto.ApiVoucher>>() {
                    @Override
                    public void onResponse(retrofit2.Call<java.util.List<com.pompom.group6.network.dto.ApiVoucher>> call,
                                           retrofit2.Response<java.util.List<com.pompom.group6.network.dto.ApiVoucher>> resp) {
                        if (binding == null) return;
                        List<Voucher> vouchers = new java.util.ArrayList<>();
                        if (resp.isSuccessful() && resp.body() != null) {
                            for (com.pompom.group6.network.dto.ApiVoucher a : resp.body()) {
                                int remaining = Math.max(a.usageLimit - a.usedCount, 0);
                                String expiry = a.endDate != null && a.endDate.length() >= 10
                                        ? a.endDate.substring(0, 10) : a.endDate;
                                Voucher v = new Voucher(0, a.code, a.discountType, a.discountValue,
                                        a.minOrderAmount, expiry, remaining);
                                v.setOid(a.id);
                                vouchers.add(v);
                            }
                        }
                        if (!vouchers.isEmpty()) {
                            binding.rvVouchers.setVisibility(View.VISIBLE);
                            VoucherAdapter voucherAdapter = new VoucherAdapter(vouchers);
                            binding.rvVouchers.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(
                                    ProductDetailActivity.this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false));
                            binding.rvVouchers.setAdapter(voucherAdapter);
                            binding.btnSeeAllVouchers.setText("Chọn voucher (" + vouchers.size() + ")");
                            binding.btnSeeAllVouchers.setOnClickListener(v -> showVoucherRadioDialog(vouchers));
                        } else {
                            binding.rvVouchers.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<java.util.List<com.pompom.group6.network.dto.ApiVoucher>> call, Throwable t) {
                        if (binding != null) binding.rvVouchers.setVisibility(View.GONE);
                    }
                });
    }

    /** Giá dạng chữ thường "125.000đ" (không HTML) — dùng cho màn không cần font nhỏ ký tự "đ". */
    private String formatPricePlain(String priceStr) {
        if (priceStr == null) return null;
        String clean = priceStr.replaceAll("[^\\d]", "");
        if (clean.isEmpty()) return priceStr;
        return String.format(java.util.Locale.GERMANY, "%,.0f", Double.parseDouble(clean)) + "đ";
    }

    /** Đặt giá dạng HTML "125.000<small>đ</small>" từ chuỗi giá đã format. */
    private void setPriceHtml(android.widget.TextView tv, String priceStr) {
        if (priceStr == null) return;
        String clean = priceStr.replaceAll("[^\\d]", "");
        if (clean.isEmpty()) { tv.setText(priceStr); return; }
        String formatted = String.format(java.util.Locale.GERMANY, "%,.0f", Double.parseDouble(clean));
        tv.setText(android.text.Html.fromHtml(formatted + "<small><small>đ</small></small>",
                android.text.Html.FROM_HTML_MODE_LEGACY));
    }

    private void loadProductData() {
        android.util.Log.d("ProductDetailActivity", "loadProductData() called with productId=" + productId);
        try {
        if (sqliteId != -1) {
            Product product = productDAO.getProductById(sqliteId);
            android.util.Log.d("ProductDetailActivity", "getProductById returned: " + (product == null ? "null" : product.getTitle()));
            if (product != null) {
                currentProduct = product;
                binding.tvProductName.setText(product.getTitle());
                binding.tvProductPrice.setText(product.getPrice());
                

                if (product.getPrice() != null) {
                    // Giả sử giá từ DB đang là chuỗi "799000" hoặc "799.000đ", ta làm sạch chỉ lấy số
                    String cleanPrice = product.getPrice().replaceAll("[^\\d]", "");
                    if (!cleanPrice.isEmpty()) {
                        double priceValue = Double.parseDouble(cleanPrice);
                        // Định dạng thành 160.000
                        String formattedPrice = String.format(java.util.Locale.GERMANY, "%,.0f", priceValue);

                        // Tạo chuỗi HTML với chữ đ nằm trong thẻ <small>
                        String priceHtml = formattedPrice + "<small><small>đ</small></small>";
                        binding.tvProductPrice.setText(android.text.Html.fromHtml(priceHtml, android.text.Html.FROM_HTML_MODE_LEGACY));
                    }
                }
                if (product.getOriginalPrice() != null) {
                    binding.tvOriginalPrice.setVisibility(View.VISIBLE);

                    String cleanOriginal = product.getOriginalPrice().replaceAll("[^\\d]", "");
                    if (!cleanOriginal.isEmpty()) {
                        double originalValue = Double.parseDouble(cleanOriginal);
                        String formattedOriginal = String.format(java.util.Locale.GERMANY, "%,.0f", originalValue);

                        String originalHtml = formattedOriginal + "<small><small>đ</small></small>";
                        binding.tvOriginalPrice.setText(android.text.Html.fromHtml(originalHtml, android.text.Html.FROM_HTML_MODE_LEGACY));
                    }
                    // Giữ nguyên gạch ngang giá gốc
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

                binding.tvProductId.setText("#" + String.format("%05d", product.seed() % 100000));
                binding.tvStockStatus.setText(product.getStock() > 0 ? "Còn hàng" : "Hết hàng");
                binding.tvStockStatus.setBackgroundColor(product.getStock() > 0 ? android.graphics.Color.parseColor("#E8F5E9") : android.graphics.Color.parseColor("#FFEBEE"));
                binding.tvStockStatus.setTextColor(product.getStock() > 0 ? android.graphics.Color.parseColor("#2E7D32") : android.graphics.Color.parseColor("#C62828"));
                
                binding.tvProductMeta.setText(product.getCategoryName() + " • " + product.getBrandName());
                // FOMO stock count — deterministic per product, always 1–8
                int fomoCount = (product.seed() * 7 + 3) % 8 + 1;
                binding.tvProductSKU.setText("Còn " + fomoCount + " sản phẩm");
                binding.tvProductSKU.setTextColor(android.graphics.Color.parseColor("#E53935"));
                binding.tvStockCount.setVisibility(View.GONE);
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

                // Load images (slider + thumbnail rail)
                try {
                    List<String> images = productDAO.getProductImages(sqliteId);
                    if (images == null || images.isEmpty()) {
                        images = new java.util.ArrayList<>();
                        if (product.getImageUrl() != null) images.add(product.getImageUrl());
                    }
                    if (!images.isEmpty()) {
                        setupImageSlider(images);
                    }
                } catch (Exception e) {
                    android.util.Log.e("ProductDetailActivity", "Error loading product images", e);
                }

                // Load reviews list
                try {
                    List<Review> reviews = productDAO.getReviewsForProduct(sqliteId);
                    allReviews.clear();
                    if (reviews != null) allReviews.addAll(reviews);
                    reviewsExpanded = false;
                    bindReviewSummary(allReviews);
                    renderReviewList();
                } catch (Exception e) {
                    android.util.Log.e("ProductDetailActivity", "Error loading reviews", e);
                    binding.rvReviews.setVisibility(View.GONE);
                }

                // Load variants
                try {
                    List<ProductVariant> variants = productDAO.getVariantsForProduct(sqliteId);
                    if (variants != null && !variants.isEmpty()) {
                        binding.rvVariants.setVisibility(View.VISIBLE);
                        // Interactive: chọn màu → viền dày, các màu khác làm mờ (Task 2)
                        VariantAdapter variantAdapter = new VariantAdapter(variants, variant -> { });
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
                        binding.btnSeeAllVouchers.setText("Chọn voucher (" + vouchers.size() + ")");
                        final List<Voucher> voucherList = vouchers;
                        binding.btnSeeAllVouchers.setOnClickListener(v -> showVoucherRadioDialog(voucherList));
                    } else {
                        binding.rvVouchers.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    android.util.Log.e("ProductDetailActivity", "Error loading vouchers", e);
                    binding.rvVouchers.setVisibility(View.GONE);
                }

                // Load related products (Task 6 — lưới 2 cột)
                try {
                    List<Product> related = productDAO.getRelatedProducts(sqliteId, 6);
                    if (related != null && !related.isEmpty()) {
                        binding.tvRelatedTitle.setVisibility(View.VISIBLE);
                        binding.rvRelated.setVisibility(View.VISIBLE);
                        binding.rvRelated.setLayoutManager(
                                new androidx.recyclerview.widget.GridLayoutManager(this, 2));
                        binding.rvRelated.setNestedScrollingEnabled(false);
                        binding.rvRelated.setAdapter(new ProductAdapter(related));
                    } else {
                        binding.tvRelatedTitle.setVisibility(View.GONE);
                        binding.rvRelated.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    android.util.Log.e("ProductDetailActivity", "Error loading related products", e);
                    binding.tvRelatedTitle.setVisibility(View.GONE);
                    binding.rvRelated.setVisibility(View.GONE);
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

    /**
     * Task 3 (fix) — Tính điểm trung bình, số đánh giá và phân bố sao THỰC TẾ từ DB
     * rồi đổ vào phần tóm tắt (thay cho số liệu tĩnh hard-code).
     */
    private void bindReviewSummary(List<Review> reviews) {
        int count = reviews != null ? reviews.size() : 0;
        int[] dist = new int[6]; // dist[1..5]
        long sum = 0;
        if (reviews != null) {
            for (Review r : reviews) {
                int star = Math.max(1, Math.min(5, r.getRating()));
                dist[star]++;
                sum += r.getRating();
            }
        }
        float avg = count > 0 ? (float) sum / count : 0f;

        binding.tvRatingBig.setText(String.format(Locale.getDefault(), "%.1f", avg));
        binding.rbAverage.setRating(avg);
        binding.ratingBar.setRating(avg);
        binding.tvRating.setText(String.format(Locale.getDefault(), "%.1f (%d đánh giá)", avg, count));
        binding.tvReviewCountSmall.setText("(" + count + " đánh giá)");
        binding.tvReviewSectionTitle.setText("Đánh giá sản phẩm (" + count + ")");

        android.widget.ProgressBar[] bars = {
                binding.pb1, binding.pb2, binding.pb3, binding.pb4, binding.pb5};
        android.widget.TextView[] counts = {
                binding.tvCount1, binding.tvCount2, binding.tvCount3, binding.tvCount4, binding.tvCount5};
        for (int star = 1; star <= 5; star++) {
            int percent = count > 0 ? Math.round(dist[star] * 100f / count) : 0;
            bars[star - 1].setProgress(percent);
            counts[star - 1].setText(String.valueOf(dist[star]));
        }
    }

    /**
     * Task 3 — Danh sách voucher dạng RadioGroup (chỉ chọn 1).
     */
    private void showVoucherRadioDialog(List<Voucher> vouchers) {
        View content = LayoutInflater.from(this).inflate(R.layout.dialog_voucher_radio, null);
        RadioGroup rg = content.findViewById(R.id.rgVouchers);
        View btnApply = content.findViewById(R.id.btnApplyVoucherRadio);

        int pink = ContextCompat.getColor(this, R.color.brand_pink);
        int textPrimary = ContextCompat.getColor(this, R.color.text_primary);
        int padV = (int) (12 * getResources().getDisplayMetrics().density);

        for (int i = 0; i < vouchers.size(); i++) {
            Voucher v = vouchers.get(i);
            RadioButton rb = new RadioButton(this);
            rb.setId(View.generateViewId());
            rb.setTag(i);
            rb.setText(buildVoucherLabel(v));
            rb.setTextColor(textPrimary);
            rb.setTextSize(13f);
            rb.setLineSpacing(0f, 1.15f);
            rb.setPadding(rb.getPaddingLeft() + padV / 2, padV, padV, padV);
            rb.setButtonTintList(android.content.res.ColorStateList.valueOf(pink));
            rg.addView(rb, new RadioGroup.LayoutParams(
                    RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT));
        }

        com.google.android.material.bottomsheet.BottomSheetDialog dialog =
                new com.google.android.material.bottomsheet.BottomSheetDialog(this);
        dialog.setContentView(content);

        btnApply.setOnClickListener(v -> {
            int checkedId = rg.getCheckedRadioButtonId();
            if (checkedId == -1) {
                Toast.makeText(this, "Vui lòng chọn 1 voucher", Toast.LENGTH_SHORT).show();
                return;
            }
            RadioButton checked = content.findViewById(checkedId);
            Voucher selected = vouchers.get((int) checked.getTag());
            binding.btnSeeAllVouchers.setText("Mã: " + selected.getCode());
            Toast.makeText(this, "Đã chọn voucher " + selected.getCode(), Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        dialog.show();
    }

    private String buildVoucherLabel(Voucher v) {
        String discount = "percent".equalsIgnoreCase(v.getDiscountType())
                ? "Giảm " + (int) v.getDiscountValue() + "%"
                : "Giảm " + String.format(Locale.getDefault(), "%,.0fđ", v.getDiscountValue());
        String minOrder = v.getMinOrderAmount() > 0
                ? "  •  Đơn tối thiểu " + String.format(Locale.getDefault(), "%,.0fđ", v.getMinOrderAmount())
                : "";
        return v.getCode() + "\n" + discount + minOrder + "  •  HSD " + v.getExpiryDate();
    }
}
