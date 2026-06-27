package com.pompom.group6.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pompom.group6.adapters.CategoryAdapter;
import com.pompom.group6.adapters.ProductAdapter;
import com.pompom.group6.database.CategoryDAO;
import com.pompom.group6.database.ProductDAO;
import com.pompom.group6.database.PromotionDAO;
import com.pompom.group6.databinding.FragmentShopBinding;
import com.pompom.group6.models.Category;
import com.pompom.group6.models.Product;

import java.util.List;

public class ShopFragment extends Fragment {

    private FragmentShopBinding binding;
    private ProductDAO productDAO;
    private CategoryDAO categoryDAO;
    private PromotionDAO promotionDAO;
    
    private ProductAdapter productAdapter;
    private CategoryAdapter categoryAdapter;
    
    private int currentPage = 0;
    private final int pageSize = 8;
    private boolean isLastPage = false;
    private boolean isLoading = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentShopBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        
        productDAO = new ProductDAO(requireContext());
        categoryDAO = new CategoryDAO(requireContext());
        promotionDAO = new PromotionDAO(requireContext());
        
        setupMarquee();
        setupCategories();
        setupProducts();
        setupPagination();
        
        loadInitialData();
    }

    private void setupMarquee() {
        List<String> messages = promotionDAO.getActivePromotionMessages();
        if (!messages.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            for (String msg : messages) {
                sb.append(msg).append("   ✦   ");
            }
            binding.tvPromoMarquee.setText(sb.toString());
            binding.tvPromoMarquee.setSelected(true); // Start marquee
        }
    }

    private void setupCategories() {
        List<Category> categories = categoryDAO.getAllCategories();
        if (categories.isEmpty()) {
            // Mock categories if DB is empty
            categories.add(new Category(1, "Trang điểm Mắt", "https://res.cloudinary.com/dwu6e0ian/image/upload/v1781340910/co_trang_diem_zgi4dz.webp"));
            categories.add(new Category(2, "Trang điểm Mặt", "https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344277/PomPom_Cloud_Cushion_kwewua.webp"));
            categories.add(new Category(3, "Trang điểm Môi", "https://res.cloudinary.com/dwu6e0ian/image/upload/v1781344448/PomPom_Unicorn_Magic_Palette_p82y28.webp"));
            categories.add(new Category(4, "Bộ sản phẩm", "https://res.cloudinary.com/dwu6e0ian/image/upload/v1781340911/ma_hong_r2373s.webp"));
        }
        categoryAdapter = new CategoryAdapter(categories);
        binding.rvCategories.setAdapter(categoryAdapter);
    }

    private void setupProducts() {
        productAdapter = new ProductAdapter();
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return productAdapter.getItemViewType(position) == 1 ? 2 : 1;
            }
        });
        binding.rvProducts.setLayoutManager(layoutManager);
        binding.rvProducts.setAdapter(productAdapter);
        binding.rvProducts.setNestedScrollingEnabled(false); // Important for NestedScrollView
    }

    private void setupPagination() {
        binding.nestedScrollView.setOnScrollChangeListener((NestedScrollView.OnScrollChangeListener) (v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()) {
                if (!isLoading && !isLastPage) {
                    loadMoreProducts();
                }
            }
        });
    }

    private void loadInitialData() {
        currentPage = 0;
        isLastPage = false;
        List<Product> products = productDAO.getProductsPaginated(pageSize, 0);
        productAdapter.setProducts(products);
        if (products.size() < pageSize) {
            isLastPage = true;
        }
    }

    private void loadMoreProducts() {
        isLoading = true;
        productAdapter.showLoading();
        
        // Simulate network delay for UX
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            currentPage++;
            List<Product> moreProducts = productDAO.getProductsPaginated(pageSize, currentPage * pageSize);
            
            productAdapter.hideLoading();
            if (moreProducts.isEmpty()) {
                isLastPage = true;
            } else {
                productAdapter.addProducts(moreProducts);
                if (moreProducts.size() < pageSize) {
                    isLastPage = true;
                }
            }
            isLoading = false;
        }, 1500);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
