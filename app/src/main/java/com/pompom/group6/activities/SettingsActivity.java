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
        binding.rowAddress.setOnClickListener(v -> startActivity(new android.content.Intent(this, AddressBookActivity.class)));
        binding.rowSupportContact.setOnClickListener(v -> startActivity(new android.content.Intent(this, ConsultationRequestActivity.class)));
        binding.rowLanguage.setOnClickListener(v -> startActivity(new android.content.Intent(this, LanguageActivity.class)));
        binding.rowPolicyHelp.setOnClickListener(v -> startActivity(new android.content.Intent(this, PolicyHelpActivity.class)));

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
        String userOid = com.pompom.group6.network.Session.getUserOid(this);
        if (userOid == null) {
            toast("Vui lòng đăng nhập lại");
            return;
        }

        View content = getLayoutInflater().inflate(com.pompom.group6.R.layout.dialog_change_password, null);
        final EditText etOld = content.findViewById(com.pompom.group6.R.id.etOldPassword);
        final EditText etNew = content.findViewById(com.pompom.group6.R.id.etNewPassword);
        final EditText etConfirm = content.findViewById(com.pompom.group6.R.id.etConfirmPassword);

        bindPasswordToggle(content.findViewById(com.pompom.group6.R.id.ivToggleOld), etOld);
        bindPasswordToggle(content.findViewById(com.pompom.group6.R.id.ivToggleNew), etNew);
        bindPasswordToggle(content.findViewById(com.pompom.group6.R.id.ivToggleConfirm), etConfirm);

        AlertDialog dialog = new AlertDialog.Builder(this).setView(content).create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        content.findViewById(com.pompom.group6.R.id.btnCancelPw).setOnClickListener(v -> dialog.dismiss());
        content.findViewById(com.pompom.group6.R.id.btnSavePw).setOnClickListener(v -> {
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
            com.pompom.group6.network.ApiClient.get()
                    .changePassword(userOid, new com.pompom.group6.network.dto.ChangePasswordRequest(oldPw, newPw))
                    .enqueue(new retrofit2.Callback<Void>() {
                        @Override
                        public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> resp) {
                            if (resp.isSuccessful()) {
                                toast("Đổi mật khẩu thành công");
                                dialog.dismiss();
                            } else if (resp.code() == 400) {
                                toast("Mật khẩu hiện tại không đúng");
                            } else {
                                toast("Đổi mật khẩu thất bại");
                            }
                        }
                        @Override
                        public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                            toast("Không kết nối được máy chủ");
                        }
                    });
        });

        dialog.show();
    }

    /** Nút con mắt: bật/tắt hiển thị mật khẩu, giữ nguyên font và vị trí con trỏ. */
    private void bindPasswordToggle(android.widget.ImageView toggle, EditText field) {
        final android.graphics.Typeface tf = field.getTypeface();
        final boolean[] shown = {false};
        toggle.setOnClickListener(v -> {
            shown[0] = !shown[0];
            int sel = field.getSelectionEnd();
            field.setInputType(InputType.TYPE_CLASS_TEXT | (shown[0]
                    ? InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                    : InputType.TYPE_TEXT_VARIATION_PASSWORD));
            field.setTypeface(tf);
            field.setSelection(sel);
            toggle.setImageResource(shown[0]
                    ? com.pompom.group6.R.drawable.ic_view
                    : com.pompom.group6.R.drawable.ic_hidden);
        });
    }

    // ---------------------------------------------------------------------
    // Logout
    // ---------------------------------------------------------------------

    private void confirmLogout() {
        View content = getLayoutInflater().inflate(com.pompom.group6.R.layout.dialog_logout, null);
        AlertDialog dialog = new AlertDialog.Builder(this).setView(content).create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        content.findViewById(com.pompom.group6.R.id.btnStay).setOnClickListener(v -> dialog.dismiss());
        content.findViewById(com.pompom.group6.R.id.btnConfirmLogout).setOnClickListener(v -> {
            dialog.dismiss();
            prefs.edit().clear().apply();
            toast("Đã đăng xuất");
            finish(); // MainActivity.onResume() will swap back to the guest (Me) screen
        });
        dialog.show();
    }

    private void showInfoDialog(String title, String message) {
        View content = getLayoutInflater().inflate(com.pompom.group6.R.layout.dialog_info, null);
        ((android.widget.TextView) content.findViewById(com.pompom.group6.R.id.tvInfoTitle)).setText(title);
        ((android.widget.TextView) content.findViewById(com.pompom.group6.R.id.tvInfoMsg)).setText(message);
        AlertDialog dialog = new AlertDialog.Builder(this).setView(content).create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }
        content.findViewById(com.pompom.group6.R.id.btnInfoClose).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void toast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
