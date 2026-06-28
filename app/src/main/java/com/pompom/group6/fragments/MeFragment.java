package com.pompom.group6.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.pompom.group6.R;
import android.content.Context;
import android.content.SharedPreferences;
import com.pompom.group6.database.UserDAO;
import com.pompom.group6.models.User;
import com.pompom.group6.databinding.ItemMenuMeBinding;
import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pompom.group6.activities.LoginActivity;
import com.pompom.group6.activities.RegisterActivity;
import com.pompom.group6.databinding.FragmentMeBinding;

public class MeFragment extends Fragment {

    private FragmentMeBinding binding;
    private UserDAO userDAO;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMeBinding.inflate(inflater, container, false);
        userDAO = new UserDAO(requireContext());
        return binding.getRoot();
    }

//    @Override
//    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
//        super.onViewCreated(view, savedInstanceState);
//        setupListeners();
//        setupMenuItems();
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        checkLoginStatus();
//    }

//    private void checkLoginStatus() {
//        if (binding == null) return;
//
//        Context context = getContext();
//        if (context == null) return;
//
//        SharedPreferences prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
//        boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);
//        int userId = prefs.getInt("user_id", -1);
//
//        if (isLoggedIn && userId != -1) {
//            binding.layoutLoggedOut.setVisibility(View.GONE);
//            binding.layoutLoggedIn.setVisibility(View.VISIBLE);
//            loadUserData(userId);
//        } else {
//            binding.layoutLoggedOut.setVisibility(View.VISIBLE);
//            binding.layoutLoggedIn.setVisibility(View.GONE);
//        }
//    }
//
//    private void loadUserData(int userId) {
//        try {
//            User user = userDAO.getUserById(userId);
//            if (user != null && binding != null) {
//                binding.tvUserName.setText(user.getFullName());
//                binding.tvMemberLevel.setText(user.getMembershipLevel());
//                binding.tvPoints.setText(String.format("%,d điểm", user.getPoints()));
//                binding.tvVoucherCount.setText(String.format("%d Voucher", user.getVoucherCount()));
//
//                if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
//                    Glide.with(this).load(user.getAvatarUrl()).into(binding.ivUserAvatar);
//                }
//            }
//        } catch (Exception e) {
//            android.util.Log.e("MeFragment", "Error loading user data: " + e.getMessage());
//        }
//    }
//
//    private void setupMenuItems() {
//        setupMenuRow(binding.menuAccountInfo.getRoot(), R.drawable.ic_user, "Thông tin tài khoản");
//        setupMenuRow(binding.menuAddressBook.getRoot(), R.drawable.ic_bookmark, "Sổ địa chỉ");
//        setupMenuRow(binding.menuPayment.getRoot(), R.drawable.ic_bag, "Thanh toán");
//        setupMenuRow(binding.menuMyPoints.getRoot(), R.drawable.ic_rank, "Điểm của tôi");
//        setupMenuRow(binding.menuWishlist.getRoot(), R.drawable.ic_heart, "Yêu thích");
//        setupMenuRow(binding.menuRecentlyViewed.getRoot(), R.drawable.ic_history, "Đã xem gần đây");
//        setupMenuRow(binding.menuSettings.getRoot(), R.drawable.ic_settings, "Cài đặt");
//    }

    private void setupMenuRow(View root, int iconRes, String title) {
        ItemMenuMeBinding itemBinding = ItemMenuMeBinding.bind(root);
        itemBinding.ivMenuIcon.setImageResource(iconRes);
        itemBinding.tvMenuTitle.setText(title);
    }

    private void setupListeners() {
        binding.btnLogin.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
        });
        binding.btnRegister.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), RegisterActivity.class);
            startActivity(intent);
        });

//        binding.btnLogout.setOnClickListener(v -> {
//            SharedPreferences prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
//            prefs.edit().clear().apply();
//            checkLoginStatus();
//            Toast.makeText(getContext(), "Đã đăng xuất", Toast.LENGTH_SHORT).show();
//        });
        
        // ... (other social buttons if needed)
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
