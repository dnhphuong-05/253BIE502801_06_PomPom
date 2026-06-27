package com.pompom.group6.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.pompom.group6.R;
import com.pompom.group6.adapters.BannerAdapter;
import com.pompom.group6.adapters.PostAdapter;
import com.pompom.group6.adapters.ProductAdapter;
import com.pompom.group6.database.BannerDAO;
import com.pompom.group6.database.CommunityDAO;
import com.pompom.group6.database.ProductDAO;
import com.pompom.group6.databinding.FragmentHomeBinding;
import com.pompom.group6.models.Banner;
import com.pompom.group6.models.CommunityPost;
import com.pompom.group6.models.Product;

import com.pompom.group6.adapters.FlashSaleAdapter;
import com.pompom.group6.database.PromotionDAO;
import com.pompom.group6.models.PromotionProduct;

import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {

    private static final long BANNER_AUTO_SCROLL_DELAY_MS = 3000L;

    private FragmentHomeBinding binding;
    private ProductDAO productDAO;
    private BannerDAO bannerDAO;
    private CommunityDAO communityDAO;
    private PromotionDAO promotionDAO;
    private final Handler bannerHandler = new Handler(Looper.getMainLooper());
    private Runnable bannerRunnable;
    private ViewPager2.OnPageChangeCallback bannerPageChangeCallback;
    private android.animation.ObjectAnimator progressAnimator;
    private android.os.CountDownTimer flashSaleTimer;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        productDAO = new ProductDAO(requireContext());
        bannerDAO = new BannerDAO(requireContext());
        communityDAO = new CommunityDAO(requireContext());
        promotionDAO = new PromotionDAO(requireContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        android.util.Log.d("HomeFragment", "=== onViewCreated started ===");

        setupBanners();
        setupBestSellers();
        setupCommunityHighlights();
        setupFlashSale();
        android.util.Log.d("HomeFragment", "=== onViewCreated finished ===");
    }

    private void setupBanners() {
        List<Banner> banners = bannerDAO.getAllBanners();
        android.util.Log.d("HomeFragment", "Banners loaded: " + banners.size());

        if (banners.isEmpty()) {
            banners.add(new Banner(1, R.drawable.promotion1, getString(R.string.promo_title)));
            banners.add(new Banner(2, R.drawable.promotion2, "NEW COLLECTION"));
            banners.add(new Banner(3, R.drawable.promotion3, "SUMMER SALE"));
        }

        BannerAdapter adapter = new BannerAdapter(banners);
        binding.vpBanners.setAdapter(adapter);
        binding.vpBanners.setOffscreenPageLimit(1);

        setupIndicators(banners.size());

        // Luxury Slide + Zoom Transformer
        binding.vpBanners.setPageTransformer((page, position) -> {
            if (position < -1 || position > 1) {
                page.setAlpha(0f);
            } else {
                page.setAlpha(1f);
                // Sliding is default if we don't fix TranslationX
            }
        });

        bannerRunnable = () -> {
            if (binding == null || adapter.getItemCount() <= 1) {
                return;
            }

            int nextItem = (binding.vpBanners.getCurrentItem() + 1) % adapter.getItemCount();
            binding.vpBanners.setCurrentItem(nextItem, true);
        };

        bannerPageChangeCallback = new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                
                // Trigger zoom animation on the new page
                RecyclerView rv = (RecyclerView) binding.vpBanners.getChildAt(0);
                BannerAdapter.BannerViewHolder vh = (BannerAdapter.BannerViewHolder) rv.findViewHolderForAdapterPosition(position);
                if (vh != null) {
                    vh.startZoomAnimation();
                }

                updateIndicators(position);
                restartBannerAutoScroll();
            }
        };
        binding.vpBanners.registerOnPageChangeCallback(bannerPageChangeCallback);
        restartBannerAutoScroll();
    }

    private void setupIndicators(int count) {
        binding.layoutIndicators.removeAllViews();
        for (int i = 0; i < count; i++) {
            ProgressBar progressBar = new ProgressBar(requireContext(), null, android.R.attr.progressBarStyleHorizontal);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 8, 1f);
            params.setMargins(8, 0, 8, 0);
            progressBar.setLayoutParams(params);
            progressBar.setMax(1000);
            progressBar.setProgress(0);
            progressBar.setProgressDrawable(requireContext().getDrawable(R.drawable.bg_banner_indicator));
            binding.layoutIndicators.addView(progressBar);
        }
    }

    private void updateIndicators(int position) {
        if (progressAnimator != null) {
            progressAnimator.cancel();
        }

        for (int i = 0; i < binding.layoutIndicators.getChildCount(); i++) {
            ProgressBar pb = (ProgressBar) binding.layoutIndicators.getChildAt(i);
            if (i < position) {
                pb.setProgress(1000);
            } else if (i > position) {
                pb.setProgress(0);
            } else {
                pb.setProgress(0);
                progressAnimator = android.animation.ObjectAnimator.ofInt(pb, "progress", 0, 1000);
                progressAnimator.setDuration(BANNER_AUTO_SCROLL_DELAY_MS);
                progressAnimator.setInterpolator(new android.view.animation.LinearInterpolator());
                progressAnimator.start();
            }
        }
    }

    private void setupBestSellers() {
        List<Product> products = productDAO.getBestSellers(6);
        ProductAdapter adapter = new ProductAdapter(products);
        adapter.setHorizontal(true);
        binding.rvBestSellers.setAdapter(adapter);
    }

    private void setupCommunityHighlights() {
        List<CommunityPost> posts = communityDAO.getTopHighlights(6);
        PostAdapter adapter = new PostAdapter(posts);
        binding.rvCommunityHighlights.setAdapter(adapter);
    }

    private void setupFlashSale() {
        List<PromotionProduct> products = promotionDAO.getFlashSaleProducts(6);
        FlashSaleAdapter adapter = new FlashSaleAdapter(products);
        binding.rvFlashSale.setAdapter(adapter);

        // Countdown Timer Logic (e.g., 2 hours from now)
        long duration = 2 * 60 * 60 * 1000 + 34 * 60 * 1000 + 15 * 1000;
        flashSaleTimer = new android.os.CountDownTimer(duration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long hours = (millisUntilFinished / (1000 * 60 * 60)) % 24;
                long minutes = (millisUntilFinished / (1000 * 60)) % 60;
                long seconds = (millisUntilFinished / 1000) % 60;

                if (binding != null) {
                    binding.tvTimerHours.setText(String.format(Locale.getDefault(), "%02d", hours));
                    binding.tvTimerMinutes.setText(String.format(Locale.getDefault(), "%02d", minutes));
                    binding.tvTimerSeconds.setText(String.format(Locale.getDefault(), "%02d", seconds));
                }
            }

            @Override
            public void onFinish() {
                if (binding != null) {
                    binding.tvTimerHours.setText("00");
                    binding.tvTimerMinutes.setText("00");
                    binding.tvTimerSeconds.setText("00");
                }
            }
        }.start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (binding != null && bannerPageChangeCallback != null) {
            binding.vpBanners.unregisterOnPageChangeCallback(bannerPageChangeCallback);
        }
        if (flashSaleTimer != null) {
            flashSaleTimer.cancel();
        }
        bannerHandler.removeCallbacksAndMessages(null);
        binding = null;
    }

    private void restartBannerAutoScroll() {
        bannerHandler.removeCallbacks(bannerRunnable);
        bannerHandler.postDelayed(bannerRunnable, BANNER_AUTO_SCROLL_DELAY_MS);
    }
}
