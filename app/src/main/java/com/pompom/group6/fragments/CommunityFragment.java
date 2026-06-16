package com.pompom.group6.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.pompom.group6.R;
import com.pompom.group6.adapters.CommunityPostAdapter;
import com.pompom.group6.database.CommunityDAO;
import com.pompom.group6.databinding.FragmentCommunityBinding;
import com.pompom.group6.models.CommunityPost;

import java.util.List;

public class CommunityFragment extends Fragment {

    private FragmentCommunityBinding binding;
    private CommunityDAO communityDAO;
    private CommunityPostAdapter adapter;
    private String currentTab = "For you";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCommunityBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        communityDAO = new CommunityDAO(requireContext());
        setupRecyclerView();
        setupTabListeners();
        loadPosts("For you");
    }

    private void setupRecyclerView() {
        adapter = new CommunityPostAdapter();
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        binding.rvCommunity.setLayoutManager(layoutManager);
        binding.rvCommunity.setAdapter(adapter);
    }

    private void setupTabListeners() {
        binding.tabForYou.setOnClickListener(v -> selectTab("For you", binding.tabForYou));
        binding.tabMakeupTips.setOnClickListener(v -> selectTab("Makeup Tips", binding.tabMakeupTips));
        binding.tabReview.setOnClickListener(v -> selectTab("Review", binding.tabReview));
        binding.tabLooks.setOnClickListener(v -> selectTab("Looks", binding.tabLooks));
    }

    private void selectTab(String tab, TextView tabView) {
        if (currentTab.equals(tab)) return;
        
        // Reset old tabs (simplified logic)
        resetTabUI(binding.tabForYou);
        resetTabUI(binding.tabMakeupTips);
        resetTabUI(binding.tabReview);
        resetTabUI(binding.tabLooks);
        
        // Set new tab UI
        tabView.setBackgroundResource(R.drawable.bg_white_pill_button);
        tabView.setBackgroundTintList(android.content.res.ColorStateList.valueOf(getResources().getColor(R.color.white)));
        tabView.setTextColor(getResources().getColor(R.color.brand_pink));
        
        currentTab = tab;
        loadPosts(tab);
    }

    private void resetTabUI(TextView tabView) {
        tabView.setBackground(null);
        tabView.setTextColor(getResources().getColor(R.color.text_secondary));
    }

    private void loadPosts(String type) {
        List<CommunityPost> posts = communityDAO.getPostsByType(type, 20);
        adapter.setPosts(posts);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
