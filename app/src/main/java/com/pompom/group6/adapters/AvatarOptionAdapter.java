package com.pompom.group6.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pompom.group6.databinding.ItemAvatarOptionBinding;
import com.pompom.group6.utils.AvatarUtils;

import java.util.List;

/**
 * Lưới lựa chọn trong popup đổi avatar.
 *  - Chế độ AVATAR: ô 0 = "ADD" (tải ảnh gallery/camera), ô 1..N = avatar mặc định.
 *  - Chế độ FRAME : mỗi ô = 1 khung mặc định (kèm nhãn), xem trước trên avatar hiện tại.
 */
public class AvatarOptionAdapter extends RecyclerView.Adapter<AvatarOptionAdapter.VH> {

    public static final int MODE_AVATAR = 0;
    public static final int MODE_FRAME = 1;

    public interface OnCellClick {
        void onCell(int position);
    }

    private final OnCellClick listener;
    private final List<String> frameTokens = AvatarUtils.frameTokens();

    private int mode = MODE_AVATAR;
    private int selectedPos = -1;
    private Uri customUri;       // ảnh vừa chọn cho ô ADD
    private String customUrl;    // ảnh avatar URL hiện có (hiện ở ô ADD nếu chưa chọn ảnh mới)
    private String sampleAvatar; // avatar hiện tại (URL hoặc token) để xem trước khung

    public AvatarOptionAdapter(OnCellClick listener) {
        this.listener = listener;
    }

    public void showAvatars(Uri customUri, String customUrl, int selectedPos) {
        this.mode = MODE_AVATAR;
        this.customUri = customUri;
        this.customUrl = customUrl;
        this.selectedPos = selectedPos;
        notifyDataSetChanged();
    }

    public void showFrames(int selectedPos, Uri sampleUri, String sampleAvatar) {
        this.mode = MODE_FRAME;
        this.selectedPos = selectedPos;
        this.customUri = sampleUri;
        this.sampleAvatar = sampleAvatar;
        notifyDataSetChanged();
    }

    public void setSelected(int pos) {
        selectedPos = pos;
        notifyDataSetChanged();
    }

    public void setCustomUri(Uri uri) {
        customUri = uri;
        notifyDataSetChanged();
    }

    public int getMode() {
        return mode;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(ItemAvatarOptionBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        ItemAvatarOptionBinding b = h.b;
        b.ivSelectedRing.setVisibility(position == selectedPos ? View.VISIBLE : View.GONE);
        b.ivAdd.setVisibility(View.GONE);
        b.ivFrameOverlay.setVisibility(View.GONE);
        b.tvOptionLabel.setVisibility(View.GONE);
        b.ivOption.setBackground(null);

        if (mode == MODE_AVATAR) {
            if (position == 0) {
                // Ô ADD: ưu tiên ảnh vừa chọn -> ảnh URL hiện có -> dấu cộng.
                if (customUri != null) {
                    Glide.with(b.ivOption).load(customUri).centerCrop().into(b.ivOption);
                } else if (customUrl != null && customUrl.startsWith("http")) {
                    Glide.with(b.ivOption).load(customUrl).centerCrop().into(b.ivOption);
                } else {
                    b.ivOption.setImageDrawable(null);
                    b.ivOption.setBackgroundResource(com.pompom.group6.R.drawable.bg_avatar_add);
                    b.ivAdd.setVisibility(View.VISIBLE);
                }
            } else {
                b.ivOption.setImageResource(AvatarUtils.DEFAULT_AVATARS[position - 1]);
            }
        } else {
            String token = frameTokens.get(position);
            // Avatar mẫu để xem trước khung.
            if (customUri != null) {
                Glide.with(b.ivOption).load(customUri).centerCrop().into(b.ivOption);
            } else {
                AvatarUtils.loadAvatar(b.ivOption.getContext(), sampleAvatar, b.ivOption);
            }
            int frameRes = AvatarUtils.frameRes(token);
            if (frameRes != 0) {
                b.ivFrameOverlay.setVisibility(View.VISIBLE);
                b.ivFrameOverlay.setImageResource(frameRes);
            }
            b.tvOptionLabel.setVisibility(View.VISIBLE);
            b.tvOptionLabel.setText(frameLabel(token));
        }

        b.getRoot().setOnClickListener(v -> listener.onCell(h.getAdapterPosition()));
    }

    private String frameLabel(String token) {
        switch (token) {
            case "classic": return "Cổ điển";
            case "gold": return "Vàng";
            case "pink": return "Hồng";
            case "mint": return "Ngọc";
            case "none": return "Không khung";
            default: return "";
        }
    }

    @Override
    public int getItemCount() {
        return mode == MODE_AVATAR ? 1 + AvatarUtils.DEFAULT_AVATARS.length : frameTokens.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final ItemAvatarOptionBinding b;
        VH(ItemAvatarOptionBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }
}
