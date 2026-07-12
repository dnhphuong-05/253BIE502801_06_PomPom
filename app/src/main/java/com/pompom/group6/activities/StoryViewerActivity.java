package com.pompom.group6.activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.core.content.ContextCompat;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;

import com.bumptech.glide.Glide;
import com.pompom.group6.R;
import com.pompom.group6.databinding.ActivityStoryViewerBinding;
import com.pompom.group6.network.dto.ApiNearbyPost;
import com.pompom.group6.utils.StatusBarUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/** Xem story 24h toàn màn hình — hỗ trợ cả ảnh (hẹn giờ 5s) và video (theo đúng thời lượng), tự chuyển sang story kế tiếp. */
public class StoryViewerActivity extends SwipeBackActivity {

    public static final String EXTRA_POSTS = "extra_posts";
    public static final String EXTRA_START_INDEX = "extra_start_index";
    private static final int IMAGE_DURATION_MS = 5000;

    private ActivityStoryViewerBinding binding;
    private ArrayList<ApiNearbyPost> posts;
    private int currentIndex = 0;
    private final Handler handler = new Handler();
    private Runnable progressRunnable;
    private ExoPlayer player;
    private ProgressBar[] segments;
    private GestureDetector swipeDetector;

    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStoryViewerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Nền toàn màn hình đen — status bar trong suốt hoà vào nền, icon sáng để đọc được
        // (trước đây chỉ set layout fullscreen mà quên set màu, để lộ màu tím mặc định của theme).
        StatusBarUtils.applyDarkImmersive(this);

        //noinspection unchecked
        posts = (ArrayList<ApiNearbyPost>) getIntent().getSerializableExtra(EXTRA_POSTS);
        currentIndex = getIntent().getIntExtra(EXTRA_START_INDEX, 0);

        if (posts == null || posts.isEmpty()) {
            finish();
            return;
        }

        player = new ExoPlayer.Builder(this).build();
        binding.playerViewStory.setPlayer(player);

        buildProgressSegments();
        setupUI();
        setupListeners();
        setupSwipeToDismiss();
        showStory(currentIndex);
    }

    private void buildProgressSegments() {
        binding.layoutProgress.removeAllViews();
        segments = new ProgressBar[posts.size()];
        for (int i = 0; i < posts.size(); i++) {
            ProgressBar bar = new ProgressBar(this, null, android.R.attr.progressBarStyleHorizontal);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 1f);
            lp.setMarginStart(2);
            lp.setMarginEnd(2);
            bar.setLayoutParams(lp);
            bar.setProgressTintList(android.content.res.ColorStateList.valueOf(
                    ContextCompat.getColor(this, R.color.white)));
            bar.setProgress(0);
            segments[i] = bar;
            binding.layoutProgress.addView(bar);
        }
    }

    private void setupUI() {
        binding.btnClose.setOnClickListener(v -> finish());
    }

    private void setupSwipeToDismiss() {
        swipeDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1 == null || e2 == null) return false;
                float deltaY = e2.getY() - e1.getY();
                float deltaX = e2.getX() - e1.getX();
                if (Math.abs(deltaY) > Math.abs(deltaX) && Math.abs(deltaY) > 100 && Math.abs(velocityY) > 100) {
                    finish();
                    overridePendingTransition(0, R.anim.slide_out_bottom);
                    return true;
                }
                return false;
            }
        });
        binding.rootLayout.setOnTouchListener((v, event) -> {
            swipeDetector.onTouchEvent(event);
            return true;
        });
    }

    private void setupListeners() {
        binding.viewNext.setOnClickListener(v -> nextStory());
        binding.viewPrevious.setOnClickListener(v -> previousStory());
        binding.btnLike.setOnClickListener(v -> {
            binding.btnLike.setImageResource(R.drawable.ic_heart);
            binding.btnLike.setColorFilter(ContextCompat.getColor(this, R.color.brand_pink));
            Toast.makeText(this, "Đã thả tim!", Toast.LENGTH_SHORT).show();
        });
    }

    private void showStory(int index) {
        if (index < 0 || index >= posts.size()) return;
        stopProgress();
        player.stop();
        player.clearMediaItems();

        for (int i = 0; i < segments.length; i++) {
            segments[i].setProgress(i < index ? 100 : 0);
        }

        ApiNearbyPost post = posts.get(index);
        binding.tvUserName.setText(post.userName != null ? post.userName : "Người dùng");
        binding.tvTime.setText(timeAgo(post.createdAt));
        Glide.with(this).load(post.userAvatar).placeholder(R.drawable.ic_avatar).into(binding.ivUserAvatar);

        if ("video".equals(post.mediaType)) {
            binding.ivStoryImage.setVisibility(View.GONE);
            binding.playerViewStory.setVisibility(View.VISIBLE);
            player.setMediaItem(MediaItem.fromUri(post.mediaUrl));
            player.prepare();
            player.setPlayWhenReady(true);
            player.addListener(new Player.Listener() {
                @Override
                public void onPlaybackStateChanged(int state) {
                    if (state == Player.STATE_ENDED) nextStory();
                }
            });
            startVideoProgress(index);
        } else {
            binding.playerViewStory.setVisibility(View.GONE);
            binding.ivStoryImage.setVisibility(View.VISIBLE);
            Glide.with(this).load(post.mediaUrl).placeholder(R.drawable.promotion1).into(binding.ivStoryImage);
            startImageProgress(index);
        }
    }

    private void startImageProgress(int index) {
        final long startTime = System.currentTimeMillis();
        progressRunnable = new Runnable() {
            @Override
            public void run() {
                int percent = (int) Math.min(100, (System.currentTimeMillis() - startTime) * 100 / IMAGE_DURATION_MS);
                segments[index].setProgress(percent);
                if (percent >= 100) {
                    nextStory();
                } else {
                    handler.postDelayed(this, 50);
                }
            }
        };
        handler.postDelayed(progressRunnable, 50);
    }

    private void startVideoProgress(int index) {
        progressRunnable = new Runnable() {
            @Override
            public void run() {
                long duration = player.getDuration();
                if (duration > 0) {
                    int percent = (int) Math.min(100, player.getCurrentPosition() * 100 / duration);
                    segments[index].setProgress(percent);
                }
                handler.postDelayed(this, 100);
            }
        };
        handler.postDelayed(progressRunnable, 100);
    }

    private void nextStory() {
        if (currentIndex < posts.size() - 1) {
            currentIndex++;
            showStory(currentIndex);
        } else {
            finish();
        }
    }

    private void previousStory() {
        if (currentIndex > 0) {
            currentIndex--;
            showStory(currentIndex);
        } else {
            showStory(0);
        }
    }

    private void stopProgress() {
        if (progressRunnable != null) handler.removeCallbacks(progressRunnable);
    }

    private String timeAgo(String isoDate) {
        if (isoDate == null) return "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = sdf.parse(isoDate.length() >= 19 ? isoDate.substring(0, 19) : isoDate);
            if (date == null) return "";
            long minutes = (System.currentTimeMillis() - date.getTime()) / 60000;
            if (minutes < 1) return "Vừa xong";
            if (minutes < 60) return minutes + " phút";
            return (minutes / 60) + " giờ";
        } catch (ParseException e) {
            return "";
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (swipeDetector != null) return swipeDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopProgress();
        if (player != null) player.setPlayWhenReady(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null && "video".equals(posts.get(currentIndex).mediaType)) {
            player.setPlayWhenReady(true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopProgress();
        if (player != null) {
            player.release();
            player = null;
        }
    }

}
