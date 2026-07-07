package com.pompom.group6.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pompom.group6.R;
import com.pompom.group6.activities.AiCallActivity;
import com.pompom.group6.activities.AiChatActivity;
import com.pompom.group6.activities.AiHistoryActivity;
import com.pompom.group6.activities.ArTryOnActivity;
import com.pompom.group6.activities.MakeupArtistActivity;
import com.pompom.group6.databinding.FragmentAiHubBinding;

/** AI Beauty Hub — hero card (hình + "Gọi ngay") cho Dermatologist/Makeup Artist, tile giới
 * thiệu gọn cho Client Advisor/AR Try-on, cộng truy cập lịch sử/báo cáo. */
public class AiHubFragment extends Fragment {

    private FragmentAiHubBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAiHubBinding.inflate(inflater, container, false);
        setupListeners();
        com.pompom.group6.utils.BottomNavScrollHelper.attach(binding.nestedScrollView, this);
        return binding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Cập nhật lại avatar phòng khi user đăng nhập/đổi avatar ở tab khác.
        updateUserAvatar();
    }

    private void setupListeners() {
        // MaterialCardView tự áp animator đổi elevation khi nhấn/giữ do card giờ clickable —
        // tắt hẳn để cardElevation="2dp" khai báo trong XML luôn cố định, không "nháy" bóng đổ.
        binding.cardDermatologist.setStateListAnimator(null);
        binding.cardMakeup.setStateListAnimator(null);

        View.OnClickListener openDermatologist = v ->
                startActivity(new Intent(getContext(), AiCallActivity.class));
        binding.cardDermatologist.setOnClickListener(openDermatologist);
        binding.btnCallDermatologist.setOnClickListener(openDermatologist);

        View.OnClickListener openMakeup = v ->
                startActivity(new Intent(getContext(), MakeupArtistActivity.class));
        binding.cardMakeup.setOnClickListener(openMakeup);
        binding.btnCallMakeup.setOnClickListener(openMakeup);

        binding.cardAdvisor.setOnClickListener(v ->
                startActivity(new Intent(getContext(), AiChatActivity.class)));

        binding.cardTryon.setOnClickListener(v ->
                startActivity(new Intent(getContext(), ArTryOnActivity.class)));

        binding.btnHistory.setOnClickListener(v -> AiHistoryActivity.start(requireContext()));

        binding.btnBeautyReport.setOnClickListener(v ->
                com.pompom.group6.activities.BeautyReportActivity.start(requireContext()));

        binding.btnAiSettings.setOnClickListener(v ->
                startActivity(new Intent(getContext(), com.pompom.group6.activities.AiSettingsActivity.class)));
    }

    /** Avatar thật nếu đã đăng nhập (giống Home/Community), icon khách nếu chưa — thay cho
     * huy hiệu "Pro" cố định trước đây. */
    private void updateUserAvatar() {
        if (binding == null) return;
        String userOid = com.pompom.group6.network.Session.getUserOid(requireContext());
        if (userOid == null) {
            binding.ivUserAvatar.setImageResource(R.drawable.ic_user2);
            return;
        }
        com.pompom.group6.network.ApiClient.get().getUser(userOid)
                .enqueue(new retrofit2.Callback<com.pompom.group6.network.dto.ApiUser>() {
                    @Override
                    public void onResponse(retrofit2.Call<com.pompom.group6.network.dto.ApiUser> call,
                                           retrofit2.Response<com.pompom.group6.network.dto.ApiUser> resp) {
                        if (binding == null) return;
                        String avatarUrl = resp.isSuccessful() && resp.body() != null ? resp.body().avatarUrl : null;
                        com.bumptech.glide.Glide.with(AiHubFragment.this)
                                .load(avatarUrl)
                                .placeholder(R.drawable.ic_user2)
                                .error(R.drawable.ic_user2)
                                .into(binding.ivUserAvatar);
                    }
                    @Override
                    public void onFailure(retrofit2.Call<com.pompom.group6.network.dto.ApiUser> call, Throwable t) {}
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
