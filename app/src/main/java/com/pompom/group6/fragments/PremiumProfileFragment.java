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
import com.pompom.group6.activities.MyReviewsActivity;
import com.pompom.group6.activities.MyStoriesActivity;
import com.pompom.group6.activities.LanguageActivity;
import com.pompom.group6.activities.NotificationActivity;
import com.pompom.group6.activities.PolicyHelpActivity;
import com.pompom.group6.activities.OrdersActivity;
import com.pompom.group6.activities.PointsActivity;
import com.pompom.group6.activities.SavedPostsActivity;
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
import com.pompom.group6.network.dto.UserUpdateRequest;
import com.pompom.group6.utils.AvatarUtils;
import com.pompom.group6.utils.ProfileFormat;
import com.pompom.group6.utils.SkinData;
import com.pompom.group6.utils.SkinProfileUi;

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
    /** Chỉ hiện skeleton ở lần tải đầu; các lần sau refresh nền, giữ nội dung (không nháy). */
    private boolean firstLoad = true;
    /** Avatar + khung hiện tại (để mở popup chỉnh sửa). */
    private String currentAvatar;
    private String currentFrame;

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
        binding.avatarContainer.setOnClickListener(v -> openAvatarEditor());

        binding.swipeRefresh.setColorSchemeColors(color(R.color.brand_pink));
        binding.swipeRefresh.setOnRefreshListener(this::loadProfile);

        // Bước 4 — icon trạng thái đơn hàng (bấm mở danh sách ĐÃ lọc đúng trạng thái).
        bindOrderStatus(binding.statusPending, R.drawable.ic_time, "Chờ xác nhận", "pending");
        bindOrderStatus(binding.statusPacking, R.drawable.ic_packing, "Chờ lấy hàng", "packing");
        bindOrderStatus(binding.statusShipping, R.drawable.ic_fast_delivery, "Đang giao", "shipping");
        bindOrderStatus(binding.statusDelivered, R.drawable.ic_bag, "Đã giao", "delivered");
        bindOrderStatus(binding.statusReturn, R.drawable.ic_history, "Trả hàng", "return");
        // Trả hàng dùng badge đỏ để tách biệt với các trạng thái đơn thành công (badge xanh).
        binding.statusReturn.tvBadge.setBackgroundResource(R.drawable.bg_circle_red);
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
            binding.swipeRefresh.setRefreshing(false);
            showState(false, true);
            binding.tvErrorMessage.setText("Bạn cần đăng nhập để xem hồ sơ.");
            return;
        }
        // Lần đầu -> skeleton; refresh (kéo/quay lại) -> giữ nội dung, không nháy.
        if (firstLoad) showState(true, false);

        // Một request duy nhất trả về toàn bộ hồ sơ + số liệu thật (toUserDto).
        ApiClient.get().getUser(currentUserOid).enqueue(new Callback<ApiUser>() {
            @Override
            public void onResponse(Call<ApiUser> call, Response<ApiUser> resp) {
                if (binding == null) return;
                binding.swipeRefresh.setRefreshing(false);
                if (!resp.isSuccessful() || resp.body() == null) {
                    if (firstLoad) {
                        showState(false, true);
                        binding.tvErrorMessage.setText("Không tải được hồ sơ. Vui lòng thử lại.");
                    }
                    return;
                }
                bindProfile(resp.body());
                firstLoad = false;
                showState(false, false);
            }

            @Override
            public void onFailure(Call<ApiUser> call, Throwable t) {
                if (binding == null) return;
                binding.swipeRefresh.setRefreshing(false);
                if (firstLoad) {
                    showState(false, true);
                    binding.tvErrorMessage.setText("Lỗi kết nối. Kiểm tra mạng và thử lại.");
                }
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

        // Ảnh đại diện + khung (Bước 2) — resolve URL / avatar mặc định / khung.
        currentAvatar = u.avatarUrl;
        currentFrame = u.avatarFrame;
        AvatarUtils.loadAvatar(requireContext(), u.avatarUrl, binding.ivUserAvatar);
        AvatarUtils.applyFrame(binding.ivAvatarFrame, u.avatarFrame);

        // Stats (Bước 2).
        binding.tvStatFollowers.setText(String.valueOf(u.followersCount));
        binding.tvStatFollowing.setText(String.valueOf(u.followingCount));
        binding.tvStatStories.setText(String.valueOf(u.storyCount));

        // Hồ sơ làn da (Bước 3).
        // Điểm 1 — Loại da: bấm chọn từ danh sách -> cập nhật DB.
        bindInfoRow(binding.rowSkinType, R.drawable.ic_droplet, "Loại da",
                SkinData.skinTypeLabel(u.skinType),
                v -> SkinProfileUi.showTypePicker(requireContext(), u.skinType,
                        code -> putSkin(UserUpdateRequest.ofSkinType(code))));
        // Điểm 2 — Vấn đề da: phân tích khách quan từ tương tác Community.
        bindInfoRow(binding.rowSkinConcern, R.drawable.ic_steth, "Vấn đề da quan tâm",
                "Xem phân tích", v -> showConcernAnalysis());
        // Điểm 3 — Tông da: bấm chọn -> cập nhật DB.
        bindInfoRow(binding.rowSkinTone, R.drawable.ic_makeup_brush, "Tông da",
                SkinData.skinToneLabel(u.skinTone),
                v -> SkinProfileUi.showTonePicker(requireContext(), u.skinTone,
                        code -> putSkin(UserUpdateRequest.ofSkinTone(code))));
        // Điểm 4 — Thành phần cần tránh: gợi ý dựa trên loại da.
        final String skinTypeCode = u.skinType;
        bindInfoRow(binding.rowAvoid, R.drawable.ic_report, "Thành phần cần tránh",
                ProfileFormat.orNull(skinTypeCode) != null ? "Xem gợi ý" : "Chọn loại da trước",
                v -> {
                    if (ProfileFormat.orNull(skinTypeCode) == null) {
                        toastMsg("Hãy chọn Loại da trước để nhận gợi ý");
                    } else {
                        SkinProfileUi.showAvoidInfo(requireContext(), skinTypeCode);
                    }
                });
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
                v -> open(MyReviewsActivity.class));

        // Hoạt động cộng đồng (Bước 6).
        bindInfoRow(binding.rowStory, R.drawable.ic_images, "Story đã đăng",
                countLabel(u.storyCount, "story"), v -> open(MyStoriesActivity.class));
        bindInfoRow(binding.rowReviews, R.drawable.ic_comment, "Bài đánh giá đã đăng",
                countLabel(u.reviewCount, "đánh giá"), v -> open(MyReviewsActivity.class));
        bindInfoRow(binding.rowSaved, R.drawable.ic_bookmark, "Nội dung đã lưu",
                countLabel(u.savedCount, "mục"), v -> open(SavedPostsActivity.class));
        bindInfoRow(binding.rowConsultation, R.drawable.ic_steth, "Lịch sử tư vấn",
                countLabel(u.consultationCount, "lượt"), v -> open(ConsultationRequestActivity.class));

        // Cài đặt & hỗ trợ (Bước 7).
        bindInfoRow(binding.rowNotifications, R.drawable.ic_notification, "Thông báo",
                null, v -> open(NotificationActivity.class));
        bindInfoRow(binding.rowSupport, R.drawable.ic_support, "Liên hệ tư vấn / CSKH",
                null, v -> open(ConsultationRequestActivity.class));
        bindInfoRow(binding.rowPolicy, R.drawable.ic_report, "Chính sách & trợ giúp",
                null, v -> open(PolicyHelpActivity.class));
        bindInfoRow(binding.rowLanguage, R.drawable.ic_language, "Ngôn ngữ",
                null, v -> open(LanguageActivity.class));
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
            feat.tvFeatureBadge.setText(compact(count));
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

    private void bindOrderStatus(ItemOrderStatusDemoBinding item, int iconRes, String label, String group) {
        item.ivStatusIcon.setImageResource(iconRes);
        item.tvLabel.setText(label);
        item.getRoot().setOnClickListener(v -> {
            Intent i = new Intent(requireContext(), OrdersActivity.class);
            i.putExtra(OrdersActivity.EXTRA_STATUS_GROUP, group);
            startActivity(i);
        });
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
        View content = getLayoutInflater().inflate(R.layout.dialog_logout, null);
        androidx.appcompat.app.AlertDialog dialog =
                new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                        .setView(content).create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        content.findViewById(R.id.btnStay).setOnClickListener(v -> dialog.dismiss());
        content.findViewById(R.id.btnConfirmLogout).setOnClickListener(v -> {
            dialog.dismiss();
            Session.logout(requireContext());
            android.widget.Toast.makeText(getContext(), "Đã đăng xuất", android.widget.Toast.LENGTH_SHORT).show();
            if (getActivity() != null) getActivity().recreate();
        });
        dialog.show();
    }

    private void open(Class<?> activity) {
        startActivity(new Intent(requireContext(), activity));
    }

    private void openAvatarEditor() {
        AvatarEditorBottomSheet sheet = AvatarEditorBottomSheet.newInstance(currentAvatar, currentFrame);
        sheet.setOnSaved(this::loadProfile);
        sheet.show(getChildFragmentManager(), "avatar_editor");
    }

    /** Cập nhật một phần hồ sơ làn da (loại da / tông da) rồi refresh. */
    private void putSkin(UserUpdateRequest body) {
        if (currentUserOid == null) return;
        ApiClient.get().updateUser(currentUserOid, body).enqueue(new Callback<ApiUser>() {
            @Override
            public void onResponse(Call<ApiUser> call, Response<ApiUser> resp) {
                if (binding == null) return;
                if (resp.isSuccessful()) {
                    toastMsg("Đã cập nhật");
                    loadProfile();
                } else {
                    toastMsg("Cập nhật thất bại, thử lại");
                }
            }
            @Override public void onFailure(Call<ApiUser> call, Throwable t) {
                if (binding != null) toastMsg("Lỗi kết nối, thử lại");
            }
        });
    }

    /** Điểm 2 — lấy phân tích vấn đề da rồi hiển thị dialog. */
    private void showConcernAnalysis() {
        if (currentUserOid == null) return;
        toastMsg("Đang phân tích…");
        ApiClient.get().getSkinConcernAnalysis(currentUserOid).enqueue(new Callback<com.pompom.group6.network.dto.ApiSkinAnalysis>() {
            @Override
            public void onResponse(Call<com.pompom.group6.network.dto.ApiSkinAnalysis> call,
                                   Response<com.pompom.group6.network.dto.ApiSkinAnalysis> resp) {
                if (binding == null || !isAdded()) return;
                SkinProfileUi.showConcernAnalysis(requireContext(), resp.isSuccessful() ? resp.body() : null);
            }
            @Override public void onFailure(Call<com.pompom.group6.network.dto.ApiSkinAnalysis> call, Throwable t) {
                if (binding != null) toastMsg("Không tải được phân tích");
            }
        });
    }

    private void toastMsg(String msg) {
        if (getContext() != null) android.widget.Toast.makeText(getContext(), msg, android.widget.Toast.LENGTH_SHORT).show();
    }

    private int count(Map<String, Integer> counts, String key) {
        Integer v = counts.get(key);
        return v == null ? 0 : v;
    }

    /** Rút gọn số lớn cho badge nhỏ: 3250 -> "3.3k" (giữ số thật ở màn chi tiết). */
    private String compact(int n) {
        if (n < 1000) return String.valueOf(n);
        if (n < 1_000_000) return trimZero(n / 1000.0) + "k";
        return trimZero(n / 1_000_000.0) + "M";
    }

    private String trimZero(double v) {
        String s = String.format(Locale.US, "%.1f", v);
        return s.endsWith(".0") ? s.substring(0, s.length() - 2) : s;
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
        // Refresh nền khi quay lại từ Account/Address/Wishlist... Bỏ qua lần onResume đầu
        // (onViewCreated đã tải) để tránh gọi API 2 lần và nháy skeleton.
        if (binding != null && !firstLoad) loadProfile();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
