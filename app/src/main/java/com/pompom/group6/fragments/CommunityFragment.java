package com.pompom.group6.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.transition.TransitionManager;

import com.pompom.group6.R;
import com.pompom.group6.activities.AddCommunityPostActivity;
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
    private String currentTab = "Reels";

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
        setupRefreshLayout();
        setupFab();
        setupSearch();
        loadPosts("Reels");
    }

    private void setupSearch() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 2) {
                    performSearch(s.toString());
                } else if (s.length() == 0) {
                    loadPosts(currentTab);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        binding.etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch(binding.etSearch.getText().toString());
                return true;
            }
            return false;
        });
    }

    private void performSearch(String keyword) {
        List<CommunityPost> searchResults = communityDAO.searchPosts(keyword, 30);
        postAdapter.setPosts(searchResults);
    }

    private void setupRefreshLayout() {
        binding.swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.brand_pink));
        binding.swipeRefresh.setOnRefreshListener(() -> {
            loadPosts(currentTab);
            // Simulate network delay
            binding.swipeRefresh.postDelayed(() -> binding.swipeRefresh.setRefreshing(false), 1000);
        });
    }

    private void setupFab() {
        binding.fabAddPost.setOnClickListener(v -> {
            Intent intent = new Intent(requireContext(), AddCommunityPostActivity.class);
            startActivity(intent);
        });
        
        // Hide/Show FAB on scroll
        binding.rvCommunity.addOnScrollListener(new androidx.recyclerview.widget.RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull androidx.recyclerview.widget.RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 && binding.fabAddPost.isShown()) {
                    binding.fabAddPost.hide();
                } else if (dy < 0 && !binding.fabAddPost.isShown()) {
                    binding.fabAddPost.show();
                }
            }
        });
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
        binding.tabReels.setOnClickListener(v -> selectTab("Reels", binding.tabReels));
        binding.tabPosts.setOnClickListener(v -> selectTab("Bài viết", binding.tabPosts));
        binding.tabSaved.setOnClickListener(v -> selectTab("Đã lưu", binding.tabSaved));
        binding.tabMyPosts.setOnClickListener(v -> selectTab("Của bạn", binding.tabMyPosts));
    }

    private void selectTab(String tab, TextView tabView) {
        if (currentTab.equals(tab)) return;
        
        resetTabUI(binding.tabReels);
        resetTabUI(binding.tabPosts);
        resetTabUI(binding.tabSaved);
        resetTabUI(binding.tabMyPosts);
        
        tabView.setTextColor(getResources().getColor(R.color.brand_pink));
        tabView.setTypeface(null, android.graphics.Typeface.BOLD);
        
        // Di chuyển thanh gạch chân mượt mà bằng ConstraintSet
        ConstraintLayout layout = (ConstraintLayout) binding.tabIndicator.getParent();
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);
        constraintSet.connect(binding.tabIndicator.getId(), ConstraintSet.START, tabView.getId(), ConstraintSet.START);
        constraintSet.connect(binding.tabIndicator.getId(), ConstraintSet.END, tabView.getId(), ConstraintSet.END);
        
        TransitionManager.beginDelayedTransition(layout);
        constraintSet.applyTo(layout);

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
