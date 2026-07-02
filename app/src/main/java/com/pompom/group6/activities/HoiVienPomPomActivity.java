package com.pompom.group6.activities;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pompom.group6.R;

import com.pompom.group6.adapters.VoucherPromoAdapter;
import com.pompom.group6.database.PromotionDAO;
import com.pompom.group6.database.UserDAO;
import com.pompom.group6.databinding.ActivityHoiVienPomPomBinding;
import com.pompom.group6.models.User;
import com.pompom.group6.models.Voucher;
import com.pompom.group6.utils.StatusBarUtils;

import java.util.List;
import java.util.Locale;
import java.util.Set;

public class HoiVienPomPomActivity extends AppCompatActivity {

    private static final Locale VN = new Locale("vi", "VN");

    private ActivityHoiVienPomPomBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHoiVienPomPomBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Transparent status bar: this screen's background shows through.
        StatusBarUtils.applyTransparent(this);

        binding.btnBack.setOnClickListener(v -> finish());

        int userId = getCurrentUserId();

        loadMembershipCard(userId);
        setupBenefitButtons();
        loadExclusiveOffers(userId);
    }

    /** Current signed-in user id, mirroring how PremiumProfileFragment reads it. */
    private int getCurrentUserId() {
        SharedPreferences prefs = getSharedPreferences("user_prefs", MODE_PRIVATE);
        return prefs.getInt("user_id", 1);
    }

    private void loadMembershipCard(int userId) {
        User user = new UserDAO(this).getUserById(userId);
        if (user == null) {
            return;
        }
        if (user.getMembershipLevel() != null) {
            binding.tvMemberLevel.setText(user.getMembershipLevel().toUpperCase(VN));
        }
        binding.tvPoints.setText(String.format(VN, "%,d điểm", user.getPoints()));
        // "Xem hạng & quyền lợi" is UI-only for now (no navigation yet).
    }

    private void setupBenefitButtons() {
        binding.benefitVoucher.setOnClickListener(v -> showBenefitDialog(
                R.drawable.voucher_doc_quyen,
                "Voucher độc quyền",
                "Nhận các voucher giảm giá độc quyền chỉ dành riêng cho hội viên PomPom, "
                        + "không công khai cho khách thường."));

        binding.benefitFreeship.setOnClickListener(v -> showBenefitDialog(
                R.drawable.freeship_toan_quoc,
                "Freeship toàn quốc",
                "Miễn phí vận chuyển cho đơn hàng trên toàn quốc, giúp bạn mua sắm thoải mái "
                        + "mà không lo phí ship."));

        binding.benefitDiscount.setOnClickListener(v -> showBenefitDialog(
                R.drawable.giam_gia_dac_biet,
                "Giảm giá đặc biệt",
                "Được hưởng mức chiết khấu cao hơn và các ưu đãi giảm giá riêng vào những dịp "
                        + "sale lớn trong năm."));

        binding.benefitBirthday.setOnClickListener(v -> showBenefitDialog(
                R.drawable.qua_tang_sinh_nhat,
                "Quà tặng sinh nhật",
                "Nhận quà tặng và voucher đặc biệt trong tháng sinh nhật của bạn như một lời "
                        + "tri ân từ PomPom."));
    }

    private void showBenefitDialog(@DrawableRes int iconRes, String title, String message) {
        View content = getLayoutInflater().inflate(R.layout.dialog_benefit, null);
        ((ImageView) content.findViewById(R.id.ivBenefitIcon)).setImageResource(iconRes);
        ((TextView) content.findViewById(R.id.tvBenefitTitle)).setText(title);
        ((TextView) content.findViewById(R.id.tvBenefitDesc)).setText(message);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(content)
                .create();
        if (dialog.getWindow() != null) {
            // Let the rounded card background show instead of the default dialog frame.
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        content.findViewById(R.id.btnClose).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void loadExclusiveOffers(int userId) {
        PromotionDAO promotionDAO = new PromotionDAO(this);
        List<Voucher> vouchers = promotionDAO.getAllVouchers();

        if (vouchers.isEmpty()) {
            binding.rvOffers.setVisibility(View.GONE);
            binding.tvOffersEmpty.setVisibility(View.VISIBLE);
            return;
        }

        Set<Integer> savedIds = promotionDAO.getSavedVoucherIds(userId);

        VoucherPromoAdapter adapter = new VoucherPromoAdapter(vouchers);
        adapter.setSavedVoucherIds(savedIds);
        // "Lưu" persists the voucher into the existing user_vouchers table.
        adapter.setOnSaveListener(voucher -> promotionDAO.saveVoucherForUser(userId, voucher.getId()));

        binding.rvOffers.setLayoutManager(new LinearLayoutManager(this));
        binding.rvOffers.setAdapter(adapter);
    }
}
