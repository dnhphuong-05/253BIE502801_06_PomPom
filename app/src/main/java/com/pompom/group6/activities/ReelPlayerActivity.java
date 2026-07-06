package com.pompom.group6.activities;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.OptIn;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.viewpager2.widget.ViewPager2;

import com.pompom.group6.adapters.ReelPagerAdapter;
import com.pompom.group6.databinding.ActivityReelPlayerBinding;
import com.pompom.group6.network.dto.ApiReel;

import java.util.ArrayList;

/** Lướt dọc liên tục qua các thước phim, giống trải nghiệm Reels của Facebook/Instagram. */
public class ReelPlayerActivity extends SwipeBackActivity {

    public static final String EXTRA_REELS = "extra_reels";
    public static final String EXTRA_START_INDEX = "extra_start_index";

    private ActivityReelPlayerBinding binding;
    private ExoPlayer player;
    private ReelPagerAdapter adapter;

    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReelPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Video toàn màn hình, tràn qua status bar/nav bar — nền luôn đen nên icon để trắng.
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        getWindow().setStatusBarColor(Color.TRANSPARENT);
        getWindow().setNavigationBarColor(Color.TRANSPARENT);
        WindowInsetsControllerCompat controller =
                WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        controller.setAppearanceLightStatusBars(false);
        controller.setAppearanceLightNavigationBars(false);

        @SuppressWarnings("unchecked")
        ArrayList<ApiReel> reels = (ArrayList<ApiReel>) getIntent().getSerializableExtra(EXTRA_REELS);
        int startIndex = getIntent().getIntExtra(EXTRA_START_INDEX, 0);
        if (reels == null || reels.isEmpty()) {
            finish();
            return;
        }

        binding.btnBack.setOnClickListener(v -> finish());

        player = new ExoPlayer.Builder(this).build();
        adapter = new ReelPagerAdapter(reels, player);
        binding.vpReels.setOrientation(ViewPager2.ORIENTATION_VERTICAL);
        binding.vpReels.setAdapter(adapter);
        binding.vpReels.setCurrentItem(startIndex, false);
        adapter.setActivePosition(startIndex);

        binding.vpReels.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                adapter.setActivePosition(position);
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) player.setPlayWhenReady(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
