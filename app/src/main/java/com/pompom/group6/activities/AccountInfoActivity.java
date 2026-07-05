package com.pompom.group6.activities;

import android.os.Bundle;
import android.widget.Toast;

import com.pompom.group6.databinding.ActivityAccountInfoBinding;
import com.pompom.group6.network.ApiClient;
import com.pompom.group6.network.Session;
import com.pompom.group6.network.dto.ApiUser;
import com.pompom.group6.network.dto.UserUpdateRequest;
import com.pompom.group6.utils.UiUtils;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AccountInfoActivity extends SwipeBackActivity {

    private ActivityAccountInfoBinding binding;
    private String userOid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        UiUtils.applyPinkStatusBar(this);

        binding.header.tvHeaderTitle.setText("Thông tin tài khoản");
        binding.header.btnBack.setOnClickListener(v -> finish());

        userOid = Session.getUserOid(this);

        loadUser();
        binding.btnSave.setOnClickListener(v -> save());
    }

    private void loadUser() {
        if (userOid == null) {
            Toast.makeText(this, "Bạn cần đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }
        ApiClient.get().getUser(userOid).enqueue(new Callback<ApiUser>() {
            @Override
            public void onResponse(Call<ApiUser> call, Response<ApiUser> resp) {
                if (binding == null || !resp.isSuccessful() || resp.body() == null) {
                    if (binding != null) Toast.makeText(AccountInfoActivity.this, "Không tải được thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }
                ApiUser user = resp.body();
                binding.etFullName.setText(user.fullName);
                binding.etEmail.setText(user.email);
                binding.etPhone.setText(user.phoneNumber);
                binding.etBirthDate.setText(safeDate(user.birthDate));
                binding.etSkinType.setText(user.skinType);
                binding.etBio.setText(user.bio);

                if ("male".equalsIgnoreCase(user.gender)) {
                    binding.rbMale.setChecked(true);
                } else if ("female".equalsIgnoreCase(user.gender)) {
                    binding.rbFemale.setChecked(true);
                } else {
                    binding.rbOther.setChecked(true);
                }
            }

            @Override
            public void onFailure(Call<ApiUser> call, Throwable t) {
                if (binding != null) Toast.makeText(AccountInfoActivity.this, "Không kết nối được máy chủ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String safeDate(String raw) {
        if (raw == null) return "";
        // Stored as "1995-05-10 00:00:00" -> show just the date part
        return raw.length() >= 10 ? raw.substring(0, 10) : raw;
    }

    private void save() {
        String name = text(binding.etFullName.getText());
        if (name.isEmpty()) {
            Toast.makeText(this, "Tên không được để trống", Toast.LENGTH_SHORT).show();
            return;
        }
        String phone = text(binding.etPhone.getText());
        String bio = text(binding.etBio.getText());
        String birth = text(binding.etBirthDate.getText());
        String skin = text(binding.etSkinType.getText());

        String gender = "other";
        if (binding.rbMale.isChecked()) gender = "male";
        else if (binding.rbFemale.isChecked()) gender = "female";

        if (userOid == null) {
            Toast.makeText(this, "Bạn cần đăng nhập", Toast.LENGTH_SHORT).show();
            return;
        }
        binding.btnSave.setEnabled(false);
        UserUpdateRequest body = new UserUpdateRequest(name, phone, bio, gender, birth, skin);
        ApiClient.get().updateUser(userOid, body).enqueue(new Callback<ApiUser>() {
            @Override
            public void onResponse(Call<ApiUser> call, Response<ApiUser> resp) {
                if (binding == null) return;
                binding.btnSave.setEnabled(true);
                if (resp.isSuccessful()) {
                    Toast.makeText(AccountInfoActivity.this, "Đã lưu thông tin", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(AccountInfoActivity.this, "Lưu thất bại", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiUser> call, Throwable t) {
                if (binding == null) return;
                binding.btnSave.setEnabled(true);
                Toast.makeText(AccountInfoActivity.this, "Không kết nối được máy chủ", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String text(CharSequence cs) {
        return cs == null ? "" : cs.toString().trim();
    }
}
