package com.pompom.group6.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import com.pompom.group6.R;
import com.pompom.group6.database.UserDAO;
import com.pompom.group6.databinding.ActivityLoginBinding;
import com.pompom.group6.models.User;

public class LoginActivity extends SwipeBackActivity {

    private ActivityLoginBinding binding;
    private UserDAO userDAO;
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

        userDAO = new UserDAO(this);

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

            User user = userDAO.authenticate(email, password);
            if (user == null) {
                Toast.makeText(this, "Email hoặc mật khẩu không đúng", Toast.LENGTH_SHORT).show();
                return;
            }

            // Persist the logged-in session
            SharedPreferences prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putBoolean("is_logged_in", true);
            editor.putInt("user_id", user.getUserId());
            editor.apply();

            Toast.makeText(this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
            finish();
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
