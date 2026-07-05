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
import com.pompom.group6.utils.StatusBarUtils;

public class ProfileActivity extends SwipeBackActivity {

    private ActivityProfileBinding binding;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Transparent status bar: this screen's background shows through.
        StatusBarUtils.applyTransparent(this);

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
        String userOid = com.pompom.group6.network.Session.getUserOid(this);
        if (userOid == null) return;
        com.pompom.group6.network.ApiClient.get().getUser(userOid)
                .enqueue(new retrofit2.Callback<com.pompom.group6.network.dto.ApiUser>() {
                    @Override
                    public void onResponse(retrofit2.Call<com.pompom.group6.network.dto.ApiUser> call,
                                           retrofit2.Response<com.pompom.group6.network.dto.ApiUser> resp) {
                        if (binding == null || !resp.isSuccessful() || resp.body() == null) return;
                        com.pompom.group6.network.dto.ApiUser user = resp.body();
                        binding.tvUserName.setText(user.fullName);
                        binding.tvMemberLevel.setText(user.membershipLevel);
                        binding.tvBio.setText(user.bio != null && !user.bio.isEmpty() ?
                                user.bio : "Beauty lover 💖\nSkincare • Makeup • AI Beauty");
                        if (user.avatarUrl != null && !user.avatarUrl.isEmpty()) {
                            Glide.with(ProfileActivity.this).load(user.avatarUrl).placeholder(R.drawable.ic_avatar).into(binding.ivUserAvatar);
                        }
                        ItemMenuMeBinding(binding.menuMyPoints.getRoot())
                                .tvMenuValue.setText(String.format(java.util.Locale.getDefault(), "%,d điểm", user.points));
                        ItemMenuMeBinding(binding.menuMyPoints.getRoot()).tvMenuValue.setVisibility(View.VISIBLE);
                        ItemMenuMeBinding(binding.menuVouchers.getRoot())
                                .tvMenuValue.setText(String.format(java.util.Locale.getDefault(), "%d voucher", user.voucherCount));
                        ItemMenuMeBinding(binding.menuVouchers.getRoot()).tvMenuValue.setVisibility(View.VISIBLE);
                    }
                    @Override public void onFailure(retrofit2.Call<com.pompom.group6.network.dto.ApiUser> call, Throwable t) {}
                });
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
