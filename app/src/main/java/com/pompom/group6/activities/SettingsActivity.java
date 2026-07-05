package com.pompom.group6.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.pompom.group6.database.UserDAO;
import com.pompom.group6.databinding.ActivitySettingsBinding;

import java.io.File;
import java.util.Locale;

public class SettingsActivity extends SwipeBackActivity {

    private static final String PREFS = "user_prefs";

    private ActivitySettingsBinding binding;
    private SharedPreferences prefs;
    private UserDAO userDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Pink status bar with light (white) icons to match the header
        getWindow().setStatusBarColor(androidx.core.content.ContextCompat.getColor(this, com.pompom.group6.R.color.brand_pink));
        androidx.core.view.WindowInsetsControllerCompat controller =
                androidx.core.view.WindowCompat.getInsetsController(getWindow(), getWindow().getDecorView());
        if (controller != null) {
            controller.setAppearanceLightStatusBars(false);
        }

        prefs = getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        userDAO = new UserDAO(this);

        setupToggles();
        setupRows();
        showVersion();
        updateCacheSize();

        binding.btnBack.setOnClickListener(v -> finish());
        binding.btnLogout.setOnClickListener(v -> confirmLogout());
    }

    private void setupToggles() {
        binding.switchNotifications.setChecked(prefs.getBoolean("notifications_enabled", true));
        binding.switchPromoEmails.setChecked(prefs.getBoolean("promo_emails", false));

        binding.switchNotifications.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("notifications_enabled", checked).apply());
        binding.switchPromoEmails.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("promo_emails", checked).apply());
    }

    private void setupRows() {
        binding.rowClearCache.setOnClickListener(v -> clearCache());
        binding.rowChangePassword.setOnClickListener(v -> showChangePasswordDialog());
        binding.rowPrivacy.setOnClickListener(v -> showInfoDialog("Chính sách bảo mật",
                "PomPom cam kết bảo mật thông tin cá nhân của bạn. Dữ liệu chỉ được dùng để " +
                        "cung cấp dịch vụ, cải thiện trải nghiệm và không chia sẻ cho bên thứ ba khi " +
                        "chưa có sự đồng ý của bạn."));
        binding.rowTerms.setOnClickListener(v -> showInfoDialog("Điều khoản sử dụng",
                "Khi sử dụng ứng dụng PomPom, bạn đồng ý tuân thủ các quy định về mua bán, " +
                        "thanh toán và ứng xử cộng đồng. Vui lòng sử dụng ứng dụng đúng mục đích."));
    }

    private void showVersion() {
        String version = "1.0.0";
        try {
            version = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
        } catch (Exception ignored) {
        }
        binding.tvVersion.setText(version);
    }

    // ---------------------------------------------------------------------
    // Cache
    // ---------------------------------------------------------------------

    private void updateCacheSize() {
        long bytes = dirSize(getCacheDir());
        double mb = bytes / (1024.0 * 1024.0);
        binding.tvCacheSize.setText(String.format(Locale.getDefault(), "%.1f MB", mb));
    }

    private void clearCache() {
        // Disk cache must be cleared off the main thread
        new Thread(() -> {
            Glide.get(getApplicationContext()).clearDiskCache();
            runOnUiThread(() -> {
                Glide.get(getApplicationContext()).clearMemory();
                updateCacheSize();
                toast("Đã xóa bộ nhớ đệm");
            });
        }).start();
    }

    private long dirSize(File dir) {
        if (dir == null || !dir.exists()) return 0;
        long size = 0;
        File[] files = dir.listFiles();
        if (files == null) return 0;
        for (File f : files) {
            size += f.isDirectory() ? dirSize(f) : f.length();
        }
        return size;
    }

    // ---------------------------------------------------------------------
    // Change password
    // ---------------------------------------------------------------------

    private void showChangePasswordDialog() {
        int userId = prefs.getInt("user_id", -1);
        if (userId == -1) {
            toast("Vui lòng đăng nhập lại");
            return;
        }

        int pad = (int) (20 * getResources().getDisplayMetrics().density);
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(pad, pad / 2, pad, 0);

        final EditText etOld = passwordField("Mật khẩu hiện tại");
        final EditText etNew = passwordField("Mật khẩu mới");
        final EditText etConfirm = passwordField("Nhập lại mật khẩu mới");
        container.addView(etOld);
        container.addView(etNew);
        container.addView(etConfirm);

        new AlertDialog.Builder(this)
                .setTitle("Đổi mật khẩu")
                .setView(container)
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String oldPw = etOld.getText().toString();
                    String newPw = etNew.getText().toString();
                    String confirmPw = etConfirm.getText().toString();

                    if (oldPw.isEmpty() || newPw.isEmpty()) {
                        toast("Vui lòng nhập đầy đủ");
                        return;
                    }
                    if (newPw.length() < 6) {
                        toast("Mật khẩu mới phải có ít nhất 6 ký tự");
                        return;
                    }
                    if (!newPw.equals(confirmPw)) {
                        toast("Mật khẩu mới không khớp");
                        return;
                    }
                    if (userDAO.changePassword(userId, oldPw, newPw)) {
                        toast("Đổi mật khẩu thành công");
                    } else {
                        toast("Mật khẩu hiện tại không đúng");
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private EditText passwordField(String hint) {
        EditText et = new EditText(this);
        et.setHint(hint);
        et.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        return et;
    }

    // ---------------------------------------------------------------------
    // Logout
    // ---------------------------------------------------------------------

    private void confirmLogout() {
        new AlertDialog.Builder(this)
                .setTitle("Đăng xuất")
                .setMessage("Bạn có chắc muốn đăng xuất khỏi tài khoản?")
                .setPositiveButton("Đăng xuất", (dialog, which) -> {
                    prefs.edit().clear().apply();
                    toast("Đã đăng xuất");
                    finish(); // MainActivity.onResume() will swap back to the guest (Me) screen
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private void showInfoDialog(String title, String message) {
        new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Đóng", null)
                .show();
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
