package com.pompom.group6.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pompom.group6.database.UserDAO;
import com.pompom.group6.databinding.ActivityAccountInfoBinding;
import com.pompom.group6.models.User;
import com.pompom.group6.utils.UiUtils;

public class AccountInfoActivity extends SwipeBackActivity {

    private ActivityAccountInfoBinding binding;
    private UserDAO userDAO;
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAccountInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        UiUtils.applyPinkStatusBar(this);

        binding.header.tvHeaderTitle.setText("Thông tin tài khoản");
        binding.header.btnBack.setOnClickListener(v -> finish());

        userDAO = new UserDAO(this);
        SharedPreferences prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        userId = prefs.getInt("user_id", 1);

        loadUser();
        binding.btnSave.setOnClickListener(v -> save());
    }

    private void loadUser() {
        User user = userDAO.getUserById(userId);
        if (user == null) {
            Toast.makeText(this, "Không tải được thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
        binding.etFullName.setText(user.getFullName());
        binding.etEmail.setText(user.getEmail());
        binding.etPhone.setText(user.getPhoneNumber());
        binding.etBirthDate.setText(safeDate(user.getBirthDate()));
        binding.etSkinType.setText(user.getSkinType());
        binding.etBio.setText(user.getBio());

        if ("male".equalsIgnoreCase(user.getGender())) {
            binding.rbMale.setChecked(true);
        } else if ("female".equalsIgnoreCase(user.getGender())) {
            binding.rbFemale.setChecked(true);
        } else {
            binding.rbOther.setChecked(true);
        }
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

        if (userDAO.updateAccount(userId, name, phone, bio, gender, birth, skin)) {
            Toast.makeText(this, "Đã lưu thông tin", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Lưu thất bại", Toast.LENGTH_SHORT).show();
        }
    }

    private String text(CharSequence cs) {
        return cs == null ? "" : cs.toString().trim();
    }
}
