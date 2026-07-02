package com.pompom.group6.activities;

import android.os.Bundle;
import android.os.Handler;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.pompom.group6.R;
import com.pompom.group6.databinding.ActivityStoryViewerBinding;

import java.util.ArrayList;

public class StoryViewerActivity extends AppCompatActivity {

    private ActivityStoryViewerBinding binding;
    private int progressStatus = 0;
    private final Handler handler = new Handler();
    private Runnable progressRunnable;
    
    private ArrayList<String> names;
    private ArrayList<Integer> images;
    private int currentIndex = 0;
    private static final int STORY_DURATION_MS = 5000; 
    private GestureDetector swipeDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStoryViewerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Full screen setup
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        names = getIntent().getStringArrayListExtra("names");
        images = getIntent().getIntegerArrayListExtra("images");
        currentIndex = getIntent().getIntExtra("startIndex", 0);

        if (names == null || images == null || names.isEmpty()) {
            finish();
            return;
        }

        setupUI();
        setupListeners();
        setupSwipeToDismiss();
        showStory(currentIndex);
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

                // Detect Vertical Swipe (Up or Down) to exit
                if (Math.abs(deltaY) > Math.abs(deltaX) && Math.abs(deltaY) > 100 && Math.abs(velocityY) > 100) {
                    finish();
                    overridePendingTransition(0, R.anim.slide_out_bottom); // Smooth exit
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
        if (index < 0 || index >= names.size()) return;

        binding.tvUserName.setText(names.get(index));
        binding.ivStoryImage.setImageResource(images.get(index));
        binding.ivUserAvatar.setImageResource(images.get(index));
        
        // Reset progress
        stopProgress();
        progressStatus = 0;
        binding.pbStory.setProgress(0);
        startProgress();
    }

    private void nextStory() {
        if (currentIndex < names.size() - 1) {
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

    private void startProgress() {
        progressRunnable = new Runnable() {
            @Override
            public void run() {
                if (progressStatus < 100) {
                    progressStatus++;
                    binding.pbStory.setProgress(progressStatus);
                    handler.postDelayed(this, STORY_DURATION_MS / 100);
                } else {
                    nextStory();
                }
            }
        };
        handler.postDelayed(progressRunnable, STORY_DURATION_MS / 100);
    }

    private void stopProgress() {
        if (progressRunnable != null) {
            handler.removeCallbacks(progressRunnable);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (swipeDetector != null) {
            return swipeDetector.onTouchEvent(event);
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopProgress();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (progressStatus < 100) {
            startProgress();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopProgress();
    }
}
