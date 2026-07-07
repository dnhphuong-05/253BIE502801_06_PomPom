package com.pompom.group6.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.pompom.group6.R;
import com.pompom.group6.adapters.AvatarOptionAdapter;
import com.pompom.group6.databinding.DialogAvatarEditorBinding;
import com.pompom.group6.network.ApiClient;
import com.pompom.group6.network.Session;
import com.pompom.group6.network.dto.ApiUser;
import com.pompom.group6.network.dto.UserUpdateRequest;
import com.pompom.group6.utils.AvatarUtils;
import com.pompom.group6.utils.CloudinaryUploader;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Popup đổi ảnh đại diện + khung avatar. Chọn avatar mặc định hoặc tải ảnh từ
 * gallery/camera (upload Cloudinary), chọn khung mặc định, rồi Lưu -> cập nhật DB.
 */
public class AvatarEditorBottomSheet extends BottomSheetDialogFragment {

    private static final String ARG_AVATAR = "avatar";
    private static final String ARG_FRAME = "frame";

    public interface OnSaved {
        void onSaved();
    }

    private DialogAvatarEditorBinding binding;
    private AvatarOptionAdapter adapter;
    private OnSaved onSaved;

    // Trạng thái lựa chọn
    private String origAvatar;    // giá trị avatar hiện tại (url / "default:N" / null)
    private String selFrame;      // token khung đang chọn
    private int selAvatarIndex = -1;  // >=0 nếu chọn avatar mặc định
    private Uri pickedUri;        // ảnh mới chọn từ gallery/camera (chưa upload)
    private Uri cameraUri;        // uri tạm cho camera

    public static AvatarEditorBottomSheet newInstance(String avatar, String frame) {
        AvatarEditorBottomSheet f = new AvatarEditorBottomSheet();
        Bundle b = new Bundle();
        b.putString(ARG_AVATAR, avatar);
        b.putString(ARG_FRAME, frame);
        f.setArguments(b);
        return f;
    }

    public void setOnSaved(OnSaved l) {
        this.onSaved = l;
    }

    // ---- Launchers (đăng ký ở field theo khuyến nghị AndroidX) ----
    private final ActivityResultLauncher<PickVisualMediaRequest> pickMedia =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), uri -> {
                if (uri != null) onImagePicked(uri);
            });

    private final ActivityResultLauncher<Uri> takePicture =
            registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
                if (Boolean.TRUE.equals(success) && cameraUri != null) onImagePicked(cameraUri);
            });

    private final ActivityResultLauncher<String> requestCamera =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) launchCamera();
                else toast("Cần quyền máy ảnh để chụp ảnh");
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DialogAvatarEditorBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        origAvatar = getArguments() != null ? getArguments().getString(ARG_AVATAR) : null;
        selFrame = getArguments() != null ? getArguments().getString(ARG_FRAME) : null;
        selAvatarIndex = AvatarUtils.defaultIndex(origAvatar); // nếu đang là avatar mặc định

        adapter = new AvatarOptionAdapter(this::onCell);
        binding.rvOptions.setLayoutManager(new GridLayoutManager(requireContext(), 4));
        binding.rvOptions.setAdapter(adapter);

        bindPreviewAvatar();
        bindPreviewFrame();

        binding.btnModeAvatar.setOnClickListener(v -> setMode(AvatarOptionAdapter.MODE_AVATAR));
        binding.btnModeFrame.setOnClickListener(v -> setMode(AvatarOptionAdapter.MODE_FRAME));
        binding.btnCancel.setOnClickListener(v -> dismiss());
        binding.btnSave.setOnClickListener(v -> save());
    }

    // -------------------------------------------------------------- chọn mode

    private void setMode(int mode) {
        styleMode(binding.btnModeAvatar, mode == AvatarOptionAdapter.MODE_AVATAR);
        styleMode(binding.btnModeFrame, mode == AvatarOptionAdapter.MODE_FRAME);
        binding.rvOptions.setVisibility(View.VISIBLE);
        if (mode == AvatarOptionAdapter.MODE_AVATAR) {
            refreshAvatarGrid();
        } else {
            refreshFrameGrid();
        }
    }

    private void refreshAvatarGrid() {
        String existingUrl = (pickedUri == null && selAvatarIndex < 0 && AvatarUtils.isUrl(origAvatar))
                ? origAvatar : null;
        int selectedPos;
        if (pickedUri != null || existingUrl != null) selectedPos = 0;         // ô ADD (ảnh tùy chọn)
        else if (selAvatarIndex >= 0) selectedPos = selAvatarIndex + 1;         // avatar mặc định
        else selectedPos = -1;
        adapter.showAvatars(pickedUri, existingUrl, selectedPos);
    }

    private void refreshFrameGrid() {
        int selectedPos = Math.max(0, AvatarUtils.frameTokens().indexOf(selFrame == null ? "classic" : selFrame));
        String sample = selAvatarIndex >= 0 ? AvatarUtils.avatarToken(selAvatarIndex) : origAvatar;
        adapter.showFrames(selectedPos, pickedUri, sample);
    }

    // --------------------------------------------------------------- click ô

    private void onCell(int position) {
        if (position < 0) return;
        if (adapter.getMode() == AvatarOptionAdapter.MODE_AVATAR) {
            if (position == 0) {
                chooseImageSource();
            } else {
                selAvatarIndex = position - 1;
                pickedUri = null;
                bindPreviewAvatar();
                refreshAvatarGrid();
            }
        } else {
            selFrame = AvatarUtils.frameTokens().get(position);
            bindPreviewFrame();
            refreshFrameGrid();
        }
    }

    // ------------------------------------------------------------- chọn ảnh

    private void chooseImageSource() {
        final android.app.Dialog d = new android.app.Dialog(requireContext());
        d.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
        View v = getLayoutInflater().inflate(R.layout.dialog_image_source, null);
        d.setContentView(v);
        if (d.getWindow() != null) {
            d.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(Color.TRANSPARENT));
            d.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        v.findViewById(R.id.optCamera).setOnClickListener(x -> { d.dismiss(); startCamera(); });
        v.findViewById(R.id.optGallery).setOnClickListener(x -> { d.dismiss(); startGallery(); });
        v.findViewById(R.id.btnCloseSource).setOnClickListener(x -> d.dismiss());
        d.show();
    }

    private void startCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            launchCamera();
        } else {
            requestCamera.launch(Manifest.permission.CAMERA);
        }
    }

    private void startGallery() {
        pickMedia.launch(new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build());
    }

    private void launchCamera() {
        try {
            File file = new File(requireContext().getCacheDir(), "avatar_camera.jpg");
            cameraUri = FileProvider.getUriForFile(requireContext(),
                    requireContext().getPackageName() + ".fileprovider", file);
            takePicture.launch(cameraUri);
        } catch (Exception e) {
            toast("Không mở được máy ảnh");
        }
    }

    private void onImagePicked(Uri uri) {
        pickedUri = uri;
        selAvatarIndex = -1;
        bindPreviewAvatar();
        if (adapter.getMode() == AvatarOptionAdapter.MODE_AVATAR) refreshAvatarGrid();
    }

    // -------------------------------------------------------------- preview

    private void bindPreviewAvatar() {
        if (pickedUri != null) {
            Glide.with(this).load(pickedUri).centerCrop().into(binding.ivPreviewAvatar);
        } else if (selAvatarIndex >= 0) {
            binding.ivPreviewAvatar.setImageResource(AvatarUtils.DEFAULT_AVATARS[selAvatarIndex]);
        } else {
            AvatarUtils.loadAvatar(requireContext(), origAvatar, binding.ivPreviewAvatar);
        }
    }

    private void bindPreviewFrame() {
        AvatarUtils.applyFrame(binding.ivPreviewFrame, selFrame);
    }

    // ---------------------------------------------------------------- lưu

    private void save() {
        String frame = selFrame == null ? "classic" : selFrame;
        setSaving(true);
        if (pickedUri != null) {
            // Ảnh mới -> upload Cloudinary trước, lấy URL rồi cập nhật.
            CloudinaryUploader.upload(pickedUri, "image", new CloudinaryUploader.Callback() {
                @Override public void onSuccess(String secureUrl) { putUpdate(secureUrl, frame); }
                @Override public void onError(String message) {
                    runOnUi(() -> { setSaving(false); toast("Tải ảnh thất bại: " + message); });
                }
            });
        } else {
            String avatarValue = selAvatarIndex >= 0 ? AvatarUtils.avatarToken(selAvatarIndex) : origAvatar;
            putUpdate(avatarValue, frame);
        }
    }

    private void putUpdate(String avatarValue, String frame) {
        String oid = Session.getUserOid(requireContext());
        if (oid == null) {
            runOnUi(() -> { setSaving(false); toast("Bạn cần đăng nhập"); });
            return;
        }
        ApiClient.get().updateUser(oid, UserUpdateRequest.avatar(avatarValue, frame))
                .enqueue(new Callback<ApiUser>() {
                    @Override
                    public void onResponse(Call<ApiUser> call, Response<ApiUser> resp) {
                        if (binding == null) return;
                        setSaving(false);
                        if (resp.isSuccessful()) {
                            toast("Đã cập nhật ảnh đại diện");
                            if (onSaved != null) onSaved.onSaved();
                            dismiss();
                        } else {
                            toast("Cập nhật thất bại, thử lại");
                        }
                    }

                    @Override
                    public void onFailure(Call<ApiUser> call, Throwable t) {
                        if (binding == null) return;
                        setSaving(false);
                        toast("Lỗi kết nối, thử lại");
                    }
                });
    }

    // -------------------------------------------------------------- helpers

    private void setSaving(boolean saving) {
        if (binding == null) return;
        binding.savingOverlay.setVisibility(saving ? View.VISIBLE : View.GONE);
        binding.btnSave.setEnabled(!saving);
        binding.btnCancel.setEnabled(!saving);
    }

    private void styleMode(MaterialButton b, boolean selected) {
        int pink = ContextCompat.getColor(requireContext(), R.color.brand_pink);
        int white = ContextCompat.getColor(requireContext(), R.color.white);
        if (selected) {
            b.setBackgroundTintList(ColorStateList.valueOf(pink));
            b.setTextColor(white);
            b.setIconTint(ColorStateList.valueOf(white));
            b.setStrokeWidth(0);
        } else {
            b.setBackgroundTintList(ColorStateList.valueOf(Color.TRANSPARENT));
            b.setTextColor(pink);
            b.setIconTint(ColorStateList.valueOf(pink));
            b.setStrokeWidth((int) (1.5f * getResources().getDisplayMetrics().density));
            b.setStrokeColor(ColorStateList.valueOf(pink));
        }
    }

    private void runOnUi(Runnable r) {
        if (isAdded() && getActivity() != null) getActivity().runOnUiThread(r);
    }

    private void toast(String msg) {
        if (getContext() != null) Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
