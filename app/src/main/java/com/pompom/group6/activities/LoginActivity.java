package com.pompom.group6.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Toast;

import com.pompom.group6.R;
import com.pompom.group6.databinding.ActivityLoginBinding;
import com.pompom.group6.network.ApiClient;
import com.pompom.group6.network.Session;
import com.pompom.group6.network.dto.ApiUser;
import com.pompom.group6.network.dto.AuthDtos;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends SwipeBackActivity {

    private ActivityLoginBinding binding;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Transparent status bar
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        );
        getWindow().setStatusBarColor(android.graphics.Color.TRANSPARENT);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
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

        binding.btnLoginSubmit.setOnClickListener(v -> {
            String email = binding.etUsername.getText().toString().trim();
            String password = binding.etPassword.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Vui lòng nhập Email và Mật khẩu", Toast.LENGTH_SHORT).show();
                return;
            }

            // Xác thực qua backend (MongoDB) thay vì SQLite cục bộ.
            binding.btnLoginSubmit.setEnabled(false);
            ApiClient.get().login(new AuthDtos.LoginRequest(email, password))
                    .enqueue(new Callback<ApiUser>() {
                        @Override
                        public void onResponse(Call<ApiUser> call, Response<ApiUser> resp) {
                            binding.btnLoginSubmit.setEnabled(true);
                            if (resp.isSuccessful() && resp.body() != null) {
                                Session.save(LoginActivity.this, resp.body());
                                Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Email hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ApiUser> call, Throwable t) {
                            binding.btnLoginSubmit.setEnabled(true);
                            android.util.Log.e("PomPomLoginDebug", "login onFailure url=" + call.request().url(), t);
                            Toast.makeText(LoginActivity.this,
                                    "Không kết nối được máy chủ. Kiểm tra backend đang chạy?", Toast.LENGTH_LONG).show();
                        }
                    });
        });

        binding.tvForgotPassword.setOnClickListener(v -> 
            Toast.makeText(this, "Tính năng Quên mật khẩu đang phát triển", Toast.LENGTH_SHORT).show());

        binding.tvRegisterLink.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
