package com.pompom.group6.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pompom.group6.R;
import com.pompom.group6.adapters.CommunityPostAdapter;
import com.pompom.group6.adapters.StoryAdapter;
import com.pompom.group6.database.CommunityDAO;
import com.pompom.group6.databinding.FragmentCommunityBinding;
import com.pompom.group6.models.CommunityPost;
import com.pompom.group6.models.Story;

import java.util.ArrayList;
import java.util.List;

public class CommunityFragment extends Fragment {

    private FragmentCommunityBinding binding;
    private CommunityDAO communityDAO;
    private CommunityPostAdapter postAdapter;
    private String currentTab = "Bài viết";

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
        setupStories();
        setupTabListeners();
        loadPosts("Bài viết");
    }

    private void setupRecyclerView() {
        postAdapter = new CommunityPostAdapter();
        binding.rvCommunity.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvCommunity.setAdapter(postAdapter);
    }

    private void setupStories() {
        List<Story> stories = new ArrayList<>();
        // 1. Create Room Story
        stories.add(new Story(0, "Tạo Room", "Trò chuyện ngay", 0, false, false, true));
        // 2. Live Story (Huyền My)
        stories.add(new Story(2, "Huyền My", "Đang live", R.drawable.ic_avatar, false, true, false));
        // 3. User Story (Trần Linh)
        stories.add(new Story(3, "Trần Linh", "Story mới", R.drawable.ic_avatar, false, false, false));
        // 4. Topics
        stories.add(new Story(4, "Skincare 101", "1.2K thành viên", R.drawable.promotion1, false, false, false));
        stories.add(new Story(5, "Makeup Tips", "856 thành viên", R.drawable.promotion2, false, false, false));
        
        StoryAdapter storyAdapter = new StoryAdapter(stories);
        binding.rvStories.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvStories.setAdapter(storyAdapter);
    }

    private void setupTabListeners() {
        binding.tabPosts.setOnClickListener(v -> selectTab("Bài viết", binding.tabPosts, 24));
        binding.tabReels.setOnClickListener(v -> selectTab("Reels", binding.tabReels, 116));
        binding.tabVideo.setOnClickListener(v -> selectTab("Video", binding.tabVideo, 204));
        binding.tabSaved.setOnClickListener(v -> selectTab("Đã lưu", binding.tabSaved, 302));
    }

    private void selectTab(String tab, TextView tabView, int marginDp) {
        if (currentTab.equals(tab)) return;
        
        resetTabUI(binding.tabPosts);
        resetTabUI(binding.tabReels);
        resetTabUI(binding.tabVideo);
        resetTabUI(binding.tabSaved);
        
        tabView.setTextColor(getResources().getColor(R.color.brand_pink));
        tabView.setTypeface(null, android.graphics.Typeface.BOLD);
        
        float density = getResources().getDisplayMetrics().density;
        ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) binding.tabIndicator.getLayoutParams();
        params.leftMargin = (int) (marginDp * density);
        binding.tabIndicator.setLayoutParams(params);

        currentTab = tab;
        loadPosts(tab);
    }

    private void resetTabUI(TextView tabView) {
        tabView.setTextColor(getResources().getColor(R.color.text_secondary));
        tabView.setTypeface(null, android.graphics.Typeface.NORMAL);
    }

    private void loadPosts(String type) {
        String dbType = type.equals("Bài viết") ? "review" : "review";
        List<CommunityPost> posts = communityDAO.getPostsByType(dbType, 20);
        postAdapter.setPosts(posts);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
