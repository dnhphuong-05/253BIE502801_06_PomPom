package com.pompom.group6.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.pompom.group6.R;
import com.pompom.group6.activities.AccountInfoActivity;
import com.pompom.group6.activities.AddressBookActivity;
import com.pompom.group6.activities.ComingSoonActivity;
import com.pompom.group6.activities.ConsultationRequestActivity;
import com.pompom.group6.activities.NotificationActivity;
import com.pompom.group6.activities.OrdersActivity;
import com.pompom.group6.activities.PointsActivity;
import com.pompom.group6.activities.SettingsActivity;
import com.pompom.group6.activities.VouchersActivity;
import com.pompom.group6.activities.WishlistActivity;
import com.pompom.group6.databinding.FragmentPremiumProfileBinding;
import com.pompom.group6.databinding.ItemOrderStatusDemoBinding;
import com.pompom.group6.databinding.ItemProfileFeatureBinding;
import com.pompom.group6.databinding.ItemProfileMenuBinding;
import com.pompom.group6.network.ApiClient;
import com.pompom.group6.network.Session;
import com.pompom.group6.network.dto.ApiUser;
import com.pompom.group6.utils.ProfileFormat;

import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Màn Profile: mọi dữ liệu đọc TRỰC TIẾP từ backend (MongoDB) qua {@link ApiUser}.
 * Không hardcode/mock: trường trống -> hiển thị empty state ("Thêm thông tin"/"Chưa có").
 * Ba trạng thái: loading (skeleton) / có dữ liệu / lỗi (nút thử lại).
 */
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

        com.pompom.group6.utils.BottomNavScrollHelper.attach(binding.nestedScrollView, this);

        // Status bar liền màu header: đẩy header xuống dưới status bar (Bước 1).
        ViewCompat.setOnApplyWindowInsetsListener(binding.profileHeader, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            int extra = (int) (16 * getResources().getDisplayMetrics().density);
            v.setPadding(v.getPaddingLeft(), bars.top + extra, v.getPaddingRight(), v.getPaddingBottom());
            return insets;
        });

        setupStaticActions();
        loadProfile();
    }

    // ------------------------------------------------------------------ actions

    private void setupStaticActions() {
        binding.btnEditProfile.setOnClickListener(v -> open(AccountInfoActivity.class));
        binding.btnSettings.setOnClickListener(v -> open(SettingsActivity.class));
        binding.btnLogout.setOnClickListener(v -> confirmLogout());
        binding.btnRetry.setOnClickListener(v -> loadProfile());
        binding.cardMyOrders.setOnClickListener(v -> open(OrdersActivity.class));

        // Bước 4 — icon trạng thái đơn hàng.
        bindOrderStatus(binding.statusPending, R.drawable.ic_time, "Chờ xác nhận");
        bindOrderStatus(binding.statusPacking, R.drawable.ic_packing, "Chờ lấy hàng");
        bindOrderStatus(binding.statusShipping, R.drawable.ic_fast_delivery, "Đang giao");
        bindOrderStatus(binding.statusDelivered, R.drawable.ic_bag, "Đã giao");
        bindOrderStatus(binding.statusReturn, R.drawable.ic_history, "Trả hàng");
    }

    // ------------------------------------------------------------------ loading

    private void showState(boolean loading, boolean error) {
        if (binding == null) return;
        binding.skeletonView.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.errorView.setVisibility(error ? View.VISIBLE : View.GONE);
        binding.contentView.setVisibility(!loading && !error ? View.VISIBLE : View.GONE);
    }

    private void loadProfile() {
        currentUserOid = Session.getUserOid(requireContext());
        if (currentUserOid == null) {
            showState(false, true);
            binding.tvErrorMessage.setText("Bạn cần đăng nhập để xem hồ sơ.");
            return;
        }
        showState(true, false);

        // Một request duy nhất trả về toàn bộ hồ sơ + số liệu thật (toUserDto).
        ApiClient.get().getUser(currentUserOid).enqueue(new Callback<ApiUser>() {
            @Override
            public void onResponse(Call<ApiUser> call, Response<ApiUser> resp) {
                if (binding == null) return;
                if (!resp.isSuccessful() || resp.body() == null) {
                    showState(false, true);
                    binding.tvErrorMessage.setText("Không tải được hồ sơ. Vui lòng thử lại.");
                    return;
                }
                bindProfile(resp.body());
                showState(false, false);
            }

            @Override
            public void onFailure(Call<ApiUser> call, Throwable t) {
                if (binding == null) return;
                showState(false, true);
                binding.tvErrorMessage.setText("Lỗi kết nối. Kiểm tra mạng và thử lại.");
            }
        });

        refreshOrderBadges();
    }

    // ------------------------------------------------------------------ binding

    private void bindProfile(ApiUser u) {
        // Header (Bước 2).
        binding.tvUserName.setText(lastTwoWords(u.fullName));
        binding.tvMemberLevel.setText(orDefault(u.membershipLevel, "Thành viên"));
        String bio = ProfileFormat.orNull(u.bio);
        binding.tvBio.setVisibility(bio != null ? View.VISIBLE : View.GONE);
        if (bio != null) binding.tvBio.setText(bio);
        if (ProfileFormat.orNull(u.avatarUrl) != null) {
            Glide.with(this).load(u.avatarUrl).placeholder(R.drawable.ic_avatar).into(binding.ivUserAvatar);
        } else {
            binding.ivUserAvatar.setImageResource(R.drawable.ic_avatar);
        }

        // Stats (Bước 2).
        binding.tvStatFollowers.setText(String.valueOf(u.followersCount));
        binding.tvStatFollowing.setText(String.valueOf(u.followingCount));
        binding.tvStatStories.setText(String.valueOf(u.storyCount));

        // Hồ sơ làn da (Bước 3) — trống thì "Thêm thông tin", không bịa.
        bindInfoRow(binding.rowSkinType, R.drawable.ic_droplet, "Loại da",
                ProfileFormat.skinTypeLabel(u.skinType), v -> open(AccountInfoActivity.class));
        bindInfoRow(binding.rowSkinConcern, R.drawable.ic_steth, "Vấn đề da quan tâm",
                ProfileFormat.orNull(u.skinConcerns), v -> open(AccountInfoActivity.class));
        bindInfoRow(binding.rowSkinTone, R.drawable.ic_makeup_brush, "Tông da",
                ProfileFormat.orNull(u.skinTone), v -> open(AccountInfoActivity.class));
        bindInfoRow(binding.rowAvoid, R.drawable.ic_report, "Thành phần cần tránh",
                ProfileFormat.orNull(u.avoidIngredients), v -> open(AccountInfoActivity.class));
        bindInfoRow(binding.rowBirthday, R.drawable.ic_cake, "Ngày sinh",
                ProfileFormat.birthDate(u.birthDate), v -> open(AccountInfoActivity.class));

        // Lưới tiện ích (Bước 5).
        bindFeature(binding.featWishlist, R.drawable.ic_heart, "Yêu thích", u.wishlistCount,
                v -> open(WishlistActivity.class));
        bindFeature(binding.featVoucher, R.drawable.ic_gift, "Voucher", u.voucherCount,
                v -> open(VouchersActivity.class));
        bindFeature(binding.featPoints, R.drawable.ic_loyalty, "Xu tích lũy", u.points,
                v -> open(PointsActivity.class));
        bindFeature(binding.featAddress, R.drawable.ic_pin, "Địa chỉ", u.addressCount,
                v -> open(AddressBookActivity.class));
        // "Đã xem gần đây" chưa có nguồn dữ liệu thật -> không hiện số (empty state).
        bindFeature(binding.featRecent, R.drawable.ic_view, "Đã xem", -1,
                v -> ComingSoonActivity.start(requireContext(), "Đã xem gần đây"));
        bindFeature(binding.featReviews, R.drawable.ic_star, "Đánh giá", u.reviewCount,
                v -> ComingSoonActivity.start(requireContext(), "Đánh giá của tôi"));

        // Hoạt động cộng đồng (Bước 6).
        bindInfoRow(binding.rowStory, R.drawable.ic_images, "Story đã đăng",
                countLabel(u.storyCount, "story"), v -> ComingSoonActivity.start(requireContext(), "Story đã đăng"));
        bindInfoRow(binding.rowReviews, R.drawable.ic_comment, "Bài đánh giá đã đăng",
                countLabel(u.reviewCount, "đánh giá"), v -> ComingSoonActivity.start(requireContext(), "Đánh giá của tôi"));
        bindInfoRow(binding.rowSaved, R.drawable.ic_bookmark, "Nội dung đã lưu",
                countLabel(u.savedCount, "mục"), v -> ComingSoonActivity.start(requireContext(), "Nội dung đã lưu"));
        bindInfoRow(binding.rowConsultation, R.drawable.ic_steth, "Lịch sử tư vấn",
                countLabel(u.consultationCount, "lượt"), v -> open(ConsultationRequestActivity.class));

        // Cài đặt & hỗ trợ (Bước 7).
        bindInfoRow(binding.rowNotifications, R.drawable.ic_notification, "Thông báo",
                null, v -> open(NotificationActivity.class));
        bindInfoRow(binding.rowSupport, R.drawable.ic_support, "Liên hệ tư vấn / CSKH",
                null, v -> open(ConsultationRequestActivity.class));
        bindInfoRow(binding.rowPolicy, R.drawable.ic_report, "Chính sách & trợ giúp",
                null, v -> ComingSoonActivity.start(requireContext(), "Chính sách & trợ giúp"));
        bindInfoRow(binding.rowLanguage, R.drawable.ic_language, "Ngôn ngữ",
                null, v -> ComingSoonActivity.start(requireContext(), "Ngôn ngữ"));
    }

    /** Hàng thông tin (icon + tiêu đề + giá trị/gợi ý). value==null -> "Thêm thông tin" (pink). */
    private void bindInfoRow(ItemProfileMenuBinding row, int iconRes, String title,
                             String value, View.OnClickListener onClick) {
        row.ivMenuIcon.setImageResource(iconRes);
        row.ivMenuIcon.setImageTintList(pink());
        row.tvMenuTitle.setText(title);

        row.tvMenuValue.setVisibility(View.VISIBLE);
        if (value != null) {
            row.tvMenuValue.setText(value);
            row.tvMenuValue.setTextColor(color(R.color.text_secondary));
        } else {
            row.tvMenuValue.setText(ProfileFormat.HINT_ADD);
            row.tvMenuValue.setTextColor(color(R.color.brand_pink));
        }

        row.ivArrow.setImageResource(R.drawable.ic_left_chevron);
        row.ivArrow.setRotation(180f);
        row.ivArrow.setImageTintList(colorList(R.color.text_secondary));
        row.getRoot().setOnClickListener(onClick);
    }

    /** Ô lưới tiện ích. count<0 -> ẩn badge (chưa có dữ liệu); count>0 -> hiện số thật. */
    private void bindFeature(ItemProfileFeatureBinding feat, int iconRes, String label,
                             int count, View.OnClickListener onClick) {
        feat.ivFeatureIcon.setImageResource(iconRes);
        feat.tvFeatureLabel.setText(label);
        if (count > 0) {
            feat.tvFeatureBadge.setVisibility(View.VISIBLE);
            feat.tvFeatureBadge.setText(count > 99 ? "99+" : String.valueOf(count));
        } else {
            feat.tvFeatureBadge.setVisibility(View.GONE);
        }
        feat.getRoot().setOnClickListener(onClick);
    }

    // ------------------------------------------------------------- order badges

    private void refreshOrderBadges() {
        if (currentUserOid == null) return;
        ApiClient.get().getOrderCounts(currentUserOid).enqueue(new Callback<Map<String, Integer>>() {
            @Override
            public void onResponse(Call<Map<String, Integer>> call, Response<Map<String, Integer>> resp) {
                if (binding == null || !resp.isSuccessful() || resp.body() == null) return;
                Map<String, Integer> c = resp.body();
                setBadge(binding.statusPending.tvBadge, count(c, "pending"));
                setBadge(binding.statusPacking.tvBadge, count(c, "confirmed") + count(c, "processing"));
                setBadge(binding.statusShipping.tvBadge, count(c, "shipping"));
                setBadge(binding.statusDelivered.tvBadge, count(c, "delivered") + count(c, "completed"));
                setBadge(binding.statusReturn.tvBadge, count(c, "returned") + count(c, "refunded"));
            }
            @Override public void onFailure(Call<Map<String, Integer>> call, Throwable t) { /* badge non-critical */ }
        });
    }

    private void bindOrderStatus(ItemOrderStatusDemoBinding item, int iconRes, String label) {
        item.ivStatusIcon.setImageResource(iconRes);
        item.tvLabel.setText(label);
        item.getRoot().setOnClickListener(v -> open(OrdersActivity.class));
    }

    private void setBadge(TextView badge, int value) {
        if (value > 0) {
            badge.setVisibility(View.VISIBLE);
            badge.setText(value > 99 ? "99+" : String.valueOf(value));
        } else {
            badge.setVisibility(View.GONE);
        }
    }

    // ------------------------------------------------------------------ helpers

    private void confirmLogout() {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc muốn đăng xuất khỏi tài khoản?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    Session.logout(requireContext());
                    android.widget.Toast.makeText(getContext(), "Đã đăng xuất", android.widget.Toast.LENGTH_SHORT).show();
                    if (getActivity() != null) getActivity().recreate();
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void open(Class<?> activity) {
        startActivity(new Intent(requireContext(), activity));
    }

    private int count(Map<String, Integer> counts, String key) {
        Integer v = counts.get(key);
        return v == null ? 0 : v;
    }

    /** "3 story", hoặc "Chưa có" khi bằng 0 (empty state, không bịa). */
    private String countLabel(int n, String unit) {
        return n > 0 ? n + " " + unit : "Chưa có";
    }

    private int color(int res) {
        return ContextCompat.getColor(requireContext(), res);
    }

    private android.content.res.ColorStateList colorList(int res) {
        return android.content.res.ColorStateList.valueOf(color(res));
    }

    private android.content.res.ColorStateList pink() {
        return colorList(R.color.brand_pink);
    }

    private String orDefault(String s, String def) {
        return ProfileFormat.orNull(s) != null ? s : def;
    }

    /** Chỉ lấy 2 từ cuối của họ tên (vd "Nguyễn Thảo Nguyên" -> "Thảo Nguyên"). */
    private String lastTwoWords(String fullName) {
        if (fullName == null) return "";
        String t = fullName.trim();
        if (t.isEmpty()) return "";
        String[] parts = t.split("\\s+");
        if (parts.length <= 2) return t;
        return parts[parts.length - 2] + " " + parts[parts.length - 1];
    }

    @Override
    public void onResume() {
        super.onResume();
        // Làm mới khi quay lại từ các màn Account/Address/Wishlist...
        if (binding != null && currentUserOid != null) loadProfile();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
