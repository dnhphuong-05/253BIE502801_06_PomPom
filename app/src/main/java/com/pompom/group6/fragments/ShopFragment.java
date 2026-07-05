package com.pompom.group6.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.bumptech.glide.Glide;
import com.pompom.group6.R;
import com.pompom.group6.activities.CartActivity;
import com.pompom.group6.activities.GuestOrderActivity;
import com.pompom.group6.activities.SearchActivity;
import com.pompom.group6.adapters.CategoryAdapter;
import com.pompom.group6.adapters.ProductAdapter;
import com.pompom.group6.database.CategoryDAO;
import com.pompom.group6.database.ProductDAO;
import com.pompom.group6.database.PromotionDAO;
import com.pompom.group6.database.UserDAO;
import com.pompom.group6.databinding.FragmentShopBinding;
import com.pompom.group6.models.Category;
import com.pompom.group6.models.FilterState;
import com.pompom.group6.models.Product;
import com.pompom.group6.models.User;
import com.pompom.group6.utils.CartManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ShopFragment extends Fragment implements CartManager.CartChangeListener {

    private FragmentShopBinding binding;
    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;
    private PromotionDAO promotionDAO;
    private CartManager cartManager;

    private ProductAdapter productAdapter;
    private CategoryAdapter categoryAdapter;
    // Ánh xạ index chip danh mục -> ObjectId thật (để lọc sản phẩm theo danh mục qua API).
    private final java.util.Map<Integer, String> categoryOidByIndex = new java.util.HashMap<>();

    // Pagination
    private int currentPage = 0;
    private final int pageSize = 8;
    private boolean isLastPage = false;
    private boolean isLoading = false;

    // View mode: false = 2-col grid (default), true = 1-col list
    private boolean isHorizontal = false;

    // Multi-select category + filter state (MODULE 5 / TASK 5)
    private final Set<Integer> selectedCategoryIds = new HashSet<>();
    private FilterState currentFilter = new FilterState();

    // ── Lifecycle ─────────────────────────────────────────────────────────

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentShopBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        productDAO  = new ProductDAO(requireContext());
        categoryDAO = new CategoryDAO(requireContext());
        promotionDAO = new PromotionDAO(requireContext());
        cartManager = CartManager.getInstance(requireContext());
        cartManager.addListener(this);

        setupCartIcon();
        setupSearchIcon();
        setupMarquee();
        setupProfileAvatar();
        setupGuestOrderFab();
        setupCategories();
        setupProducts();
        setupGridToggle();
        setupFilterButton();
        setupPagination();

        reloadProductsFiltered();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Refresh avatar in case user logged in/out
        setupProfileAvatar();
        setupGuestOrderFab();
        if (cartManager != null) updateCartBadge(cartManager.getTotalCount());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (cartManager != null) cartManager.removeListener(this);
        binding = null;
    }

    // ── YC2: Circular avatar ──────────────────────────────────────────────

    private void setupProfileAvatar() {
        if (binding == null) return;
        String userOid = com.pompom.group6.network.Session.getUserOid(requireContext());
        if (userOid == null) {
            resetToDefaultProfileIcon();
            return;
        }
        com.pompom.group6.network.ApiClient.get().getUser(userOid)
                .enqueue(new retrofit2.Callback<com.pompom.group6.network.dto.ApiUser>() {
                    @Override
                    public void onResponse(retrofit2.Call<com.pompom.group6.network.dto.ApiUser> call,
                                           retrofit2.Response<com.pompom.group6.network.dto.ApiUser> resp) {
                        if (binding == null) return;
                        com.pompom.group6.network.dto.ApiUser user = resp.isSuccessful() ? resp.body() : null;
                        if (user != null && user.avatarUrl != null && !user.avatarUrl.isEmpty()) {
                            Glide.with(ShopFragment.this).load(user.avatarUrl).circleCrop()
                                    .placeholder(R.drawable.ic_user2).into(binding.ivProfile);
                            ImageViewCompat.setImageTintList(binding.ivProfile, null);
                        } else {
                            resetToDefaultProfileIcon();
                        }
                    }
                    @Override public void onFailure(retrofit2.Call<com.pompom.group6.network.dto.ApiUser> call, Throwable t) {
                        resetToDefaultProfileIcon();
                    }
                });
    }

    private void resetToDefaultProfileIcon() {
        if (binding == null) return;
        binding.ivProfile.setImageResource(R.drawable.ic_user2);
        ImageViewCompat.setImageTintList(binding.ivProfile,
                ContextCompat.getColorStateList(requireContext(), R.color.brand_background));
    }

    // ── Guest order FAB — only shown to non-logged-in users ──────────────────

    private void setupGuestOrderFab() {
        if (binding == null) return;
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);
        if (isLoggedIn) {
            binding.fabGuestOrder.setVisibility(View.GONE);
        } else {
            binding.fabGuestOrder.setVisibility(View.VISIBLE);
            binding.fabGuestOrder.setOnClickListener(v -> {
                Intent intent = new Intent(requireContext(), GuestOrderActivity.class);
                startActivity(intent);
            });
        }
    }

    // ── Cart icon + badge ─────────────────────────────────────────────────

    private void setupCartIcon() {
        binding.ivCart.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), CartActivity.class);
            startActivity(intent);
            requireActivity().overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });
        updateCartBadge(cartManager.getTotalCount());
    }

    private void setupSearchIcon() {
        binding.ivSearch.setOnClickListener(v ->
                startActivity(new Intent(requireContext(), SearchActivity.class)));
    }

    private void updateCartBadge(int count) {
        if (binding == null || binding.tvCartBadge == null) return;
        if (count > 0) {
            binding.tvCartBadge.setVisibility(View.VISIBLE);
            binding.tvCartBadge.setText(count > 99 ? "99+" : String.valueOf(count));
        } else {
            binding.tvCartBadge.setVisibility(View.GONE);
        }
    }

    @Override
    public void onCartChanged(int totalCount) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> updateCartBadge(totalCount));
        }
    }

    // ── Promo marquee ─────────────────────────────────────────────────────

    private void setupMarquee() {
        String[] messages = {
                "Miễn phí vận chuyển cho đơn từ 299K",
                "Giảm đến 50% cho bộ sưu tập Unicorn Magic",
                "Nhập mã WELCOME20 giảm 20% đơn đầu tiên",
        };
        StringBuilder sb = new StringBuilder();
        for (String msg : messages) sb.append(msg).append("   ✦   ");
        binding.tvPromoMarquee.setText(sb.toString());
        binding.tvPromoMarquee.setSelected(true);
    }

    // ── Categories (MODULE 3 multi-select) ───────────────────────────────

    private void setupCategories() {
        // Danh mục lấy từ MongoDB. (Lọc sản phẩm theo danh mục sẽ nối API sau.)
        com.pompom.group6.network.ApiClient.get().getCategories()
                .enqueue(new retrofit2.Callback<List<com.pompom.group6.network.dto.ApiCategory>>() {
                    @Override
                    public void onResponse(retrofit2.Call<List<com.pompom.group6.network.dto.ApiCategory>> call,
                                           retrofit2.Response<List<com.pompom.group6.network.dto.ApiCategory>> resp) {
                        if (binding == null || !resp.isSuccessful() || resp.body() == null) return;
                        List<Category> categories = new java.util.ArrayList<>();
                        categoryOidByIndex.clear();
                        int idx = 1;
                        for (com.pompom.group6.network.dto.ApiCategory a : resp.body()) {
                            categoryOidByIndex.put(idx, a.id);
                            categories.add(new Category(idx++, a.categoryName, a.imageUrl));
                        }
                        bindCategories(categories);
                    }
                    @Override public void onFailure(retrofit2.Call<List<com.pompom.group6.network.dto.ApiCategory>> call, Throwable t) {}
                });
    }

    private void bindCategories(List<Category> categories) {
        categoryAdapter = new CategoryAdapter(categories, (categoryId, isSelected) -> {
            // TASK 5: Multi-select → reload products
            if (isSelected) {
                selectedCategoryIds.add(categoryId);
            } else {
                selectedCategoryIds.remove(categoryId);
            }
            // Sync into currentFilter so FilterSheet shows correct state
            currentFilter.categoryIds = new HashSet<>(selectedCategoryIds);
            updateClearFilterVisibility();
            reloadProductsFiltered();
        });

        binding.rvCategories.setAdapter(categoryAdapter);
    }

    // ── Products RecyclerView ─────────────────────────────────────────────

    private void setupProducts() {
        productAdapter = new ProductAdapter();
        GridLayoutManager lm = buildGridLayoutManager(2);
        binding.rvProducts.setLayoutManager(lm);
        binding.rvProducts.setAdapter(productAdapter);
        binding.rvProducts.setNestedScrollingEnabled(false);
    }

    private GridLayoutManager buildGridLayoutManager(int spanCount) {
        GridLayoutManager lm = new GridLayoutManager(requireContext(), spanCount);
        lm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                int type = productAdapter.getItemViewType(position);
                // VIEW_TYPE_LOADING=2, VIEW_TYPE_HORIZONTAL=1 → full width
                return (type == 1 || type == 2) ? spanCount : 1;
            }
        });
        return lm;
    }

    // ── MODULE 2: Grid / Row toggle ───────────────────────────────────────

    private void setupGridToggle() {
        // Default: grid mode (2-col, isHorizontal=false)
        // ivGridView = ic_gridview1 (selected), ivRowView = ic_rowview0 (unselected)
        binding.ivGridView.setImageResource(R.drawable.ic_gridview1);
        binding.ivRowView.setImageResource(R.drawable.ic_rowview0);

        // Click ivRowView → 1-col list mode
        binding.ivRowView.setOnClickListener(v -> {
            if (isHorizontal) return; // already in row mode
            isHorizontal = true;
            binding.ivRowView.setImageResource(R.drawable.ic_rowview1);
            binding.ivGridView.setImageResource(R.drawable.ic_gridview0);

            productAdapter.setHorizontal(true);
            binding.rvProducts.setLayoutManager(buildGridLayoutManager(1));
            productAdapter.notifyDataSetChanged();
        });

        // Click ivGridView → 2-col grid mode
        binding.ivGridView.setOnClickListener(v -> {
            if (!isHorizontal) return; // already in grid mode
            isHorizontal = false;
            binding.ivGridView.setImageResource(R.drawable.ic_gridview1);
            binding.ivRowView.setImageResource(R.drawable.ic_rowview0);

            productAdapter.setHorizontal(false);
            binding.rvProducts.setLayoutManager(buildGridLayoutManager(2));
            productAdapter.notifyDataSetChanged();
        });
    }

    // ── MODULE 5: Filter sheet ────────────────────────────────────────────

    private void setupFilterButton() {
        binding.layoutFilter.setOnClickListener(v -> showFilterSheet());

        // "| Xóa" button clears all filters
        if (binding.tvClearFilter != null) {
            binding.tvClearFilter.setOnClickListener(v -> {
                currentFilter.reset();
                selectedCategoryIds.clear();
                categoryAdapter.clearSelection();
                updateClearFilterVisibility();
                reloadProductsFiltered();
            });
        }
    }

    private void showFilterSheet() {
        FilterSheetFragment sheet = FilterSheetFragment.newInstance(currentFilter);
        sheet.setOnFilterAppliedListener(state -> {
            currentFilter = state;
            // Sync category selections back to the horizontal strip
            selectedCategoryIds.clear();
            selectedCategoryIds.addAll(state.categoryIds);
            categoryAdapter.setSelection(selectedCategoryIds);
            updateClearFilterVisibility();
            reloadProductsFiltered();
        });
        getParentFragmentManager()
                .beginTransaction()
                .add(android.R.id.content, sheet, "filter_sheet")
                .addToBackStack("filter_sheet")
                .commit();
    }

    private void updateClearFilterVisibility() {
        if (binding == null || binding.tvClearFilter == null) return;
        binding.tvClearFilter.setVisibility(
                currentFilter.hasActiveFilters() ? View.VISIBLE : View.GONE);
    }

    // ── Pagination ────────────────────────────────────────────────────────

    private void setupPagination() {
        binding.nestedScrollView.setOnScrollChangeListener(
                (NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                    View child = v.getChildAt(0);
                    if (child != null
                            && scrollY == child.getMeasuredHeight() - v.getMeasuredHeight()
                            && !isLoading && !isLastPage) {
                        loadMoreProducts();
                    }
                });
    }

    // ── Data loading ──────────────────────────────────────────────────────

    private void reloadProductsFiltered() {
        currentPage = 0;
        // Nạp sản phẩm từ MongoDB, có áp bộ lọc giá/đánh giá/sắp xếp (danh mục sẽ nối sau).
        isLastPage = true;
        Long minPrice = currentFilter.minPrice > 0 ? currentFilter.minPrice : null;
        Long maxPrice = currentFilter.maxPrice > 0 ? currentFilter.maxPrice : null;
        Float minRating = currentFilter.minRating > 0 ? currentFilter.minRating : null;
        String sort = null;
        if ("asc".equals(currentFilter.sortPrice)) sort = "price_asc";
        else if ("desc".equals(currentFilter.sortPrice)) sort = "price_desc";
        else if (!currentFilter.sortAlpha.isEmpty()) sort = "alpha";
        else if (currentFilter.sortNewest) sort = "newest";
        else if (currentFilter.sortPopular) sort = "popular";

        // Danh mục đã chọn (index) -> ObjectId thật, nối bằng dấu phẩy.
        String categoryIds = null;
        if (!currentFilter.categoryIds.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (Integer index : currentFilter.categoryIds) {
                String oid = categoryOidByIndex.get(index);
                if (oid != null) {
                    if (sb.length() > 0) sb.append(",");
                    sb.append(oid);
                }
            }
            if (sb.length() > 0) categoryIds = sb.toString();
        }

        com.pompom.group6.network.ApiClient.get().getProductsFiltered(categoryIds, minPrice, maxPrice, minRating, sort)
                .enqueue(new retrofit2.Callback<List<com.pompom.group6.network.dto.ApiProduct>>() {
                    @Override
                    public void onResponse(retrofit2.Call<List<com.pompom.group6.network.dto.ApiProduct>> call,
                                           retrofit2.Response<List<com.pompom.group6.network.dto.ApiProduct>> resp) {
                        if (binding == null || !resp.isSuccessful() || resp.body() == null) return;
                        List<Product> products = new java.util.ArrayList<>();
                        for (com.pompom.group6.network.dto.ApiProduct a : resp.body()) {
                            products.add(com.pompom.group6.network.ProductMapper.toProduct(a));
                        }
                        productAdapter.setProducts(products);
                    }

                    @Override
                    public void onFailure(retrofit2.Call<List<com.pompom.group6.network.dto.ApiProduct>> call, Throwable t) {
                        // Không tải được → giữ danh sách hiện tại.
                    }
                });
    }

    private void loadMoreProducts() {
        isLoading = true;
        productAdapter.showLoading();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (binding == null) return;
            currentPage++;
            List<Product> more = productDAO.getProductsFiltered(
                    currentFilter.categoryIds.isEmpty() ? null : currentFilter.categoryIds,
                    currentFilter.minPrice,
                    currentFilter.maxPrice,
                    currentFilter.minRating,
                    currentFilter.sortAlpha,
                    currentFilter.sortPrice,
                    currentFilter.sortNewest,
                    currentFilter.sortPopular,
                    pageSize, currentPage * pageSize);

            productAdapter.hideLoading();
            if (more.isEmpty()) {
                isLastPage = true;
            } else {
                productAdapter.addProducts(more);
                if (more.size() < pageSize) isLastPage = true;
            }
            isLoading = false;
        }, 1000);
    }
}
