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
        SharedPreferences prefs = requireContext()
                .getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE);
        boolean isLoggedIn = prefs.getBoolean("is_logged_in", false);
        int userId = prefs.getInt("user_id", -1);

        if (isLoggedIn && userId != -1) {
            UserDAO userDAO = new UserDAO(requireContext());
            User user = userDAO.getUserById(userId);
            if (user != null && user.getAvatarUrl() != null && !user.getAvatarUrl().isEmpty()) {
                Glide.with(this)
                        .load(user.getAvatarUrl())
                        .circleCrop()
                        .placeholder(R.drawable.ic_user2)
                        .into(binding.ivProfile);
                // Remove tint when showing real avatar
                ImageViewCompat.setImageTintList(binding.ivProfile, null);
                return;
            }
        }
        resetToDefaultProfileIcon();
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
        List<String> messages = promotionDAO.getActivePromotionMessages();
        if (!messages.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String msg : messages) sb.append(msg).append("   ✦   ");
            binding.tvPromoMarquee.setText(sb.toString());
            binding.tvPromoMarquee.setSelected(true);
        }
    }

    // ── Categories (MODULE 3 multi-select) ───────────────────────────────

    private void setupCategories() {
        List<Category> categories = categoryDAO.getAllCategories();
        if (categories.isEmpty()) {
            categories.add(new Category(1, "Trang điểm Mắt",
                    "https://res.cloudinary.com/dwu6e0ian/image/upload/v1781340910/co_trang_diem_zgi4dz.webp"));
            categories.add(new Category(2, "Trang điểm Mặt",
                    "https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344277/PomPom_Cloud_Cushion_kwewua.webp"));
            categories.add(new Category(3, "Trang điểm Môi",
                    "https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344448/PomPom_Unicorn_Magic_Palette_p82y28.webp"));
            categories.add(new Category(4, "Bộ sản phẩm",
                    "https://res.cloudinary.com/dwu6e0ian/image/upload/v1781340911/ma_hong_r2373s.webp"));
        }

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
        isLastPage = false;

        List<Product> products = productDAO.getProductsFiltered(
                currentFilter.categoryIds.isEmpty() ? null : currentFilter.categoryIds,
                currentFilter.minPrice,
                currentFilter.maxPrice,
                currentFilter.minRating,
                pageSize, 0);

        productAdapter.setProducts(products);
        if (products.size() < pageSize) isLastPage = true;
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
