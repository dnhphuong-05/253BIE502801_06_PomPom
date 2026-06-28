package com.pompom.group6.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.pompom.group6.R;
import com.pompom.group6.database.UserDAO;
import com.pompom.group6.databinding.ActivityProfileBinding;
import com.pompom.group6.databinding.ItemProfileMenuBinding;
import com.pompom.group6.models.User;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Edge-to-edge transparency
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        userDAO = new UserDAO(this);

        // Dynamic padding for status bar
        androidx.core.view.ViewCompat.setOnApplyWindowInsetsListener(binding.profileHeader, (v, insets) -> {
            androidx.core.graphics.Insets systemBars = insets.getInsets(androidx.core.view.WindowInsetsCompat.Type.systemBars());
            v.setPadding(v.getPaddingLeft(), systemBars.top + (int)(20 * getResources().getDisplayMetrics().density), 
                    v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        setupMenuItems();
        loadUserData();
        setupOrderHistory();
    }

    private void loadUserData() {
        // Fetch user with ID 1 for demo
        User user = userDAO.getUserById(1);
        if (user != null) {
            binding.tvUserName.setText(user.getFullName());
            binding.tvMemberLevel.setText(user.getMembershipLevel());
            binding.tvBio.setText(user.getBio() != null && !user.getBio().isEmpty() ? 
                    user.getBio() : "Beauty lover 💖\nSkincare • Makeup • AI Beauty");
            
            if (user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                Glide.with(this).load(user.getAvatarUrl()).placeholder(R.drawable.ic_avatar).into(binding.ivUserAvatar);
            }

            // Points & Vouchers for Menu
            ItemMenuMeBinding(binding.menuMyPoints.getRoot())
                    .tvMenuValue.setText(String.format(java.util.Locale.getDefault(), "%,d điểm", user.getPoints()));
            ItemMenuMeBinding(binding.menuMyPoints.getRoot()).tvMenuValue.setVisibility(View.VISIBLE);

            ItemMenuMeBinding(binding.menuVouchers.getRoot())
                    .tvMenuValue.setText(String.format(java.util.Locale.getDefault(), "%d voucher", user.getVoucherCount()));
            ItemMenuMeBinding(binding.menuVouchers.getRoot()).tvMenuValue.setVisibility(View.VISIBLE);
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

    private ItemProfileMenuBinding ItemMenuMeBinding(View root) {
        return ItemProfileMenuBinding.bind(root);
    }

    private void setupOrderHistory() {
        // Placeholder for RecyclerView initialization
        binding.rvOrderHistory.setVisibility(View.VISIBLE);
    }
}
