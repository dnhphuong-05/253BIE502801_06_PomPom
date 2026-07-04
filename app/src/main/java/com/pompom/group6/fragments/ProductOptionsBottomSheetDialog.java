package com.pompom.group6.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.pompom.group6.R;
import com.pompom.group6.activities.CartActivity;
import com.pompom.group6.adapters.VariantAdapter;
import com.pompom.group6.database.ProductDAO;
import com.pompom.group6.databinding.DialogProductOptionsBottomSheetBinding;
import com.pompom.group6.models.CartItem;
import com.pompom.group6.models.ProductVariant;
import com.pompom.group6.utils.CartManager;

import java.util.List;

public class ProductOptionsBottomSheetDialog extends BottomSheetDialogFragment {

    // ── Action type constants ────────────────────────────────────────────────────
    /** From product list grid — "+" button → add to cart, update badge, dismiss. */
    public static final int ACTION_ADD_TO_CART        = 0;
    /** From product detail "Thêm vào giỏ hàng" → add, dismiss, show Toast. */
    public static final int ACTION_ADD_TO_CART_DETAIL = 1;
    /** From product detail "Mua ngay" → add, go to CartActivity (checkout), dismiss. */
    public static final int ACTION_BUY_NOW            = 2;

    // ── Bundle keys ─────────────────────────────────────────────────────────────
    private static final String ARG_PRODUCT_ID  = "arg_product_id";
    private static final String ARG_TITLE       = "arg_title";
    private static final String ARG_PRICE       = "arg_price";
    private static final String ARG_IMAGE_URL   = "arg_image_url";
    private static final String ARG_ACTION_TYPE = "arg_action_type";
    private static final String SHEET_TAG       = "ProductOptionsBottomSheet";

    // ── State ────────────────────────────────────────────────────────────────────
    private DialogProductOptionsBottomSheetBinding binding;
    private int quantity = 1;
    private ProductVariant selectedVariant = null;

    // ── Static factory ───────────────────────────────────────────────────────────

    /**
     * Show this bottom sheet.
     *
     * @param fm         The FragmentManager to use (getSupportFragmentManager()).
     * @param productId  Product DB id (used to load variants).
     * @param title      Product display name.
     * @param priceStr   Formatted price string, e.g. "799.000đ".
     * @param imageUrl   URL for the product thumbnail.
     * @param actionType One of ACTION_ADD_TO_CART / ACTION_ADD_TO_CART_DETAIL / ACTION_BUY_NOW.
     */
    public static void show(FragmentManager fm,
                            int productId,
                            String title,
                            String priceStr,
                            String imageUrl,
                            int actionType) {
        ProductOptionsBottomSheetDialog sheet = new ProductOptionsBottomSheetDialog();
        Bundle args = new Bundle();
        args.putInt(ARG_PRODUCT_ID,   productId);
        args.putString(ARG_TITLE,     title);
        args.putString(ARG_PRICE,     priceStr);
        args.putString(ARG_IMAGE_URL, imageUrl);
        args.putInt(ARG_ACTION_TYPE,  actionType);
        sheet.setArguments(args);
        sheet.show(fm, SHEET_TAG);
    }

    // ── Lifecycle ────────────────────────────────────────────────────────────────

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DialogProductOptionsBottomSheetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Bundle args = getArguments();
        if (args == null) { dismiss(); return; }

        int    productId  = args.getInt(ARG_PRODUCT_ID, -1);
        String title      = args.getString(ARG_TITLE, "");
        String price      = args.getString(ARG_PRICE, "");
        String imgUrl     = args.getString(ARG_IMAGE_URL, "");
        int    actionType = args.getInt(ARG_ACTION_TYPE, ACTION_ADD_TO_CART);

        populateHeader(title, price, imgUrl);
        setupQuantityControls();
        loadVariants(productId);
        setupActionButton(productId, title, price, imgUrl, actionType);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ── Header ───────────────────────────────────────────────────────────────────

    private void populateHeader(String title, String price, String imageUrl) {
        binding.tvProductTitle.setText(title);
        binding.tvProductPrice.setText(price);
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.favicon_pompom)
                    .error(R.drawable.favicon_pompom)
                    .into(binding.ivProductPreview);
        }
    }

    // ── Quantity ─────────────────────────────────────────────────────────────────

    private void setupQuantityControls() {
        binding.tvQuantity.setText(String.valueOf(quantity));

        binding.btnMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                binding.tvQuantity.setText(String.valueOf(quantity));
            }
        });

        binding.btnPlus.setOnClickListener(v -> {
            quantity++;
            binding.tvQuantity.setText(String.valueOf(quantity));
        });
    }

    // ── Variants ─────────────────────────────────────────────────────────────────

    private void loadVariants(int productId) {
        if (productId == -1) {
            showNoVariants();
            return;
        }
        new Thread(() -> {
            Context appCtx = requireContext().getApplicationContext();
            List<ProductVariant> variants = new ProductDAO(appCtx).getVariantsForProduct(productId);

            if (getActivity() == null) return;
            getActivity().runOnUiThread(() -> {
                if (binding == null) return;
                if (variants == null || variants.isEmpty()) {
                    showNoVariants();
                } else {
                    binding.rvColorOptions.setVisibility(View.VISIBLE);
                    binding.tvNoVariants.setVisibility(View.GONE);

                    // Pre-select first variant
                    selectedVariant = variants.get(0);
                    binding.tvSelectedVariant.setText(selectedVariant.getName());

                    VariantAdapter adapter = new VariantAdapter(variants, variant -> {
                        selectedVariant = variant;
                        binding.tvSelectedVariant.setText(variant.getName());
                    });
                    binding.rvColorOptions.setLayoutManager(
                            new LinearLayoutManager(getContext(),
                                    LinearLayoutManager.HORIZONTAL, false));
                    binding.rvColorOptions.setAdapter(adapter);
                }
            });
        }).start();
    }

    private void showNoVariants() {
        binding.rvColorOptions.setVisibility(View.GONE);
        binding.tvNoVariants.setVisibility(View.VISIBLE);
    }

    // ── Action button ─────────────────────────────────────────────────────────────

    private void setupActionButton(int productId, String title,
                                   String price, String imageUrl, int actionType) {
        // Label
        switch (actionType) {
            case ACTION_ADD_TO_CART:
                binding.btnAction.setText("+ Thêm vào giỏ hàng");
                break;
            case ACTION_ADD_TO_CART_DETAIL:
                binding.btnAction.setText("Thêm vào giỏ hàng");
                break;
            case ACTION_BUY_NOW:
                binding.btnAction.setText("Mua ngay");
                break;
        }

        binding.btnAction.setOnClickListener(v -> {
            // Build cart item and add to manager (notifies badge listeners globally)
            CartItem item = new CartItem(productId, title, price, imageUrl, quantity);
            CartManager.getInstance(requireContext()).addItem(item);

            switch (actionType) {
                case ACTION_ADD_TO_CART:
                    // CASE 1: from product list — just dismiss; badge updates via listener
                    dismiss();
                    break;

                case ACTION_ADD_TO_CART_DETAIL:
                    // CASE 2: from product detail — dismiss then show success toast
                    dismiss();
                    Toast.makeText(requireContext(),
                            "✓ Đã thêm " + quantity + " sản phẩm vào giỏ hàng",
                            Toast.LENGTH_SHORT).show();
                    break;

                case ACTION_BUY_NOW:
                    // CASE 3: from product detail "Mua ngay" — go directly to CheckoutActivity
                    dismiss();
                    Intent intent = new Intent(requireContext(),
                            com.pompom.group6.activities.CheckoutActivity.class);
                    requireActivity().startActivity(intent);
                    requireActivity().overridePendingTransition(
                            R.anim.slide_in_right, R.anim.slide_out_left);
                    break;
            }
        });
    }
}
