package com.pompom.group6.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.pompom.group6.R;
import com.pompom.group6.database.UserDAO;
import com.pompom.group6.databinding.FragmentPremiumProfileBinding;
import com.pompom.group6.databinding.ItemProfileMenuBinding;
import com.pompom.group6.models.User;

public class PremiumProfileFragment extends Fragment {

    private FragmentPremiumProfileBinding binding;
    private UserDAO userDAO;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPremiumProfileBinding.inflate(inflater, container, false);
        userDAO = new UserDAO(requireContext());
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Handle status bar padding dynamically for "overflow" look
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(binding.profileHeader, (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top + (int)(16 * getResources().getDisplayMetrics().density), 
                    v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        setupMenuItems();
        loadUserData();
        setupLogout();
    }

    private void setupLogout() {
        binding.btnLogout.setOnClickListener(v -> {
            // Clear login state
            android.content.SharedPreferences prefs = requireContext().getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE);
            prefs.edit().clear().apply();

            // Refresh MainActivity to switch fragments
            if (getActivity() != null) {
                getActivity().recreate();
            }
            
            android.widget.Toast.makeText(getContext(), "Đã đăng xuất", android.widget.Toast.LENGTH_SHORT).show();
        });
    }

    private void loadUserData() {
        // Fetch current user from prefs or default 1
        android.content.SharedPreferences prefs = requireContext().getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", 1);
        
        User user = userDAO.getUserById(userId);
        if (user != null) {
            binding.tvUserName.setText(user.getFullName());
            binding.tvMemberLevel.setText(user.getMembershipLevel());
            binding.tvBio.setText(user.getBio() != null && !user.getBio().isEmpty() ? 
                    user.getBio() : "Beauty lover 💖");
            
            if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                Glide.with(this).load(user.getAvatarUrl()).placeholder(R.drawable.ic_avatar).into(binding.ivUserAvatar);
            }

            // Update Menu Values
            updateMenuValue(binding.menuMyPoints.getRoot(), String.format(java.util.Locale.getDefault(), "%,d điểm", user.getPoints()));
            updateMenuValue(binding.menuVouchers.getRoot(), user.getVoucherCount() + " voucher");
        }
    }

    private void setupMenuItems() {
        setupMenuRow(binding.menuAccountInfo.getRoot(), "Thông tin tài khoản");
        setupMenuRow(binding.menuAddressBook.getRoot(), "Sổ địa chỉ");
        setupMenuRow(binding.menuPayment.getRoot(), "Thanh toán");
        setupMenuRow(binding.menuMyPoints.getRoot(), "Điểm của tôi");
        setupMenuRow(binding.menuVouchers.getRoot(), "Voucher của tôi");
        setupMenuRow(binding.menuWishlist.getRoot(), "Yêu thích");
    }

    private void setupMenuRow(View root, String title) {
        ItemProfileMenuBinding itemBinding = ItemProfileMenuBinding.bind(root);
        itemBinding.tvMenuTitle.setText(title);
        itemBinding.ivMenuIcon.setImageResource(R.drawable.ic_pin);
        itemBinding.ivArrow.setImageResource(R.drawable.ic_pin);
    }

    private void updateMenuValue(View root, String value) {
        ItemProfileMenuBinding itemBinding = ItemProfileMenuBinding.bind(root);
        itemBinding.tvMenuValue.setVisibility(View.VISIBLE);
        itemBinding.tvMenuValue.setText(value);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
