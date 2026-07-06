package com.pompom.group6.activities;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;

import com.bumptech.glide.Glide;
import com.pompom.group6.R;
import com.pompom.group6.databinding.ActivityAddStoryBinding;
import com.pompom.group6.network.ApiClient;
import com.pompom.group6.network.Session;
import com.pompom.group6.network.dto.ApiNearbyPost;
import com.pompom.group6.network.dto.NearbyPostRequest;
import com.pompom.group6.utils.CloudinaryUploader;
import com.pompom.group6.utils.LocationHelper;
import com.pompom.group6.utils.StatusBarUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** Đăng story 24h (ảnh/video) kèm vị trí GPS hiện tại. */
public class AddStoryActivity extends SwipeBackActivity {

    private ActivityAddStoryBinding binding;
    private Uri pickedUri;
    private String mediaType; // "image" | "video"

    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri == null) return;
                pickedUri = uri;
                String mime = getContentResolver().getType(uri);
                mediaType = (mime != null && mime.startsWith("video/")) ? "video" : "image";
                binding.layoutPickPrompt.setVisibility(View.GONE);
                binding.ivMediaPreview.setVisibility(View.VISIBLE);
                Glide.with(this).load(uri).into(binding.ivMediaPreview);
            });

    private final ActivityResultLauncher<String> requestLocationPermission =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) {
                    doSubmit();
                } else {
                    Toast.makeText(this, "Cần quyền vị trí để đăng story theo khu vực", Toast.LENGTH_SHORT).show();
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddStoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        StatusBarUtils.applyPinkHeader(this);
        StatusBarUtils.applyHeaderContentInsets(this, binding.header.getRoot(), binding.getRoot());

        binding.header.btnBack.setOnClickListener(v -> finish());
        binding.header.tvHeaderTitle.setText("Đăng story");

        binding.cardMediaPreview.setOnClickListener(v -> pickMedia.launch(
                new PickVisualMediaRequest.Builder()
                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageAndVideo.INSTANCE)
                        .build()));

        binding.btnSubmitStory.setOnClickListener(v -> submit());
    }

    private void submit() {
        if (pickedUri == null) {
            Toast.makeText(this, "Vui lòng chọn ảnh hoặc video", Toast.LENGTH_SHORT).show();
            return;
        }
        if (Session.getUserOid(this) == null) {
            Toast.makeText(this, "Vui lòng đăng nhập để đăng story", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!LocationHelper.hasPermission(this)) {
            requestLocationPermission.launch(android.Manifest.permission.ACCESS_FINE_LOCATION);
            return;
        }
        doSubmit();
    }

    private void doSubmit() {
        binding.btnSubmitStory.setEnabled(false);
        binding.btnSubmitStory.setText("Đang lấy vị trí...");

        LocationHelper.getCurrentLocation(this, new LocationHelper.Callback() {
            @Override
            public void onLocation(double lat, double lng) {
                uploadAndPost(lat, lng);
            }

            @Override
            public void onUnavailable() {
                resetSubmitButton();
                Toast.makeText(AddStoryActivity.this,
                        "Không lấy được vị trí, vui lòng bật GPS và thử lại", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void uploadAndPost(double lat, double lng) {
        binding.btnSubmitStory.setText("Đang tải lên...");
        CloudinaryUploader.upload(pickedUri, mediaType, new CloudinaryUploader.Callback() {
            @Override
            public void onSuccess(String secureUrl) {
                String caption = binding.etCaption.getText() != null
                        ? binding.etCaption.getText().toString().trim() : "";
                NearbyPostRequest body = new NearbyPostRequest(
                        Session.getUserOid(AddStoryActivity.this), secureUrl, mediaType, caption, lat, lng);
                ApiClient.get().createNearbyPost(body).enqueue(new Callback<ApiNearbyPost>() {
                    @Override
                    public void onResponse(Call<ApiNearbyPost> call, Response<ApiNearbyPost> resp) {
                        if (resp.isSuccessful()) {
                            Toast.makeText(AddStoryActivity.this, "Đã đăng story!", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            resetSubmitButton();
                            Toast.makeText(AddStoryActivity.this, "Đăng story thất bại, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiNearbyPost> call, Throwable t) {
                        resetSubmitButton();
                        Toast.makeText(AddStoryActivity.this, "Không kết nối được máy chủ", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onError(String message) {
                resetSubmitButton();
                Toast.makeText(AddStoryActivity.this, "Tải media lên thất bại: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void resetSubmitButton() {
        binding.btnSubmitStory.setEnabled(true);
        binding.btnSubmitStory.setText("Đăng story");
    }
}
