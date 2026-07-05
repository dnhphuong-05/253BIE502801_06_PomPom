package com.pompom.group6.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.gson.JsonElement;
import com.pompom.group6.R;
import com.pompom.group6.databinding.FragmentPremiumProfileBinding;
import com.pompom.group6.databinding.ItemProfileMenuBinding;
import com.pompom.group6.network.ApiClient;
import com.pompom.group6.network.Session;
import com.pompom.group6.network.dto.ApiUser;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PremiumProfileFragment extends Fragment {

    private FragmentPremiumProfileBinding binding;
    private String currentUserOid;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentPremiumProfileBinding.inflate(inflater, container, false);
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
        // Lấy id chuỗi (ObjectId) của user đã đăng nhập qua backend.
        currentUserOid = Session.getUserOid(requireContext());
        if (currentUserOid == null) {
            // Chưa đăng nhập qua backend — không có gì để tải.
            return;
        }

        // 1) Hồ sơ: tên, hạng thành viên, điểm, voucher, bio, avatar — LẤY TỪ MONGODB.
        ApiClient.get().getUser(currentUserOid).enqueue(new Callback<ApiUser>() {
            @Override
            public void onResponse(Call<ApiUser> call, Response<ApiUser> resp) {
                if (binding == null || !resp.isSuccessful() || resp.body() == null) return;
                ApiUser u = resp.body();
                binding.tvUserName.setText(lastTwoWords(u.fullName));
                binding.tvMemberLevel.setText(u.membershipLevel);
                binding.tvBio.setText(u.bio != null && !u.bio.isEmpty() ? u.bio : "Beauty lover 💖");

                if (u.avatarUrl != null && !u.avatarUrl.isEmpty()) {
                    Glide.with(PremiumProfileFragment.this).load(u.avatarUrl)
                            .placeholder(R.drawable.ic_avatar).into(binding.ivUserAvatar);
                } else {
                    binding.ivUserAvatar.setImageResource(R.drawable.ic_avatar);
                }

                updateMenuValue(binding.menuMyPoints.getRoot(),
                        String.format(java.util.Locale.getDefault(), "%,d điểm", u.points));
                updateMenuValue(binding.menuVouchers.getRoot(), u.voucherCount + " voucher");
            }

            @Override
            public void onFailure(Call<ApiUser> call, Throwable t) { /* giữ giá trị mặc định trên UI */ }
        });

        // 2) Số địa chỉ.
        ApiClient.get().getAddresses(currentUserOid).enqueue(new Callback<List<com.pompom.group6.network.dto.ApiAddress>>() {
            @Override
            public void onResponse(Call<List<com.pompom.group6.network.dto.ApiAddress>> call, Response<List<com.pompom.group6.network.dto.ApiAddress>> resp) {
                if (binding == null || !resp.isSuccessful() || resp.body() == null) return;
                int n = resp.body().size();
                updateMenuValue(binding.menuAddressBook.getRoot(), n > 0 ? n + " địa chỉ" : "Chưa có");
            }
            @Override public void onFailure(Call<List<com.pompom.group6.network.dto.ApiAddress>> call, Throwable t) {}
        });

        // 3) Số sản phẩm yêu thích.
        ApiClient.get().getWishlist(currentUserOid).enqueue(new Callback<List<com.pompom.group6.network.dto.ApiProduct>>() {
            @Override
            public void onResponse(Call<List<com.pompom.group6.network.dto.ApiProduct>> call, Response<List<com.pompom.group6.network.dto.ApiProduct>> resp) {
                if (binding == null || !resp.isSuccessful() || resp.body() == null) return;
                int n = resp.body().size();
                updateMenuValue(binding.menuWishlist.getRoot(), n > 0 ? n + " sản phẩm" : "Trống");
            }
            @Override public void onFailure(Call<List<com.pompom.group6.network.dto.ApiProduct>> call, Throwable t) {}
        });

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

    /** Show order-count badges on the status shortcuts (lấy từ MongoDB). */
    private void refreshOrderBadges() {
        if (currentUserOid == null) return;
        ApiClient.get().getOrderCounts(currentUserOid).enqueue(new Callback<Map<String, Integer>>() {
            @Override
            public void onResponse(Call<Map<String, Integer>> call, Response<Map<String, Integer>> resp) {
                if (binding == null || !resp.isSuccessful() || resp.body() == null) return;
                Map<String, Integer> counts = resp.body();
                setBadge(binding.statusPending.tvBadge, count(counts, "pending"));
                setBadge(binding.statusPacking.tvBadge, count(counts, "confirmed") + count(counts, "processing"));
                setBadge(binding.statusShipping.tvBadge, count(counts, "shipping"));
                setBadge(binding.statusDelivered.tvBadge, count(counts, "delivered"));
                setBadge(binding.statusCompleted.tvBadge, count(counts, "completed"));
            }
            @Override public void onFailure(Call<Map<String, Integer>> call, Throwable t) {}
        });
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

    /** Returns only the last two words of a full name (e.g. "Nguyễn Thảo Nguyên" -> "Thảo Nguyên"). */
    private String lastTwoWords(String fullName) {
        if (fullName == null) {
            return "";
        }
        String trimmed = fullName.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        String[] parts = trimmed.split("\\s+");
        if (parts.length <= 2) {
            return trimmed;
        }
        return parts[parts.length - 2] + " " + parts[parts.length - 1];
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
