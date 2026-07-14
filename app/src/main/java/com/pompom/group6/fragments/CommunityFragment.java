package com.pompom.group6.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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
import com.pompom.group6.utils.SwipeTabFrameLayout;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.List;

/**
 * Màn Community — 3 tab:
 *  - "Bài viết": feed UGC (mọi bài viết cộng đồng, mới nhất trước) + Story 24h theo bán kính GPS
 *    ở đầu tab (đã gộp từ tab "Gần đây" cũ) — tab mặc định.
 *  - "Thước phim": reels đăng lại từ Instagram/Facebook/TikTok, lọc theo nguồn — khôi phục lại.
 *  - "Khám phá": nội dung biên tập (Blog thương hiệu / Tips bác sĩ), chuyển bằng chip.
 * Quyền GPS xin ngay khi mở Community vì Story giờ nằm trong tab mặc định.
 */
public class CommunityFragment extends Fragment {

    private static final String[] TAB_ORDER = {"Posts", "Reels", "Discover"};

    private FragmentCommunityBinding binding;
    private CommunityPostAdapter postAdapter;
    private String currentTopTab = "Posts";
    private String currentDiscoverSection = "Blog";
    private boolean fabExpanded = false;
    private final List<ApiReel> allReels = new ArrayList<>();
    private String currentReelSource = null; // null = "Tất cả"
    private final List<CommunityPost> allPosts = new ArrayList<>();
    private boolean sortPopular = false;
    private boolean followingOnly = false;

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
        setupTabSwipeGesture();
        setupFeedRecyclerView();
        setupRefreshLayout();
        setupFabs();
        setupDiscoverToggle();
        setupReelSourceFilter();
        setupStories();
        setupLocationTag();
        setupPostsFilter();
        setupHeaderIcons();

        loadReels();
        loadBlogs();
        loadTips();
        loadAllPosts();
    }

    /** Avatar nếu đã đăng nhập, icon khách nếu chưa; chuông thông báo mở màn Thông báo thật. */
    private void setupHeaderIcons() {
        binding.btnNotifications.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), com.pompom.group6.activities.NotificationActivity.class)));
        // Đã đăng nhập -> tab Me hiển thị Profile; chưa đăng nhập -> tab Me hiển thị màn đăng nhập.
        binding.ivUserAvatar.setOnClickListener(v -> {
            if (getActivity() instanceof com.pompom.group6.MainActivity) {
                ((com.pompom.group6.MainActivity) getActivity()).switchToTab(4);
            }
        });
        updateUserAvatar();
        updateNotificationBadge();
    }

    /** Badge chuông = số thông báo CHƯA đọc thật của user (trước đây bị gán cứng "3").
     * Ẩn hẳn khi chưa đăng nhập hoặc không có thông báo mới; hiện "9+" khi vượt 9. */
    private void updateNotificationBadge() {
        if (binding == null) return;
        String userOid = com.pompom.group6.network.Session.getUserOid(requireContext());
        if (userOid == null) {
            binding.tvNotificationBadge.setVisibility(View.GONE);
            return;
        }
        com.pompom.group6.network.ApiClient.get().getNotifications(userOid)
                .enqueue(new retrofit2.Callback<List<com.pompom.group6.network.dto.ApiNotification>>() {
                    @Override
                    public void onResponse(retrofit2.Call<List<com.pompom.group6.network.dto.ApiNotification>> call,
                                           retrofit2.Response<List<com.pompom.group6.network.dto.ApiNotification>> resp) {
                        if (binding == null) return;
                        int unread = 0;
                        if (resp.isSuccessful() && resp.body() != null) {
                            for (com.pompom.group6.network.dto.ApiNotification n : resp.body()) {
                                if (!n.isRead) unread++;
                            }
                        }
                        if (unread <= 0) {
                            binding.tvNotificationBadge.setVisibility(View.GONE);
                        } else {
                            binding.tvNotificationBadge.setText(unread > 9 ? "9+" : String.valueOf(unread));
                            binding.tvNotificationBadge.setVisibility(View.VISIBLE);
                        }
                    }
                    @Override public void onFailure(retrofit2.Call<List<com.pompom.group6.network.dto.ApiNotification>> call, Throwable t) {}
                });
    }

    /** Lấy avatar mới nhất từ backend (giống HomeFragment) thay vì dùng bản lưu tạm lúc đăng nhập,
     * để tránh hiển thị avatar rỗng/cũ khi user đã đổi avatar sau khi đăng nhập. */
    private void updateUserAvatar() {
        if (binding == null) return;
        String userOid = com.pompom.group6.network.Session.getUserOid(requireContext());
        if (userOid == null) {
            resetToDefaultAvatarIcon();
            return;
        }
        com.pompom.group6.network.ApiClient.get().getUser(userOid)
                .enqueue(new retrofit2.Callback<com.pompom.group6.network.dto.ApiUser>() {
                    @Override
                    public void onResponse(retrofit2.Call<com.pompom.group6.network.dto.ApiUser> call,
                                           retrofit2.Response<com.pompom.group6.network.dto.ApiUser> resp) {
                        if (binding == null) return;
                        String avatarUrl = resp.isSuccessful() && resp.body() != null ? resp.body().avatarUrl : null;
                        // Bỏ tint trắng (chỉ dùng cho icon khách) trước khi nạp ảnh đại diện thật.
                        androidx.core.widget.ImageViewCompat.setImageTintList(binding.ivUserAvatar, null);
                        com.bumptech.glide.Glide.with(CommunityFragment.this)
                                .load(avatarUrl)
                                .circleCrop()
                                .placeholder(R.drawable.ic_user2)
                                .error(R.drawable.ic_user2)
                                .into(binding.ivUserAvatar);
                    }
                    @Override public void onFailure(retrofit2.Call<com.pompom.group6.network.dto.ApiUser> call, Throwable t) {}
                });
    }

    /** ic_user2 vốn cùng tông màu hồng với nền header -> tint trắng để icon khách hiện rõ. */
    private void resetToDefaultAvatarIcon() {
        binding.ivUserAvatar.setImageResource(R.drawable.ic_user2);
        androidx.core.widget.ImageViewCompat.setImageTintList(binding.ivUserAvatar,
                androidx.core.content.ContextCompat.getColorStateList(requireContext(), R.color.white));
    }

    @Override
    public void onResume() {
        super.onResume();
        // Cập nhật lại avatar phòng khi user đăng nhập/đổi avatar ở tab khác.
        updateUserAvatar();
        // Tải lại feed "Bài viết" để trạng thái thích/lưu luôn đúng với người dùng hiện
        // tại — tránh giữ liked=true của lần tải trước đó sau khi đăng xuất/đăng nhập tài
        // khoản khác ở tab Me trong lúc CommunityFragment vẫn còn sống trong ViewPager2.
        loadAllPosts();
        // AddStoryActivity chỉ finish() sau khi đăng xong, không báo kết quả về —
        // tải lại hàng story mỗi khi quay lại màn để story mới đăng hiện ra ngay.
        if (LocationHelper.hasPermission(requireContext())) {
            fetchNearbyStories();
        }
    }

    // ── Tab trên cùng: Bài viết / Thước phim / Khám phá ──

    private void setupTopTabs() {
        binding.tabPosts.setOnClickListener(v -> selectTopTab("Posts", binding.tabPosts));
        binding.tabReels.setOnClickListener(v -> selectTopTab("Reels", binding.tabReels));
        binding.tabDiscover.setOnClickListener(v -> selectTopTab("Discover", binding.tabDiscover));
    }

    private void selectTopTab(String tab, TextView tabView) {
        if (currentTopTab.equals(tab)) return;
        collapseSpeedDial();

        resetTabUI(binding.tabPosts);
        resetTabUI(binding.tabReels);
        resetTabUI(binding.tabDiscover);

        tabView.setTextColor(getResources().getColor(R.color.white));
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
        tabView.setTextColor(getResources().getColor(R.color.brand_pink_light));
        tabView.setTypeface(null, android.graphics.Typeface.NORMAL);
    }

    /** Chuyển panel kèm hiệu ứng mờ dần (crossfade) cho mượt thay vì đổi visibility đột ngột. */
    private void showPanel(String tab) {
        View next = "Posts".equals(tab) ? binding.postsTab
                : "Reels".equals(tab) ? binding.panelReels
                : binding.panelDiscover;

        for (View panel : new View[]{binding.postsTab, binding.panelReels, binding.panelDiscover}) {
            if (panel == next) {
                panel.setVisibility(View.VISIBLE);
                panel.setAlpha(0f);
                panel.animate().alpha(1f).setDuration(200).setListener(null).start();
            } else if (panel.getVisibility() == View.VISIBLE) {
                panel.animate().alpha(0f).setDuration(150)
                        .setListener(new android.animation.AnimatorListenerAdapter() {
                            @Override public void onAnimationEnd(android.animation.Animator animation) {
                                panel.setVisibility(View.GONE);
                                panel.setAlpha(1f);
                            }
                        }).start();
            }
        }
    }

    /** Vuốt ngang trên nội dung để chuyển qua lại 3 tab, giống lướt trang. */
    private void setupTabSwipeGesture() {
        binding.tabContent.setExcludedView(binding.rvStories);
        binding.tabContent.setOnSwipeListener(new SwipeTabFrameLayout.OnSwipeListener() {
            @Override public void onSwipeLeft() { moveTab(1); }
            @Override public void onSwipeRight() { moveTab(-1); }
        });
    }

    private void moveTab(int delta) {
        int index = Arrays.asList(TAB_ORDER).indexOf(currentTopTab) + delta;
        if (index < 0 || index >= TAB_ORDER.length) return;
        String tab = TAB_ORDER[index];
        selectTopTab(tab, tabViewFor(tab));
    }

    private TextView tabViewFor(String tab) {
        switch (tab) {
            case "Posts": return binding.tabPosts;
            case "Reels": return binding.tabReels;
            default: return binding.tabDiscover;
        }
    }

    // ── Thước phim: video đăng lại từ Instagram/Facebook/TikTok ──

    private void loadReels() {
        binding.rvReels.setLayoutManager(new LinearLayoutManager(requireContext()));
        com.pompom.group6.utils.BottomNavScrollHelper.attach(binding.rvReels, this);
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

    private void applyReelSourceFilter() {
        if (binding == null) return;
        List<ApiReel> filtered = new ArrayList<>();
        for (ApiReel r : allReels) {
            if (currentReelSource == null || currentReelSource.equalsIgnoreCase(r.source)) filtered.add(r);
        }
        binding.rvReels.setAdapter(new ReelAdapter(filtered));
        binding.tvReelsEmpty.setVisibility(filtered.isEmpty() ? View.VISIBLE : View.GONE);
    }

    // ── Khám phá: Blog thương hiệu / Tips bác sĩ (chuyển bằng chip) ──

    private void setupDiscoverToggle() {
        binding.chipDiscoverBlog.setOnClickListener(v -> selectDiscoverSection("Blog"));
        binding.chipDiscoverTips.setOnClickListener(v -> selectDiscoverSection("Tips"));
    }

    private void selectDiscoverSection(String section) {
        if (currentDiscoverSection.equals(section)) return;
        currentDiscoverSection = section;
        boolean blog = "Blog".equals(section);
        binding.groupBlog.setVisibility(blog ? View.VISIBLE : View.GONE);
        binding.groupTips.setVisibility(blog ? View.GONE : View.VISIBLE);
        setChipSelected(binding.chipDiscoverBlog, blog);
        setChipSelected(binding.chipDiscoverTips, !blog);
    }

    private void setChipSelected(TextView chip, boolean selected) {
        chip.setBackgroundResource(selected ? R.drawable.bg_label_pink : R.drawable.bg_white_pill_button);
        chip.setTextColor(getResources().getColor(selected ? R.color.white : R.color.text_secondary));
    }

    /** Hiện thông báo trạng thái (đang tải / rỗng / lỗi) và ẩn danh sách. */
    private void showPanelStatus(TextView status, View list, String text) {
        if (binding == null) return;
        status.setText(text);
        status.setVisibility(View.VISIBLE);
        list.setVisibility(View.GONE);
    }

    /** Hiện danh sách và ẩn thông báo trạng thái. */
    private void showPanelList(TextView status, View list) {
        if (binding == null) return;
        status.setVisibility(View.GONE);
        list.setVisibility(View.VISIBLE);
    }

    // ── Blog thương hiệu ──

    private void loadBlogs() {
        binding.rvBlogFull.setLayoutManager(new LinearLayoutManager(requireContext()));
        com.pompom.group6.utils.BottomNavScrollHelper.attach(binding.rvBlogFull, this);
        showPanelStatus(binding.tvBlogStatus, binding.rvBlogFull, "Đang tải...");
        com.pompom.group6.network.ApiClient.get().getBlogs(50)
                .enqueue(new retrofit2.Callback<List<ApiBlog>>() {
                    @Override
                    public void onResponse(retrofit2.Call<List<ApiBlog>> call, retrofit2.Response<List<ApiBlog>> resp) {
                        if (binding == null) return;
                        if (!resp.isSuccessful() || resp.body() == null) {
                            showPanelStatus(binding.tvBlogStatus, binding.rvBlogFull, "Không tải được blog, vui lòng thử lại sau");
                            return;
                        }
                        if (resp.body().isEmpty()) {
                            showPanelStatus(binding.tvBlogStatus, binding.rvBlogFull, "Chưa có blog nào");
                            return;
                        }
                        binding.rvBlogFull.setAdapter(new BlogAdapter(resp.body(), true));
                        showPanelList(binding.tvBlogStatus, binding.rvBlogFull);
                    }
                    @Override public void onFailure(retrofit2.Call<List<ApiBlog>> call, Throwable t) {
                        if (binding != null) showPanelStatus(binding.tvBlogStatus, binding.rvBlogFull, "Không kết nối được máy chủ");
                    }
                });
    }

    // ── Tips từ bác sĩ tư vấn ──

    private void loadTips() {
        binding.rvTipsFull.setLayoutManager(new LinearLayoutManager(requireContext()));
        com.pompom.group6.utils.BottomNavScrollHelper.attach(binding.rvTipsFull, this);
        showPanelStatus(binding.tvTipsStatus, binding.rvTipsFull, "Đang tải...");
        com.pompom.group6.network.ApiClient.get().getExpertArticles(50)
                .enqueue(new retrofit2.Callback<List<ApiExpertArticle>>() {
                    @Override
                    public void onResponse(retrofit2.Call<List<ApiExpertArticle>> call,
                                           retrofit2.Response<List<ApiExpertArticle>> resp) {
                        if (binding == null) return;
                        if (!resp.isSuccessful() || resp.body() == null) {
                            showPanelStatus(binding.tvTipsStatus, binding.rvTipsFull, "Không tải được tips, vui lòng thử lại sau");
                            return;
                        }
                        if (resp.body().isEmpty()) {
                            showPanelStatus(binding.tvTipsStatus, binding.rvTipsFull, "Chưa có tips nào");
                            return;
                        }
                        binding.rvTipsFull.setAdapter(new ExpertArticleAdapter(resp.body(), true));
                        showPanelList(binding.tvTipsStatus, binding.rvTipsFull);
                    }
                    @Override public void onFailure(retrofit2.Call<List<ApiExpertArticle>> call, Throwable t) {
                        if (binding != null) showPanelStatus(binding.tvTipsStatus, binding.rvTipsFull, "Không kết nối được máy chủ");
                    }
                });
    }

    /** Feed "Bài viết": tải mọi bài viết cộng đồng mới nhất, lưu lại rồi áp bộ lọc/sắp xếp hiện tại
     * cục bộ — không gọi lại API mỗi khi đổi lọc (cùng cách tiếp cận với bộ lọc nguồn ở tab Reels). */
    private void loadAllPosts() {
        String userOid = com.pompom.group6.network.Session.getUserOid(requireContext());
        com.pompom.group6.network.ApiClient.get().searchCommunityPosts(50, null, userOid)
                .enqueue(new retrofit2.Callback<List<com.pompom.group6.network.dto.ApiCommunityPost>>() {
                    @Override
                    public void onResponse(retrofit2.Call<List<com.pompom.group6.network.dto.ApiCommunityPost>> call,
                                           retrofit2.Response<List<com.pompom.group6.network.dto.ApiCommunityPost>> resp) {
                        if (binding == null || !resp.isSuccessful() || resp.body() == null) return;
                        allPosts.clear();
                        allPosts.addAll(mapPosts(resp.body()));
                        applyPostsFilter();
                    }
                    @Override public void onFailure(retrofit2.Call<List<com.pompom.group6.network.dto.ApiCommunityPost>> call, Throwable t) {}
                });
    }

    /** Áp lọc "Đang theo dõi" + sắp xếp "Phổ biến nhất" (theo lượt thích) lên danh sách đã tải. */
    private void applyPostsFilter() {
        List<CommunityPost> filtered = new ArrayList<>();
        for (CommunityPost p : allPosts) {
            if (followingOnly && !Boolean.TRUE.equals(p.getFollowing())) continue;
            filtered.add(p);
        }
        if (sortPopular) {
            filtered.sort((a, b) -> Integer.compare(b.getLikeCount(), a.getLikeCount()));
        }
        String emptyText = followingOnly ? "Bạn chưa theo dõi ai có bài viết phù hợp" : "Chưa có bài viết nào";
        bindPostList(filtered, emptyText);
    }

    /** Mở bộ lọc bài viết (thay cho ô tìm kiếm cũ) — sắp xếp Mới nhất/Phổ biến nhất, hiện Tất cả/Đang theo dõi. */
    private void setupPostsFilter() {
        binding.btnPostsFilter.setOnClickListener(v ->
                CommunityFilterBottomSheet.show(getChildFragmentManager(), sortPopular, followingOnly,
                        (newSortPopular, newFollowingOnly) -> {
                            sortPopular = newSortPopular;
                            followingOnly = newFollowingOnly;
                            updateFilterLabel();
                            applyPostsFilter();
                        }));
    }

    /** Đổi màu/nhãn nút Lọc để phản ánh đang có bộ lọc khác mặc định hay không. */
    private void updateFilterLabel() {
        if (binding == null) return;
        boolean active = sortPopular || followingOnly;
        if (!active) {
            binding.tvPostsFilterLabel.setText("Lọc");
        } else if (sortPopular && followingOnly) {
            binding.tvPostsFilterLabel.setText("Lọc • Phổ biến, Đang theo dõi");
        } else if (sortPopular) {
            binding.tvPostsFilterLabel.setText("Lọc • Phổ biến nhất");
        } else {
            binding.tvPostsFilterLabel.setText("Lọc • Đang theo dõi");
        }
        binding.tvPostsFilterLabel.setTextColor(getResources().getColor(
                active ? R.color.brand_pink : R.color.text_primary));
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
            cp.setAuthorId(a.userId);
            cp.setShareCount(a.shareCount);
            cp.setCreatedAt(a.createdAt);
            cp.setFollowing(a.isFollowing);
            if (a.previewComments != null) {
                List<CommunityPost.PreviewComment> pc = new java.util.ArrayList<>();
                for (com.pompom.group6.network.dto.ApiCommunityPost.PreviewComment c : a.previewComments) {
                    pc.add(new CommunityPost.PreviewComment(c.authorName, c.content));
                }
                cp.setPreviewComments(pc);
            }
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
            loadAllPosts();
            binding.swipeRefresh.postDelayed(() -> binding.swipeRefresh.setRefreshing(false), 1000);
        });
    }

    /** Nút tròn nổi: bấm để nảy lên 2 lựa chọn "Tạo bài viết" / "Liên hệ tư vấn". */
    private void setupFabs() {
        // Vài thiết bị/theme Material3 vẫn tự áp animator đổi elevation khi nhấn/giữ dù đã khai
        // báo app:elevation="0dp" trong XML — tắt hẳn bằng code để bóng không còn "giới hạn cứng".
        binding.fabMain.setStateListAnimator(null);
        binding.fabAddPost.setStateListAnimator(null);
        binding.fabConsult.setStateListAnimator(null);

        binding.fabMain.setOnClickListener(v -> {
            if (fabExpanded) collapseSpeedDial(); else expandSpeedDial();
        });
        View.OnClickListener addPostListener = v -> {
            collapseSpeedDial();
            startActivity(new Intent(requireContext(), AddCommunityPostActivity.class));
        };
        View.OnClickListener consultListener = v -> {
            collapseSpeedDial();
            startActivity(new Intent(requireContext(), ConsultationRequestActivity.class));
        };
        // Nhãn chữ ("Tạo bài viết"/"Liên hệ tư vấn") trước đây không có listener riêng —
        // chạm vào nhãn (thay vì đúng nút tròn nhỏ bên cạnh) bị lọt xuống bài viết bên dưới.
        binding.fabAddPost.setOnClickListener(addPostListener);
        binding.rowCreatePost.setOnClickListener(addPostListener);
        binding.fabConsult.setOnClickListener(consultListener);
        binding.rowConsult.setOnClickListener(consultListener);

        setupFabDrag();
        applyBottomNavClearance();
    }

    /** Cụm FAB trước đây nằm khuất một phần sau thanh nav dưới (pill nổi cao ~80dp + khoảng nổi
     * 4dp của MainActivity) — khi thanh nav đang hiện, chạm vào "Tạo bài viết"/"Liên hệ tư vấn"
     * lọt xuống bài viết bên dưới thay vì trúng nút. Đẩy hẳn cụm FAB lên trên thanh nav bằng
     * margin đáy tính theo inset hệ thống, độc lập với việc nav đang ẩn hay hiện. */
    private void applyBottomNavClearance() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.speedDialGroup, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            float density = getResources().getDisplayMetrics().density;
            int navBarClearance = (int) (84 * density);
            int baseMargin = (int) (24 * density);
            ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) v.getLayoutParams();
            params.bottomMargin = baseMargin + navBarClearance + bars.bottom;
            v.setLayoutParams(params);
            return insets;
        });
        ViewCompat.requestApplyInsets(binding.speedDialGroup);
    }

    /** Kéo-thả cả cụm nút nổi đi bất kỳ đâu trên màn hình — nhấn nhẹ (không kéo) vẫn mở/đóng dial. */
    private void setupFabDrag() {
        binding.fabMain.setOnTouchListener(new View.OnTouchListener() {
            private float dX, dY, startX, startY;
            private static final int CLICK_THRESHOLD = 10;

            @Override
            public boolean onTouch(View view, android.view.MotionEvent event) {
                View group = binding.speedDialGroup;
                switch (event.getAction()) {
                    case android.view.MotionEvent.ACTION_DOWN:
                        dX = group.getTranslationX() - event.getRawX();
                        dY = group.getTranslationY() - event.getRawY();
                        startX = event.getRawX();
                        startY = event.getRawY();
                        return true;
                    case android.view.MotionEvent.ACTION_MOVE:
                        group.setTranslationX(event.getRawX() + dX);
                        group.setTranslationY(event.getRawY() + dY);
                        return true;
                    case android.view.MotionEvent.ACTION_UP:
                        float diffX = Math.abs(event.getRawX() - startX);
                        float diffY = Math.abs(event.getRawY() - startY);
                        if (diffX < CLICK_THRESHOLD && diffY < CLICK_THRESHOLD) {
                            view.performClick();
                        }
                        return true;
                    default:
                        return false;
                }
            }
        });
    }

    private void expandSpeedDial() {
        fabExpanded = true;
        binding.fabMain.animate().rotation(45f).setDuration(200).start();
        bounceIn(binding.rowCreatePost, 0);
        bounceIn(binding.rowConsult, 60);
    }

    private void collapseSpeedDial() {
        if (!fabExpanded) return;
        fabExpanded = false;
        binding.fabMain.animate().rotation(0f).setDuration(150).start();
        bounceOut(binding.rowCreatePost);
        bounceOut(binding.rowConsult);
    }

    private void bounceIn(View row, long startDelay) {
        row.setVisibility(View.VISIBLE);
        row.setAlpha(0f);
        row.setScaleX(0.4f);
        row.setScaleY(0.4f);
        row.setTranslationY(28f);
        row.animate()
                .alpha(1f).scaleX(1f).scaleY(1f).translationY(0f)
                .setStartDelay(startDelay)
                .setDuration(260)
                .setInterpolator(new OvershootInterpolator(2.4f))
                .start();
    }

    private void bounceOut(View row) {
        row.animate()
                .alpha(0f).scaleX(0.4f).scaleY(0.4f).translationY(28f)
                .setDuration(140)
                .withEndAction(() -> row.setVisibility(View.INVISIBLE))
                .start();
    }

    private void setupFeedRecyclerView() {
        postAdapter = new CommunityPostAdapter();
        binding.rvFeed.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvFeed.setAdapter(postAdapter);
        com.pompom.group6.utils.BottomNavScrollHelper.attach(binding.rvFeed, this);
    }

    /** Hàng Story 24h theo bán kính GPS — luôn có ô "Đăng story" đầu tiên. Gọi ngay lúc mở màn
     * vì Story giờ nằm trong tab "Bài viết" mặc định (đã gộp từ tab "Gần đây" cũ). */
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

    /** Vị trí GPS thật của máy — dùng để xem story/bài viết quanh nơi mình đang đứng. */
    private void fetchNearbyStories() {
        LocationHelper.getCurrentLocation(requireContext(), new LocationHelper.Callback() {
            @Override
            public void onLocation(double lat, double lng) {
                resolvePlaceName(lat, lng);
                fetchNearbyStoriesAt(lat, lng);
            }

            @Override
            public void onUnavailable() {
                showStoriesAddButtonOnly();
            }
        });
    }

    private void fetchNearbyStoriesAt(double lat, double lng) {
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

    private static final String[] PRESET_CITY_NAMES = {
            "TP. Hồ Chí Minh", "Hà Nội", "Đà Nẵng", "Hải Phòng", "Cần Thơ", "Nha Trang"
    };
    private static final double[][] PRESET_CITY_COORDS = {
            {10.7769, 106.7009},
            {21.0278, 105.8342},
            {16.0544, 108.2022},
            {20.8449, 106.6881},
            {10.0452, 105.7469},
            {12.2388, 109.1967},
    };
    private boolean locationPickedManually = false;

    /** Tag vị trí ở đầu tab "Bài viết" — bấm để chọn khu vực khác và xem story quanh khu vực đó
     * thay vì GPS thật của máy. */
    private void setupLocationTag() {
        binding.btnChangeLocation.setOnClickListener(v -> {
            // Tick sẵn khu vực đang xem (nếu đang khớp một thành phố trong danh sách).
            int current = -1;
            String shown = binding.tvLocationTag.getText() != null ? binding.tvLocationTag.getText().toString() : "";
            for (int i = 0; i < PRESET_CITY_NAMES.length; i++) {
                if (PRESET_CITY_NAMES[i].equals(shown)) { current = i; break; }
            }
            com.pompom.group6.utils.PomPomDialog.pickList(requireContext(),
                    "Chọn khu vực xem bài viết", PRESET_CITY_NAMES, current, this::selectCity);
        });
    }

    private void selectCity(int index) {
        if (binding == null) return;
        locationPickedManually = true;
        binding.tvLocationTag.setText(PRESET_CITY_NAMES[index]);
        fetchNearbyStoriesAt(PRESET_CITY_COORDS[index][0], PRESET_CITY_COORDS[index][1]);
    }

    /** Dịch toạ độ GPS thật ra tên khu vực để hiển thị lên tag — chạy nền vì Geocoder gọi mạng;
     * bỏ qua nếu người dùng đã tự chọn khu vực khác trong lúc chờ kết quả. */
    private void resolvePlaceName(double lat, double lng) {
        android.content.Context ctx = getContext();
        if (ctx == null) return;
        new Thread(() -> {
            String placeName = null;
            try {
                android.location.Geocoder geocoder = new android.location.Geocoder(ctx, new java.util.Locale("vi"));
                @SuppressWarnings("deprecation")
                List<android.location.Address> results = geocoder.getFromLocation(lat, lng, 1);
                if (results != null && !results.isEmpty()) {
                    placeName = formatAddress(results.get(0));
                }
            } catch (Exception ignored) {
                // Không có mạng/dịch vụ geocode — giữ tag ở giá trị mặc định.
            }
            if (placeName == null || getActivity() == null) return;
            String display = placeName;
            getActivity().runOnUiThread(() -> {
                if (binding != null && !locationPickedManually) binding.tvLocationTag.setText(display);
            });
        }).start();
    }

    /** Ghép quận/huyện + thành phố để ra địa điểm cụ thể (vd "Quận 1, TP. Hồ Chí Minh") thay vì
     * chỉ tên thành phố chung chung — đúng với toạ độ GPS thật vừa lấy được. */
    private String formatAddress(android.location.Address addr) {
        String district = addr.getSubAdminArea();
        String city = addr.getLocality() != null ? addr.getLocality() : addr.getAdminArea();
        if (district != null && city != null && !district.equalsIgnoreCase(city)) {
            return district + ", " + city;
        }
        if (city != null) return city;
        return district;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
