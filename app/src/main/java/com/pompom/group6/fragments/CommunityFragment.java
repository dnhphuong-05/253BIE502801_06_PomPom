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

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import com.pompom.group6.activities.ConsultationRequestActivity;
import com.pompom.group6.adapters.BlogAdapter;
import com.pompom.group6.adapters.CommunityPostAdapter;
import com.pompom.group6.adapters.ExpertArticleAdapter;
import com.pompom.group6.adapters.ReelAdapter;
import com.pompom.group6.adapters.StoryAdapter;
import com.pompom.group6.database.CommunityDAO;
import com.pompom.group6.databinding.FragmentCommunityBinding;
import com.pompom.group6.models.CommunityPost;
import com.pompom.group6.models.Story;
import com.pompom.group6.network.dto.ApiBlog;
import com.pompom.group6.network.dto.ApiExpertArticle;
import com.pompom.group6.network.dto.ApiNearbyPost;
import com.pompom.group6.network.dto.ApiReel;
import com.pompom.group6.utils.LocationHelper;

import java.util.ArrayList;
import java.util.List;

public class CommunityFragment extends Fragment {

    private FragmentCommunityBinding binding;
    private CommunityDAO communityDAO;
    private CommunityPostAdapter postAdapter;
    private String currentTab = "Reels";

    private final ActivityResultLauncher<String> requestLocationPermission =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), granted -> {
                if (granted) fetchNearbyStories();
                else showStoriesAddButtonOnly();
            });

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
        setupBlogs();
        setupExpertArticles();
        loadPosts("Reels");
    }

    // ── Khám phá: Blog thương hiệu ──────────────────────────────────────────

    private void setupBlogs() {
        fetchBlogs(6);
        binding.btnSeeAllBlogs.setOnClickListener(v -> fetchBlogs(50));
    }

    private void fetchBlogs(int limit) {
        com.pompom.group6.network.ApiClient.get().getBlogs(limit)
                .enqueue(new retrofit2.Callback<List<ApiBlog>>() {
                    @Override
                    public void onResponse(retrofit2.Call<List<ApiBlog>> call, retrofit2.Response<List<ApiBlog>> resp) {
                        if (binding == null || !resp.isSuccessful() || resp.body() == null) return;
                        binding.rvBlogs.setLayoutManager(
                                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
                        binding.rvBlogs.setAdapter(new BlogAdapter(resp.body()));
                    }
                    @Override public void onFailure(retrofit2.Call<List<ApiBlog>> call, Throwable t) {}
                });
    }

    // ── Khám phá: Tips từ chuyên gia + Liên hệ tư vấn ────────────────────────

    private void setupExpertArticles() {
        com.pompom.group6.network.ApiClient.get().getExpertArticles(10)
                .enqueue(new retrofit2.Callback<List<ApiExpertArticle>>() {
                    @Override
                    public void onResponse(retrofit2.Call<List<ApiExpertArticle>> call,
                                           retrofit2.Response<List<ApiExpertArticle>> resp) {
                        if (binding == null || !resp.isSuccessful() || resp.body() == null) return;
                        binding.rvExpertArticles.setLayoutManager(
                                new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
                        binding.rvExpertArticles.setAdapter(new ExpertArticleAdapter(resp.body()));
                    }
                    @Override public void onFailure(retrofit2.Call<List<ApiExpertArticle>> call, Throwable t) {}
                });

        binding.btnContactConsult.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), ConsultationRequestActivity.class)));
    }

    // ── Reels: video đăng lại từ Instagram/Facebook/TikTok ───────────────────

    private void loadReels() {
        com.pompom.group6.network.ApiClient.get().getReels(20)
                .enqueue(new retrofit2.Callback<List<ApiReel>>() {
                    @Override
                    public void onResponse(retrofit2.Call<List<ApiReel>> call, retrofit2.Response<List<ApiReel>> resp) {
                        if (binding == null || !resp.isSuccessful() || resp.body() == null) return;
                        binding.rvCommunity.setAdapter(new ReelAdapter(resp.body()));
                    }
                    @Override public void onFailure(retrofit2.Call<List<ApiReel>> call, Throwable t) {}
                });
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
        fetchPosts(30, keyword);
    }

    /** Map bài viết từ API sang model + đổ vào adapter. */
    private void fetchPosts(Integer limit, String query) {
        com.pompom.group6.network.ApiClient.get().getCommunityPosts(limit, query)
                .enqueue(new retrofit2.Callback<List<com.pompom.group6.network.dto.ApiCommunityPost>>() {
                    @Override
                    public void onResponse(retrofit2.Call<List<com.pompom.group6.network.dto.ApiCommunityPost>> call,
                                           retrofit2.Response<List<com.pompom.group6.network.dto.ApiCommunityPost>> resp) {
                        if (binding == null || !resp.isSuccessful() || resp.body() == null) return;
                        bindPostList(mapPosts(resp.body()), "Không tìm thấy bài viết phù hợp");
                    }
                    @Override public void onFailure(retrofit2.Call<List<com.pompom.group6.network.dto.ApiCommunityPost>> call, Throwable t) {}
                });
    }

    /** Tải feed cho từng tab, đúng theo dữ liệu thật: Bài viết (tất cả) / Đã lưu / Của bạn (theo user_id). */
    private void fetchPostsForTab(String tab) {
        String userOid = com.pompom.group6.network.Session.getUserOid(requireContext());
        String authorId = "Của bạn".equals(tab) ? userOid : null;
        String savedBy = "Đã lưu".equals(tab) ? userOid : null;

        if (("Của bạn".equals(tab) || "Đã lưu".equals(tab)) && userOid == null) {
            bindPostList(new java.util.ArrayList<>(), "Vui lòng đăng nhập để xem mục này");
            return;
        }

        com.pompom.group6.network.ApiClient.get().getCommunityPostsFiltered(50, authorId, savedBy, userOid)
                .enqueue(new retrofit2.Callback<List<com.pompom.group6.network.dto.ApiCommunityPost>>() {
                    @Override
                    public void onResponse(retrofit2.Call<List<com.pompom.group6.network.dto.ApiCommunityPost>> call,
                                           retrofit2.Response<List<com.pompom.group6.network.dto.ApiCommunityPost>> resp) {
                        if (binding == null || !resp.isSuccessful() || resp.body() == null) return;
                        String emptyText = "Đã lưu".equals(tab) ? "Bạn chưa lưu bài viết nào"
                                : "Của bạn".equals(tab) ? "Bạn chưa đăng bài viết nào"
                                : "Chưa có bài viết nào";
                        bindPostList(mapPosts(resp.body()), emptyText);
                    }
                    @Override public void onFailure(retrofit2.Call<List<com.pompom.group6.network.dto.ApiCommunityPost>> call, Throwable t) {}
                });
    }

    private List<CommunityPost> mapPosts(List<com.pompom.group6.network.dto.ApiCommunityPost> apiPosts) {
        List<CommunityPost> posts = new java.util.ArrayList<>();
        for (com.pompom.group6.network.dto.ApiCommunityPost a : apiPosts) {
            String img = a.images != null && !a.images.isEmpty() ? a.images.get(0) : null;
            CommunityPost cp = new CommunityPost(a.id, 0, a.content, img,
                    a.likeCount, a.commentCount, "review", a.authorName, a.authorAvatar);
            if (a.images != null) cp.setImages(a.images);
            cp.setSaved(a.isSaved);
            cp.setLiked(a.isLiked);
            posts.add(cp);
        }
        return posts;
    }

    private void bindPostList(List<CommunityPost> posts, String emptyText) {
        if (binding == null) return;
        binding.rvCommunity.setAdapter(postAdapter);
        postAdapter.setPosts(posts);
        binding.tvFeedEmpty.setText(emptyText);
        binding.tvFeedEmpty.setVisibility(posts.isEmpty() ? View.VISIBLE : View.GONE);
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

    /** Hàng Story 24h theo bán kính GPS — luôn có ô "Đăng story" đầu tiên. */
    private void setupStories() {
        binding.rvStories.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        if (LocationHelper.hasPermission(requireContext())) {
            fetchNearbyStories();
        } else {
            requestLocationPermission.launch(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }

    private void showStoriesAddButtonOnly() {
        if (binding == null) return;
        List<Story> stories = new ArrayList<>();
        stories.add(new Story());
        binding.rvStories.setAdapter(new StoryAdapter(stories));
    }

    private void fetchNearbyStories() {
        LocationHelper.getCurrentLocation(requireContext(), new LocationHelper.Callback() {
            @Override
            public void onLocation(double lat, double lng) {
                com.pompom.group6.network.ApiClient.get().getNearbyPosts(lat, lng, 100)
                        .enqueue(new retrofit2.Callback<List<ApiNearbyPost>>() {
                            @Override
                            public void onResponse(retrofit2.Call<List<ApiNearbyPost>> call,
                                                   retrofit2.Response<List<ApiNearbyPost>> resp) {
                                if (binding == null) return;
                                List<Story> stories = new ArrayList<>();
                                stories.add(new Story());
                                if (resp.isSuccessful() && resp.body() != null) {
                                    for (ApiNearbyPost p : resp.body()) stories.add(new Story(p));
                                }
                                binding.rvStories.setAdapter(new StoryAdapter(stories));
                            }
                            @Override public void onFailure(retrofit2.Call<List<ApiNearbyPost>> call, Throwable t) {
                                showStoriesAddButtonOnly();
                            }
                        });
            }

            @Override
            public void onUnavailable() {
                showStoriesAddButtonOnly();
            }
        });
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
        binding.tvFeedEmpty.setVisibility(View.GONE);
        if ("Reels".equals(type)) {
            loadReels();
            return;
        }
        // "Bài viết" = tất cả bài hiển thị; "Đã lưu"/"Của bạn" lọc thật theo user_id đang đăng nhập.
        fetchPostsForTab(type);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
