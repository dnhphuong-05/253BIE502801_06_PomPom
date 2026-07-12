package com.pompom.group6.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.pompom.group6.R;
import com.pompom.group6.adapters.ImagePreviewAdapter;
import com.pompom.group6.databinding.ActivityOrderReviewBinding;
import com.pompom.group6.databinding.ItemOrderReviewProductBinding;
import com.pompom.group6.network.ApiClient;
import com.pompom.group6.network.Session;
import com.pompom.group6.network.dto.ApiOrderDetail;
import com.pompom.group6.utils.CloudinaryUploader;
import com.pompom.group6.utils.StatusBarUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Đánh giá từng sản phẩm trong 1 đơn "Đã giao" — chấm sao + nhận xét + ảnh ngay dưới mỗi sản phẩm,
 * sau khi gửi hỏi người dùng có muốn đăng lại đánh giá lên Cộng đồng không.
 */
public class OrderReviewActivity extends SwipeBackActivity {

    private static final String EXTRA_ORDER_ID = "order_id";

    public static void start(Context context, String orderId) {
        Intent i = new Intent(context, OrderReviewActivity.class);
        i.putExtra(EXTRA_ORDER_ID, orderId);
        context.startActivity(i);
    }

    /** Trạng thái đánh giá của 1 sản phẩm trong đơn — giữ để build request khi bấm Gửi. */
    private static class ItemReview {
        String productId;
        String productName;
        int rating = 0;
        EditText commentInput;
        List<Uri> images = new ArrayList<>();
        ImagePreviewAdapter imageAdapter;
        ImageView[] stars;
    }

    private ActivityOrderReviewBinding binding;
    private String orderId;
    private String userOid;
    private final List<ItemReview> reviews = new ArrayList<>();
    private int activeImagePickerIndex = -1;

    private final ActivityResultLauncher<PickVisualMediaRequest> pickMultipleMedia =
            registerForActivityResult(new ActivityResultContracts.PickMultipleVisualMedia(5), uris -> {
                if (uris.isEmpty() || activeImagePickerIndex < 0 || activeImagePickerIndex >= reviews.size()) return;
                ItemReview r = reviews.get(activeImagePickerIndex);
                r.images.addAll(uris);
                r.imageAdapter.notifyDataSetChanged();
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrderReviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        StatusBarUtils.applyPinkHeader(this);
        StatusBarUtils.applyHeaderContentInsets(this, binding.header.getRoot(), binding.getRoot());

        binding.header.tvHeaderTitle.setText("Đánh giá đơn hàng");
        binding.header.btnBack.setOnClickListener(v -> finish());
        binding.btnSubmitReview.setOnClickListener(v -> submitAll());

        orderId = getIntent().getStringExtra(EXTRA_ORDER_ID);
        userOid = Session.getUserOid(this);
        load();
    }

    private void load() {
        if (orderId == null || userOid == null) {
            finish();
            return;
        }
        binding.progressBar.setVisibility(View.VISIBLE);
        ApiClient.get().getOrder(orderId, userOid).enqueue(new Callback<ApiOrderDetail>() {
            @Override
            public void onResponse(@NonNull Call<ApiOrderDetail> call, @NonNull Response<ApiOrderDetail> resp) {
                if (binding == null) return;
                binding.progressBar.setVisibility(View.GONE);
                if (!resp.isSuccessful() || resp.body() == null) {
                    Toast.makeText(OrderReviewActivity.this, "Không tải được đơn hàng", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }
                buildForm(resp.body());
            }

            @Override
            public void onFailure(@NonNull Call<ApiOrderDetail> call, @NonNull Throwable t) {
                if (binding == null) return;
                binding.progressBar.setVisibility(View.GONE);
                Toast.makeText(OrderReviewActivity.this, "Không kết nối được máy chủ", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    private void buildForm(ApiOrderDetail order) {
        binding.itemsContainer.removeAllViews();
        reviews.clear();

        List<ApiOrderDetail.Item> pending = new ArrayList<>();
        if (order.items != null) {
            for (ApiOrderDetail.Item it : order.items) {
                if (!it.isReviewed && it.productId != null) pending.add(it);
            }
        }
        if (pending.isEmpty()) {
            Toast.makeText(this, "Đơn hàng này đã được đánh giá đủ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        for (ApiOrderDetail.Item it : pending) {
            addItemRow(it);
        }
    }

    private void addItemRow(ApiOrderDetail.Item item) {
        ItemOrderReviewProductBinding row = ItemOrderReviewProductBinding.inflate(
                LayoutInflater.from(this), binding.itemsContainer, false);

        ItemReview state = new ItemReview();
        state.productId = item.productId;
        state.productName = item.productName != null ? item.productName : "Sản phẩm";
        state.commentInput = row.etComment;
        state.stars = new ImageView[]{row.star1, row.star2, row.star3, row.star4, row.star5};
        reviews.add(state);

        row.tvProductName.setText(state.productName);
        Glide.with(this).load(item.productThumbnail).placeholder(R.drawable.logo_pompom).into(row.ivProduct);

        for (int i = 0; i < state.stars.length; i++) {
            int starValue = i + 1;
            state.stars[i].setOnClickListener(v -> {
                state.rating = starValue;
                paintStars(state);
                updateSubmitEnabled();
            });
        }
        paintStars(state);

        state.imageAdapter = new ImagePreviewAdapter(state.images, position -> {
            state.images.remove(position);
            state.imageAdapter.notifyDataSetChanged();
            row.rvReviewImages.setVisibility(state.images.isEmpty() ? View.GONE : View.VISIBLE);
        });
        row.rvReviewImages.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        row.rvReviewImages.setAdapter(state.imageAdapter);

        row.btnAddPhoto.setOnClickListener(v -> {
            activeImagePickerIndex = reviews.indexOf(state);
            pickMultipleMedia.launch(new PickVisualMediaRequest.Builder()
                    .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                    .build());
        });
        // Hiện lại dải ảnh khi vừa thêm (adapter rỗng ban đầu nên đang gone theo XML).
        state.imageAdapter.registerAdapterDataObserver(new androidx.recyclerview.widget.RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                row.rvReviewImages.setVisibility(state.images.isEmpty() ? View.GONE : View.VISIBLE);
            }
        });

        binding.itemsContainer.addView(row.getRoot());
    }

    private void paintStars(ItemReview state) {
        for (int i = 0; i < state.stars.length; i++) {
            state.stars[i].setImageResource(i < state.rating ? R.drawable.ic_star : R.drawable.ic_star_outline);
        }
    }

    private void updateSubmitEnabled() {
        boolean allRated = !reviews.isEmpty();
        for (ItemReview r : reviews) {
            if (r.rating <= 0) { allRated = false; break; }
        }
        binding.btnSubmitReview.setEnabled(allRated);
    }

    // ------------------------------------------------------------------ submit

    private void submitAll() {
        binding.btnSubmitReview.setEnabled(false);
        binding.progressBar.setVisibility(View.VISIBLE);
        submitNext(0);
    }

    private void submitNext(int index) {
        if (index >= reviews.size()) {
            if (binding == null) return;
            binding.progressBar.setVisibility(View.GONE);
            promptCrossPost();
            return;
        }
        ItemReview r = reviews.get(index);
        if (r.images.isEmpty()) {
            postReview(r, new ArrayList<>(), () -> submitNext(index + 1));
        } else {
            uploadImagesThenPost(r, new ArrayList<>(r.images), 0, new ArrayList<>(), () -> submitNext(index + 1));
        }
    }

    private void uploadImagesThenPost(ItemReview r, List<Uri> uris, int i, List<String> uploaded, Runnable onDone) {
        if (i >= uris.size()) {
            postReview(r, uploaded, onDone);
            return;
        }
        CloudinaryUploader.upload(uris.get(i), "image", new CloudinaryUploader.Callback() {
            @Override
            public void onSuccess(String secureUrl) {
                uploaded.add(secureUrl);
                uploadImagesThenPost(r, uris, i + 1, uploaded, onDone);
            }

            @Override
            public void onError(String message) {
                // Ảnh lỗi thì bỏ qua ảnh đó, vẫn gửi đánh giá với phần đã upload được.
                uploadImagesThenPost(r, uris, i + 1, uploaded, onDone);
            }
        });
    }

    private void postReview(ItemReview r, List<String> imageUrls, Runnable onDone) {
        Map<String, Object> body = new HashMap<>();
        body.put("user_id", userOid);
        body.put("rating", r.rating);
        body.put("comment", r.commentInput.getText().toString().trim());
        body.put("images", imageUrls);
        body.put("order_id", orderId);
        ApiClient.get().submitReview(r.productId, body).enqueue(new Callback<com.pompom.group6.network.dto.ApiReview>() {
            @Override
            public void onResponse(@NonNull Call<com.pompom.group6.network.dto.ApiReview> call,
                                    @NonNull Response<com.pompom.group6.network.dto.ApiReview> resp) {
                onDone.run();
            }

            @Override
            public void onFailure(@NonNull Call<com.pompom.group6.network.dto.ApiReview> call, @NonNull Throwable t) {
                onDone.run();
            }
        });
    }

    private void promptCrossPost() {
        if (isFinishing()) return;
        new AlertDialog.Builder(this)
                .setTitle("Đăng đánh giá lên Cộng đồng?")
                .setMessage("Chia sẻ cảm nhận của bạn về sản phẩm để mọi người cùng tham khảo nhé!")
                .setPositiveButton("Đăng lên Cộng đồng", (dialog, which) -> goToCommunityPost())
                .setNegativeButton("Không, cảm ơn", (dialog, which) -> finishWithToast())
                .setCancelable(false)
                .show();
    }

    private void goToCommunityPost() {
        StringBuilder content = new StringBuilder();
        for (ItemReview r : reviews) {
            String comment = r.commentInput.getText().toString().trim();
            content.append(r.productName).append(" — ").append(r.rating).append("★");
            if (!comment.isEmpty()) content.append("\n").append(comment);
            content.append("\n\n");
        }
        Intent i = new Intent(this, AddCommunityPostActivity.class);
        i.putExtra(AddCommunityPostActivity.EXTRA_INITIAL_CONTENT, content.toString().trim());
        startActivity(i);
        finish();
    }

    private void finishWithToast() {
        Toast.makeText(this, "Đã gửi đánh giá", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
