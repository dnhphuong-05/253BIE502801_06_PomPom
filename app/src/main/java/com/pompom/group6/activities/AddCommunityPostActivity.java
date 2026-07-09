package com.pompom.group6.activities;

import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
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

public class AddCommunityPostActivity extends SwipeBackActivity {

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
        setupUserHeader();
        setupImageSelection();
        setupHashtagAnalysis();
        setupPostActions();
        setupBackNavigation();
    }

    /** Hiển thị đúng tên/avatar người dùng đang đăng nhập, giống HomeFragment/CommunityFragment. */
    private void setupUserHeader() {
        String userOid = com.pompom.group6.network.Session.getUserOid(this);
        if (userOid == null) {
            binding.tvUserName.setText(R.string.app_name);
            return;
        }
        binding.tvUserName.setText(com.pompom.group6.network.Session.getUserName(this));

        com.pompom.group6.network.ApiClient.get().getUser(userOid)
                .enqueue(new retrofit2.Callback<com.pompom.group6.network.dto.ApiUser>() {
                    @Override
                    public void onResponse(retrofit2.Call<com.pompom.group6.network.dto.ApiUser> call,
                                           retrofit2.Response<com.pompom.group6.network.dto.ApiUser> resp) {
                        if (!resp.isSuccessful() || resp.body() == null) return;
                        com.pompom.group6.network.dto.ApiUser user = resp.body();
                        if (!TextUtils.isEmpty(user.fullName)) {
                            binding.tvUserName.setText(user.fullName);
                        }
                        com.bumptech.glide.Glide.with(AddCommunityPostActivity.this)
                                .load(user.avatarUrl)
                                .placeholder(R.drawable.ic_avatar)
                                .error(R.drawable.ic_avatar)
                                .into(binding.ivUserAvatar);
                    }
                    @Override
                    public void onFailure(retrofit2.Call<com.pompom.group6.network.dto.ApiUser> call, Throwable t) {}
                });
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

            String userOid = com.pompom.group6.network.Session.getUserOid(this);
            if (userOid == null) {
                Toast.makeText(this, "Bạn cần đăng nhập để đăng bài", Toast.LENGTH_SHORT).show();
                return;
            }

            String fullContent = content + (hashtags.isEmpty() ? "" : "\n\n" + hashtags);
            setPosting(true);
            uploadImagesThenSubmit(userOid, fullContent, new ArrayList<>(selectedUris), 0, new ArrayList<>());
        };

        binding.btnPostTop.setOnClickListener(postListener);
        binding.btnPostBottom.setOnClickListener(postListener);
    }

    /** Tải từng ảnh đã chọn lên Cloudinary trước (giống AddStoryActivity), rồi mới gửi
     * URL thật lên backend — trước đây gửi thẳng content:// URI cục bộ nên ảnh không tải lên được. */
    private void uploadImagesThenSubmit(String userOid, String fullContent, List<Uri> uris, int index, List<String> uploadedUrls) {
        if (index >= uris.size()) {
            submitPost(userOid, fullContent, uploadedUrls);
            return;
        }
        com.pompom.group6.utils.CloudinaryUploader.upload(uris.get(index), "image",
                new com.pompom.group6.utils.CloudinaryUploader.Callback() {
                    @Override
                    public void onSuccess(String secureUrl) {
                        uploadedUrls.add(secureUrl);
                        uploadImagesThenSubmit(userOid, fullContent, uris, index + 1, uploadedUrls);
                    }
                    @Override
                    public void onError(String message) {
                        setPosting(false);
                        Toast.makeText(AddCommunityPostActivity.this, "Tải ảnh lên thất bại: " + message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void submitPost(String userOid, String fullContent, List<String> imageUrls) {
        com.pompom.group6.network.ApiClient.get()
                .createPost(new com.pompom.group6.network.dto.CreatePostRequest(userOid, fullContent, imageUrls))
                .enqueue(new retrofit2.Callback<com.pompom.group6.network.dto.ApiCommunityPost>() {
                    @Override
                    public void onResponse(retrofit2.Call<com.pompom.group6.network.dto.ApiCommunityPost> call,
                                           retrofit2.Response<com.pompom.group6.network.dto.ApiCommunityPost> resp) {
                        if (resp.isSuccessful()) {
                            Toast.makeText(AddCommunityPostActivity.this, "Đăng bài viết thành công!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            setPosting(false);
                            Toast.makeText(AddCommunityPostActivity.this, "Có lỗi xảy ra, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(retrofit2.Call<com.pompom.group6.network.dto.ApiCommunityPost> call, Throwable t) {
                        setPosting(false);
                        Toast.makeText(AddCommunityPostActivity.this, "Không kết nối được máy chủ", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setPosting(boolean posting) {
        binding.btnPostTop.setEnabled(!posting);
        binding.btnPostBottom.setEnabled(!posting);
        binding.btnPostBottom.setText(posting ? "Đang đăng..." : "ĐĂNG BÀI NGAY");
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
        com.pompom.group6.utils.PomPomDialog.confirm(this, "✏️", "Hủy bài viết?",
                "Bạn có chắc chắn muốn hủy bài viết đang tạo không? Những gì bạn nhập sẽ không được lưu.",
                "Hủy bài", "Tiếp tục viết", this::finish);
    }
}
