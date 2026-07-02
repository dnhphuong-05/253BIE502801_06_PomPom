package com.pompom.group6.activities;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.chip.Chip;
import com.pompom.group6.R;
import com.pompom.group6.adapters.ImagePreviewAdapter;
import com.pompom.group6.database.CommunityDAO;
import com.pompom.group6.databinding.ActivityAddCommunityPostBinding;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddCommunityPostActivity extends AppCompatActivity {

    private ActivityAddCommunityPostBinding binding;
    private CommunityDAO communityDAO;
    private final List<Uri> selectedUris = new ArrayList<>();
    private ImagePreviewAdapter previewAdapter;
    private final Map<String, String[]> keywordHashtags = new HashMap<>();

    // Multiple Image Picker Launcher (Max 5 images)
    private final ActivityResultLauncher<PickVisualMediaRequest> pickMultipleMedia =
            registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(5), uris -> {
                if (!uris.isEmpty()) {
                    selectedUris.addAll(uris);
                    updatePreviewVisibility();
                    previewAdapter.notifyDataSetChanged();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddCommunityPostBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        communityDAO = new CommunityDAO(this);
        initKeywordMap();

        setupToolbar();
        setupImageSelection();
        setupHashtagAnalysis();
        setupPostActions();
        setupBackNavigation();
    }

    private void initKeywordMap() {
        keywordHashtags.put("son", new String[]{"#lipstick", "#makeup", "#beauty"});
        keywordHashtags.put("da", new String[]{"#skincare", "#routine", "#kbeauty"});
        keywordHashtags.put("review", new String[]{"#pompomreview", "#reviewlamdep", "#honestreview"});
        keywordHashtags.put("đẹp", new String[]{"#beautiful", "#glowup", "#pretty"});
        keywordHashtags.put("mụn", new String[]{"#acnecare", "#trimun", "#skincaretips"});
        keywordHashtags.put("trắng", new String[]{"#whitening", "#duongtrang", "#brightening"});
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> showDiscardConfirmation());
    }

    private void setupImageSelection() {
        previewAdapter = new ImagePreviewAdapter(selectedUris, position -> {
            selectedUris.remove(position);
            updatePreviewVisibility();
            previewAdapter.notifyDataSetChanged();
        });
        
        binding.rvImagePreview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        binding.rvImagePreview.setAdapter(previewAdapter);

        binding.btnSelectImage.setOnClickListener(v -> {
            pickMultipleMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });
    }

    private void updatePreviewVisibility() {
        binding.rvImagePreview.setVisibility(selectedUris.isEmpty() ? View.GONE : View.VISIBLE);
    }

    private void setupHashtagAnalysis() {
        binding.etContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                analyzeContent(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void analyzeContent(String content) {
        binding.cgHashtagSuggestions.removeAllViews();
        List<String> suggested = new ArrayList<>();

        for (Map.Entry<String, String[]> entry : keywordHashtags.entrySet()) {
            if (content.contains(entry.getKey())) {
                for (String tag : entry.getValue()) {
                    if (!suggested.contains(tag)) {
                        suggested.add(tag);
                        addHashtagChip(tag);
                    }
                }
            }
        }
        
        // Always show default tags if empty
        if (suggested.isEmpty()) {
            String[] defaults = {"#Beauty", "#Skincare", "#PomPom", "#Trending"};
            for (String tag : defaults) {
                addHashtagChip(tag);
            }
        }
    }

    private void addHashtagChip(String tag) {
        Chip chip = new Chip(this);
        chip.setText(tag);
        chip.setChipBackgroundColorResource(R.color.brand_pink_light);
        chip.setTextColor(ContextCompat.getColor(this, R.color.brand_pink));
        chip.setChipStrokeWidth(0);
        chip.setOnClickListener(v -> {
            String currentText = binding.etHashtags.getText().toString().trim();
            if (!currentText.contains(tag)) {
                binding.etHashtags.setText(currentText.isEmpty() ? tag : currentText + " " + tag);
            }
        });
        binding.cgHashtagSuggestions.addView(chip);
    }

    private void setupPostActions() {
        View.OnClickListener postListener = v -> {
            String content = binding.etContent.getText().toString().trim();
            String hashtags = binding.etHashtags.getText().toString().trim();
            
            if (content.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập nội dung bài viết", Toast.LENGTH_SHORT).show();
                return;
            }

            String fullContent = content + (hashtags.isEmpty() ? "" : "\n\n" + hashtags);

            int selectedId = binding.rgPostType.getCheckedRadioButtonId();
            RadioButton rb = findViewById(selectedId);
            String postType = rb != null ? rb.getText().toString() : "Review";

            List<String> pathList = new ArrayList<>();
            for (Uri uri : selectedUris) {
                pathList.add(uri.toString());
            }
            String imagesPath = TextUtils.join(",", pathList);

            long result = communityDAO.insertPost(1, fullContent, imagesPath, postType);

            if (result != -1) {
                Toast.makeText(this, "Đăng bài viết thành công!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Có lỗi xảy ra, vui lòng thử lại", Toast.LENGTH_SHORT).show();
            }
        };

        binding.btnPostTop.setOnClickListener(postListener);
        binding.btnPostBottom.setOnClickListener(postListener);
    }

    private void setupBackNavigation() {
        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!binding.etContent.getText().toString().trim().isEmpty() || !selectedUris.isEmpty()) {
                    showDiscardConfirmation();
                } else {
                    setEnabled(false);
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });
    }

    private void showDiscardConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Hủy bài viết?")
                .setMessage("Bạn có chắc chắn muốn hủy bài viết đang tạo không? Những gì bạn nhập sẽ không được lưu.")
                .setPositiveButton("Hủy bài", (dialog, which) -> finish())
                .setNegativeButton("Tiếp tục viết", null)
                .show();
    }
}
