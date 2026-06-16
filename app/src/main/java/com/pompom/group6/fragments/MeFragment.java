package com.pompom.group6.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pompom.group6.databinding.FragmentMeBinding;

public class MeFragment extends Fragment {

    private FragmentMeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentMeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupListeners();
    }

    private void setupListeners() {
        binding.btnLogin.setOnClickListener(v -> Toast.makeText(getContext(), "Tính năng Đăng nhập đang phát triển", Toast.LENGTH_SHORT).show());
        binding.btnRegister.setOnClickListener(v -> Toast.makeText(getContext(), "Tính năng Đăng ký đang phát triển", Toast.LENGTH_SHORT).show());
        
        binding.btnGoogle.setOnClickListener(v -> Toast.makeText(getContext(), "Đăng nhập Google", Toast.LENGTH_SHORT).show());
        binding.btnApple.setOnClickListener(v -> Toast.makeText(getContext(), "Đăng nhập Apple", Toast.LENGTH_SHORT).show());
        binding.btnFacebook.setOnClickListener(v -> Toast.makeText(getContext(), "Đăng nhập Facebook", Toast.LENGTH_SHORT).show());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
