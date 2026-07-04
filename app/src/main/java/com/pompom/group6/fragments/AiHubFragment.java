package com.pompom.group6.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pompom.group6.activities.AiCallActivity;
import com.pompom.group6.activities.AiChatActivity;
import com.pompom.group6.activities.ArTryOnActivity;
import com.pompom.group6.activities.MakeupArtistActivity;
import com.pompom.group6.databinding.FragmentAiHubBinding;

public class AiHubFragment extends Fragment {

    private FragmentAiHubBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAiHubBinding.inflate(inflater, container, false);
        setupListeners();
        return binding.getRoot();
    }

    private void setupListeners() {
        binding.btnCallDermatologist.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AiCallActivity.class);
            startActivity(intent);
        });

        binding.btnChatAdvisor.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AiChatActivity.class);
            startActivity(intent);
        });

        binding.btnCallMakeup.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MakeupArtistActivity.class);
            startActivity(intent);
        });

        binding.btnOpenTryon.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), ArTryOnActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
