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
import androidx.transition.TransitionManager;

import com.pompom.group6.R;
import com.pompom.group6.activities.AddCommunityPostActivity;
import com.pompom.group6.activities.ConsultationRequestActivity;
import com.pompom.group6.adapters.BlogAdapter;
import com.pompom.group6.adapters.CommunityPostAdapter;
import com.pompom.group6.adapters.ExpertArticleAdapter;
import com.pompom.group6.adapters.ReelAdapter;
import com.pompom.group6.adapters.StoryAdapter;
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

/**
 * Màn Community: 4 tab nội dung — Thước phim / Blog thương hiệu / Tips bác sĩ / Tin gần đây.
 * "Tin gần đây" tái sử dụng nguyên feed cộng đồng hiện có (Bài viết/Đã lưu/Của bạn, story 24h theo GPS).
 */
public class CommunityFragment extends Fragment {

    private FragmentCommunityBinding binding;
    private CommunityPostAdapter postAdapter;
    private String currentTopTab = "Reels";
    private String currentFeedSubTab = "Bài viết";
    private final List<ApiReel> allReels = new ArrayList<>();
    private String currentReelSource = null; // null = "Tất cả"

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

        setupTopTabs();
        setupFeedRecyclerView();
        setupStories();
        setupFeedSubTabs();
        setupRefreshLayout();
        setupFabs();
        setupSearch();
        setupReelSourceFilter();

        loadReels();
        loadBlogs();
        loadTips();
        fetchPostsForTab(currentFeedSubTab);
    }

    // ── Tab trên cùng: Thước phim / Blog thương hiệu / Tips bác sĩ / Tin gần đây ──

    private void setupTopTabs() {
        binding.tabReels.setOnClickListener(v -> selectTopTab("Reels", binding.tabReels));
        binding.tabBlog.setOnClickListener(v -> selectTopTab("Blog", binding.tabBlog));
        binding.tabTips.setOnClickListener(v -> selectTopTab("Tips", binding.tabTips));
        binding.tabNearby.setOnClickListener(v -> selectTopTab("Nearby", binding.tabNearby));
    }

    private void selectTopTab(String tab, TextView tabView) {
        if (currentTopTab.equals(tab)) return;

        resetTabUI(binding.tabReels);
        resetTabUI(binding.tabBlog);
        resetTabUI(binding.tabTips);
        resetTabUI(binding.tabNearby);

        tabView.setTextColor(getResources().getColor(R.color.brand_pink));
        tabView.setTypeface(null, android.graphics.Typeface.BOLD);

        ConstraintLayout layout = (ConstraintLayout) binding.tabIndicator.getParent();
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);
        constraintSet.connect(binding.tabIndicator.getId(), ConstraintSet.START, tabView.getId(), ConstraintSet.START);
        constraintSet.connect(binding.tabIndicator.getId(), ConstraintSet.END, tabView.getId(), ConstraintSet.END);

        TransitionManager.beginDelayedTransition(layout);
        constraintSet.applyTo(layout);

        currentTopTab = tab;
        showPanel(tab);
    }

    private void resetTabUI(TextView tabView) {
        tabView.setTextColor(getResources().getColor(R.color.text_secondary));
        tabView.setTypeface(null, android.graphics.Typeface.NORMAL);
    }

    private void showPanel(String tab) {
        binding.panelReels.setVisibility("Reels".equals(tab) ? View.VISIBLE : View.GONE);
        binding.rvBlogFull.setVisibility("Blog".equals(tab) ? View.VISIBLE : View.GONE);
        binding.panelTips.setVisibility("Tips".equals(tab) ? View.VISIBLE : View.GONE);
        binding.panelNearby.setVisibility("Nearby".equals(tab) ? View.VISIBLE : View.GONE);
        // Đăng bài chỉ có ý nghĩa trong feed "Tin gần đây"; nút tư vấn luôn nổi ở mọi tab.
        binding.fabAddPost.setVisibility("Nearby".equals(tab) ? View.VISIBLE : View.GONE);
    }

    // ── Thước phim: video đăng lại từ Instagram/Facebook/TikTok ──

    private void loadReels() {
        binding.rvReels.setLayoutManager(new LinearLayoutManager(requireContext()));
        com.pompom.group6.network.ApiClient.get().getReels(20)
                .enqueue(new retrofit2.Callback<List<ApiReel>>() {
                    @Override
                    public void onResponse(retrofit2.Call<List<ApiReel>> call, retrofit2.Response<List<ApiReel>> resp) {
                        if (binding == null || !resp.isSuccessful() || resp.body() == null) return;
                        allReels.clear();
                        allReels.addAll(resp.body());
                        applyReelSourceFilter();
                    }
                    @Override public void onFailure(retrofit2.Call<List<ApiReel>> call, Throwable t) {}
                });
    }

    /** Bộ lọc theo nguồn mạng xã hội (Tất cả/Instagram/TikTok/Facebook) — lọc cục bộ trên danh sách đã tải. */
    private void setupReelSourceFilter() {
        binding.chipSourceAll.setOnClickListener(v -> selectReelSource(null, binding.chipSourceAll));
        binding.chipSourceInstagram.setOnClickListener(v -> selectReelSource("instagram", binding.chipSourceInstagram));
        binding.chipSourceTiktok.setOnClickListener(v -> selectReelSource("tiktok", binding.chipSourceTiktok));
        binding.chipSourceFacebook.setOnClickListener(v -> selectReelSource("facebook", binding.chipSourceFacebook));
    }

    private void selectReelSource(String source, TextView chip) {
        if ((source == null && currentReelSource == null) || (source != null && source.equals(currentReelSource))) return;
        currentReelSource = source;
        setChipSelected(binding.chipSourceAll, chip == binding.chipSourceAll);
        setChipSelected(binding.chipSourceInstagram, chip == binding.chipSourceInstagram);
        setChipSelected(binding.chipSourceTiktok, chip == binding.chipSourceTiktok);
        setChipSelected(binding.chipSourceFacebook, chip == binding.chipSourceFacebook);
        applyReelSourceFilter();
    }

    private void setChipSelected(TextView chip, boolean selected) {
        chip.setBackgroundResource(selected ? R.drawable.bg_label_pink : R.drawable.bg_white_pill_button);
        chip.setTextColor(getResources().getColor(selected ? R.color.white : R.color.text_secondary));
    }

    private void applyReelSourceFilter() {
        if (binding == null) return;
        List<ApiReel> filtered = new ArrayList<>();
        for (ApiReel r : allReels) {
            if (currentReelSource == null || currentReelSource.equalsIgnoreCase(r.source)) filtered.add(r);
        }
        binding.rvReels.setAdapter(new ReelAdapter(filtered));
    }

    // ── Blog thương hiệu ──

    private void loadBlogs() {
        binding.rvBlogFull.setLayoutManager(new LinearLayoutManager(requireContext()));
        com.pompom.group6.network.ApiClient.get().getBlogs(50)
                .enqueue(new retrofit2.Callback<List<ApiBlog>>() {
                    @Override
                    public void onResponse(retrofit2.Call<List<ApiBlog>> call, retrofit2.Response<List<ApiBlog>> resp) {
                        if (binding == null || !resp.isSuccessful() || resp.body() == null) return;
                        binding.rvBlogFull.setAdapter(new BlogAdapter(resp.body(), true));
                    }
                    @Override public void onFailure(retrofit2.Call<List<ApiBlog>> call, Throwable t) {}
                });
    }

    // ── Tips từ bác sĩ tư vấn + Liên hệ tư vấn ──

    private void loadTips() {
        binding.rvTipsFull.setLayoutManager(new LinearLayoutManager(requireContext()));
        com.pompom.group6.network.ApiClient.get().getExpertArticles(50)
                .enqueue(new retrofit2.Callback<List<ApiExpertArticle>>() {
                    @Override
                    public void onResponse(retrofit2.Call<List<ApiExpertArticle>> call,
                                           retrofit2.Response<List<ApiExpertArticle>> resp) {
                        if (binding == null || !resp.isSuccessful() || resp.body() == null) return;
                        binding.rvTipsFull.setAdapter(new ExpertArticleAdapter(resp.body(), true));
                    }
                    @Override public void onFailure(retrofit2.Call<List<ApiExpertArticle>> call, Throwable t) {}
                });

        binding.btnContactConsult.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), ConsultationRequestActivity.class)));
    }

    // ── Tìm kiếm (tìm trong feed "Tin gần đây" — endpoint search hiện chỉ hỗ trợ bài viết cộng đồng) ──

    private void setupSearch() {
        binding.etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 2) {
                    performSearch(s.toString());
                } else if (s.length() == 0) {
                    fetchPostsForTab(currentFeedSubTab);
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
        selectTopTab("Nearby", binding.tabNearby);
        com.pompom.group6.network.ApiClient.get().getCommunityPosts(30, keyword)
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

    /** Tải feed cho từng tab phụ, đúng theo dữ liệu thật: Bài viết (tất cả) / Đã lưu / Của bạn (theo user_id). */
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
        binding.rvFeed.setAdapter(postAdapter);
        postAdapter.setPosts(posts);
        binding.tvFeedEmpty.setText(emptyText);
        binding.tvFeedEmpty.setVisibility(posts.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void setupRefreshLayout() {
        binding.swipeRefresh.setColorSchemeColors(getResources().getColor(R.color.brand_pink));
        binding.swipeRefresh.setOnRefreshListener(() -> {
            fetchPostsForTab(currentFeedSubTab);
            binding.swipeRefresh.postDelayed(() -> binding.swipeRefresh.setRefreshing(false), 1000);
        });
    }

    private void setupFabs() {
        binding.fabAddPost.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), AddCommunityPostActivity.class)));
        binding.fabConsult.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), ConsultationRequestActivity.class)));
    }

    private void setupFeedRecyclerView() {
        postAdapter = new CommunityPostAdapter();
        binding.rvFeed.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvFeed.setAdapter(postAdapter);
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

    private void setupFeedSubTabs() {
        binding.tabPosts.setOnClickListener(v -> selectFeedSubTab("Bài viết", binding.tabPosts));
        binding.tabSaved.setOnClickListener(v -> selectFeedSubTab("Đã lưu", binding.tabSaved));
        binding.tabMyPosts.setOnClickListener(v -> selectFeedSubTab("Của bạn", binding.tabMyPosts));
    }

    private void selectFeedSubTab(String tab, TextView tabView) {
        if (currentFeedSubTab.equals(tab)) return;

        resetFeedSubTabUI(binding.tabPosts);
        resetFeedSubTabUI(binding.tabSaved);
        resetFeedSubTabUI(binding.tabMyPosts);

        tabView.setTextColor(getResources().getColor(R.color.brand_pink));
        tabView.setTypeface(null, android.graphics.Typeface.BOLD);

        ConstraintLayout layout = (ConstraintLayout) binding.feedTabIndicator.getParent();
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(layout);
        constraintSet.connect(binding.feedTabIndicator.getId(), ConstraintSet.START, tabView.getId(), ConstraintSet.START);
        constraintSet.connect(binding.feedTabIndicator.getId(), ConstraintSet.END, tabView.getId(), ConstraintSet.END);

        TransitionManager.beginDelayedTransition(layout);
        constraintSet.applyTo(layout);

        currentFeedSubTab = tab;
        binding.tvFeedEmpty.setVisibility(View.GONE);
        fetchPostsForTab(tab);
    }

    private void resetFeedSubTabUI(TextView tabView) {
        tabView.setTextColor(getResources().getColor(R.color.text_secondary));
        tabView.setTypeface(null, android.graphics.Typeface.NORMAL);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
