package com.pompom.group6.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Toast;

import com.pompom.group6.R;
import com.pompom.group6.databinding.ActivityRegisterBinding;
import com.pompom.group6.network.ApiClient;
import com.pompom.group6.network.Session;
import com.pompom.group6.network.dto.ApiUser;
import com.pompom.group6.network.dto.AuthDtos;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends SwipeBackActivity {

    private ActivityRegisterBinding binding;
    private boolean isPasswordVisible = false;
    private boolean isConfirmPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Transparent status bar
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        );
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);

        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupListeners();
    }

    private void setupListeners() {
        binding.btnBack.setOnClickListener(v -> finish());

        binding.ivShowPassword.setOnClickListener(v -> {
            isPasswordVisible = !isPasswordVisible;
            if (isPasswordVisible) {
                binding.etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                binding.ivShowPassword.setImageResource(R.drawable.ic_view);
            } else {
                binding.etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                binding.ivShowPassword.setImageResource(R.drawable.ic_hidden);
            }
            binding.etPassword.setSelection(binding.etPassword.getText().length());
        });

        binding.ivShowConfirmPassword.setOnClickListener(v -> {
            isConfirmPasswordVisible = !isConfirmPasswordVisible;
            if (isConfirmPasswordVisible) {
                binding.etConfirmPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                binding.ivShowConfirmPassword.setImageResource(R.drawable.ic_view);
            } else {
                binding.etConfirmPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                binding.ivShowConfirmPassword.setImageResource(R.drawable.ic_hidden);
            }
            binding.etConfirmPassword.setSelection(binding.etConfirmPassword.getText().length());
        });

        binding.btnRegisterSubmit.setOnClickListener(v -> {
            String email = binding.etEmail.getText().toString().trim();
            String phone = binding.etPhone.getText().toString().trim();
            String password = binding.etPassword.getText().toString();
            String confirmPassword = binding.etConfirmPassword.getText().toString();

            if (email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Email không hợp lệ", Toast.LENGTH_SHORT).show();
                return;
            }

            if (password.length() < 6) {
                Toast.makeText(this, "Mật khẩu phải có ít nhất 6 ký tự", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "Mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!binding.cbTerms.isChecked()) {
                Toast.makeText(this, "Vui lòng đồng ý với điều khoản", Toast.LENGTH_SHORT).show();
                return;
            }

            String fullName = deriveNameFromEmail(email);
            binding.btnRegisterSubmit.setEnabled(false);
            ApiClient.get().register(new AuthDtos.RegisterRequest(fullName, email, password, phone))
                    .enqueue(new Callback<ApiUser>() {
                        @Override
                        public void onResponse(Call<ApiUser> call, Response<ApiUser> resp) {
                            binding.btnRegisterSubmit.setEnabled(true);
                            if (resp.isSuccessful() && resp.body() != null) {
                                // Tự đăng nhập tài khoản vừa tạo
                                Session.save(RegisterActivity.this, resp.body());
                                Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else if (resp.code() == 409) {
                                Toast.makeText(RegisterActivity.this, "Email đã được sử dụng", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterActivity.this, "Đăng ký thất bại, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiUser> call, Throwable t) {
                            binding.btnRegisterSubmit.setEnabled(true);
                            Toast.makeText(RegisterActivity.this,
                                    "Không kết nối được máy chủ. Kiểm tra backend đang chạy?", Toast.LENGTH_LONG).show();
                        }
                    });
        });

        binding.tvLoginLink.setOnClickListener(v -> finish());
    }

    /** Builds a friendly display name from the email local-part, e.g. "thao.nguyen" -> "Thao Nguyen". */
    private String deriveNameFromEmail(String email) {
        String local = email.substring(0, email.indexOf('@'));
        String[] parts = local.split("[._-]+");
        StringBuilder sb = new StringBuilder();
        for (String part : parts) {
            if (part.isEmpty()) continue;
            sb.append(Character.toUpperCase(part.charAt(0)));
            if (part.length() > 1) sb.append(part.substring(1));
            sb.append(' ');
        }
        String name = sb.toString().trim();
        return name.isEmpty() ? "PomPom User" : name;
    }
}
