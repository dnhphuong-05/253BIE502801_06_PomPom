package com.pompom.group6.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.pompom.group6.adapters.ProductAdapter;
import com.pompom.group6.databinding.FragmentHomeBinding;
import com.pompom.group6.models.Product;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupProductsRecyclerView();
        setupRecentlyViewedRecyclerView();
    }

    private void setupProductsRecyclerView() {
        binding.rvProducts.setLayoutManager(new GridLayoutManager(requireContext(), 4));
        
        List<Product> products = new ArrayList<>();
        products.add(new Product(0, "Knight Unicorn All-In Gift Set", "$669.00", "$711.00"));
        products.add(new Product(0, "Knight Unicorn 6-Color Satin Makeup Palette", "$35.00", null));
        products.add(new Product(0, "Knight Unicorn Satin Blush", "$26.00", null));
        products.add(new Product(0, "Knight Unicorn Glazed Lipstick", "$20.00", null));
        products.add(new Product(0, "Knight Unicorn Eyeshadow Palette & Charm", "$15.00", null));
        products.add(new Product(0, "Knight Unicorn lip liner", "$15.00", null));
        products.add(new Product(0, "Knight Unicorn Cloud Loose Setting Powder", "$35.00", null));
        products.add(new Product(0, "Knight Unicorn Cloud Loose Setting Powder (with 2 Refills)", "$45.00", null));

        ProductAdapter adapter = new ProductAdapter(products);
        binding.rvProducts.setAdapter(adapter);
    }

    private void setupRecentlyViewedRecyclerView() {
        binding.rvRecentlyViewed.setLayoutManager(new GridLayoutManager(requireContext(), 3));
        
        List<Product> recentProducts = new ArrayList<>();
        recentProducts.add(new Product(0, "Knight Unicorn Satin Blush", "$26.00", null));
        recentProducts.add(new Product(0, "The Sweetie Bear 4-Color Concealer Palette", "$32.00", null));
        recentProducts.add(new Product(0, "The Sweetie Bear Coating Lip Jelly", "$20.00", null));

        ProductAdapter adapter = new ProductAdapter(recentProducts);
        binding.rvRecentlyViewed.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
