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
    private int currentUserId = 1;
    private User currentUser;

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
        binding.btnNotifications.setOnClickListener(v ->
                toast("Bạn không có thông báo mới"));

        binding.btnSettings.setOnClickListener(v ->
                startActivity(new android.content.Intent(requireContext(),
                        com.pompom.group6.activities.SettingsActivity.class)));

        binding.btnLogout.setOnClickListener(v -> confirmLogout());
    }

    private void confirmLogout() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc muốn đăng xuất khỏi tài khoản?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    // Clear login state
                    android.content.SharedPreferences prefs = requireContext()
                            .getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE);
                    prefs.edit().clear().apply();

                    toast("Đã đăng xuất");

                    // Refresh MainActivity to switch back to the guest (Me) screen
                    if (getActivity() != null) {
                        getActivity().recreate();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void loadUserData() {
        // Fetch current user from prefs or default 1
        android.content.SharedPreferences prefs = requireContext().getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE);
        currentUserId = prefs.getInt("user_id", 1);

        currentUser = userDAO.getUserById(currentUserId);
        if (currentUser != null) {
            binding.tvUserName.setText(currentUser.getFullName());
            binding.tvMemberLevel.setText(currentUser.getMembershipLevel());
            binding.tvBio.setText(currentUser.getBio() != null && !currentUser.getBio().isEmpty() ?
                    currentUser.getBio() : "Beauty lover 💖");

            if (currentUser.getAvatarUrl() != null && !currentUser.getAvatarUrl().isEmpty()) {
                Glide.with(this).load(currentUser.getAvatarUrl()).placeholder(R.drawable.ic_avatar).into(binding.ivUserAvatar);
            } else {
                binding.ivUserAvatar.setImageResource(R.drawable.ic_avatar);
            }

            // Update Menu Values
            updateMenuValue(binding.menuMyPoints.getRoot(), String.format(java.util.Locale.getDefault(), "%,d điểm", currentUser.getPoints()));
            updateMenuValue(binding.menuVouchers.getRoot(), currentUser.getVoucherCount() + " voucher");
        }

        int addressCount = userDAO.getAddressCount(currentUserId);
        updateMenuValue(binding.menuAddressBook.getRoot(),
                addressCount > 0 ? addressCount + " địa chỉ" : "Chưa có");
        int wishlistCount = userDAO.getWishlistCount(currentUserId);
        updateMenuValue(binding.menuWishlist.getRoot(),
                wishlistCount > 0 ? wishlistCount + " sản phẩm" : "Trống");

        refreshOrderBadges();
    }

    private void setupMenuItems() {
        setupMenuRow(binding.menuAccountInfo.getRoot(), R.drawable.ic_user2, "Thông tin tài khoản",
                v -> open(com.pompom.group6.activities.AccountInfoActivity.class));
        setupMenuRow(binding.menuAddressBook.getRoot(), R.drawable.ic_pin, "Sổ địa chỉ",
                v -> open(com.pompom.group6.activities.AddressBookActivity.class));
        setupMenuRow(binding.menuPayment.getRoot(), R.drawable.ic_credit_card, "Thanh toán",
                v -> open(com.pompom.group6.activities.PaymentActivity.class));
        setupMenuRow(binding.menuMyPoints.getRoot(), R.drawable.ic_loyalty, "Điểm của tôi",
                v -> open(com.pompom.group6.activities.PointsActivity.class));
        setupMenuRow(binding.menuVouchers.getRoot(), R.drawable.ic_gift, "Voucher của tôi",
                v -> open(com.pompom.group6.activities.VouchersActivity.class));
        setupMenuRow(binding.menuWishlist.getRoot(), R.drawable.ic_heart, "Yêu thích",
                v -> open(com.pompom.group6.activities.WishlistActivity.class));

        binding.cardMyOrders.setOnClickListener(
                v -> open(com.pompom.group6.activities.OrdersActivity.class));

        setupOrderStatuses();
    }

    private void setupOrderStatuses() {
        bindOrderStatus(binding.statusPending, R.drawable.ic_time, "Chờ xác nhận");
        bindOrderStatus(binding.statusPacking, R.drawable.ic_packing, "Chờ lấy hàng");
        bindOrderStatus(binding.statusShipping, R.drawable.ic_fast_delivery, "Đang giao");
        bindOrderStatus(binding.statusDelivered, R.drawable.ic_bag, "Đã giao");
        bindOrderStatus(binding.statusCompleted, R.drawable.ic_star, "Hoàn thành");
    }

    private void bindOrderStatus(com.pompom.group6.databinding.ItemOrderStatusDemoBinding item,
                                 int iconRes, String label) {
        item.ivStatusIcon.setImageResource(iconRes);
        item.tvLabel.setText(label);
        item.getRoot().setOnClickListener(
                v -> open(com.pompom.group6.activities.OrdersActivity.class));
    }

    /** Show order-count badges on the status shortcuts. */
    private void refreshOrderBadges() {
        java.util.Map<String, Integer> counts = userDAO.getOrderCounts(currentUserId);
        setBadge(binding.statusPending.tvBadge, count(counts, "pending"));
        setBadge(binding.statusPacking.tvBadge, count(counts, "confirmed") + count(counts, "processing"));
        setBadge(binding.statusShipping.tvBadge, count(counts, "shipping"));
        setBadge(binding.statusDelivered.tvBadge, count(counts, "delivered"));
        setBadge(binding.statusCompleted.tvBadge, count(counts, "completed"));
    }

    private int count(java.util.Map<String, Integer> counts, String key) {
        Integer v = counts.get(key);
        return v == null ? 0 : v;
    }

    private void setBadge(android.widget.TextView badge, int value) {
        if (value > 0) {
            badge.setVisibility(View.VISIBLE);
            badge.setText(String.valueOf(value));
        } else {
            badge.setVisibility(View.GONE);
        }
    }

    private void open(Class<?> activity) {
        startActivity(new android.content.Intent(requireContext(), activity));
    }

    private void setupMenuRow(View root, int iconRes, String title, View.OnClickListener onClick) {
        ItemProfileMenuBinding itemBinding = ItemProfileMenuBinding.bind(root);
        itemBinding.tvMenuTitle.setText(title);

        // Leading icon in brand pink (from nav_text_selector palette)
        itemBinding.ivMenuIcon.setImageResource(iconRes);
        itemBinding.ivMenuIcon.setImageTintList(android.content.res.ColorStateList.valueOf(
                androidx.core.content.ContextCompat.getColor(requireContext(), R.color.brand_pink)));

        // Trailing right-pointing chevron in the neutral (unselected) color
        itemBinding.ivArrow.setImageResource(R.drawable.ic_left_chevron);
        itemBinding.ivArrow.setRotation(180f);
        itemBinding.ivArrow.setImageTintList(android.content.res.ColorStateList.valueOf(
                androidx.core.content.ContextCompat.getColor(requireContext(), R.color.text_secondary)));

        root.setOnClickListener(onClick);
    }

    private void updateMenuValue(View root, String value) {
        ItemProfileMenuBinding itemBinding = ItemProfileMenuBinding.bind(root);
        itemBinding.tvMenuValue.setVisibility(View.VISIBLE);
        itemBinding.tvMenuValue.setText(value);
    }

    private void toast(String message) {
        android.widget.Toast.makeText(getContext(), message, android.widget.Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh values when returning from Account/Address/Wishlist screens
        if (binding != null) {
            loadUserData();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
