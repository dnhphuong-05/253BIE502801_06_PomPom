package com.pompom.group6.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
                            String productId,
                            String title,
                            String priceStr,
                            String imageUrl,
                            int actionType) {
        ProductOptionsBottomSheetDialog sheet = new ProductOptionsBottomSheetDialog();
        Bundle args = new Bundle();
        args.putString(ARG_PRODUCT_ID, productId);
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

        String productId  = args.getString(ARG_PRODUCT_ID, null);
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

    private void loadVariants(String productId) {
        if (productId == null) { showNoVariants(); return; }

        if (productId.matches("\\d+")) {
            // SQLite product: load locally
            final int sqliteId = Integer.parseInt(productId);
            new Thread(() -> {
                Context appCtx = requireContext().getApplicationContext();
                List<ProductVariant> variants = new ProductDAO(appCtx).getVariantsForProduct(sqliteId);
                if (getActivity() == null) return;
                getActivity().runOnUiThread(() -> bindVariants(variants));
            }).start();
        } else {
            // MongoDB product: fetch variants from API
            com.pompom.group6.network.ApiClient.get()
                    .getProduct(productId)
                    .enqueue(new retrofit2.Callback<com.pompom.group6.network.dto.ApiProduct>() {
                        @Override
                        public void onResponse(retrofit2.Call<com.pompom.group6.network.dto.ApiProduct> call,
                                               retrofit2.Response<com.pompom.group6.network.dto.ApiProduct> resp) {
                            if (getActivity() == null || binding == null) return;
                            List<com.pompom.group6.network.dto.ApiProductVariant> apiVariants =
                                    (resp.isSuccessful() && resp.body() != null) ? resp.body().variants : null;
                            List<ProductVariant> variants = new java.util.ArrayList<>();
                            if (apiVariants != null) {
                                for (com.pompom.group6.network.dto.ApiProductVariant av : apiVariants) {
                                    ProductVariant variant = new ProductVariant(
                                            0, 0,
                                            av.variantName != null ? av.variantName : "",
                                            "",
                                            av.additionalPrice,
                                            av.stock,
                                            av.imageUrl);
                                    variant.setOid(av.id);
                                    variants.add(variant);
                                }
                            }
                            getActivity().runOnUiThread(() -> bindVariants(variants));
                        }

                        @Override
                        public void onFailure(retrofit2.Call<com.pompom.group6.network.dto.ApiProduct> call, Throwable t) {
                            if (getActivity() != null) getActivity().runOnUiThread(() -> showNoVariants());
                        }
                    });
        }
    }

    private void bindVariants(List<ProductVariant> variants) {
        if (binding == null) return;
        if (variants == null || variants.isEmpty()) {
            showNoVariants();
            return;
        }
        binding.rvColorOptions.setVisibility(View.VISIBLE);
        binding.tvNoVariants.setVisibility(View.GONE);

        selectedVariant = variants.get(0);
        binding.tvSelectedVariant.setText(selectedVariant.getName());

        VariantAdapter adapter = new VariantAdapter(variants, variant -> {
            selectedVariant = variant;
            binding.tvSelectedVariant.setText(variant.getName());
        });
        binding.rvColorOptions.setLayoutManager(
                new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        binding.rvColorOptions.setAdapter(adapter);
    }

    private void showNoVariants() {
        binding.rvColorOptions.setVisibility(View.GONE);
        binding.tvNoVariants.setVisibility(View.VISIBLE);
    }

    // ── Action button ─────────────────────────────────────────────────────────────

    private void setupActionButton(String productId, String title,
                                   String price, String imageUrl, int actionType) {
        // Giỏ hàng (in-memory) vẫn dùng id số; id ObjectId thì băm sang số tạm thời.
        final int cartProductId = (productId != null && productId.matches("\\d+"))
                ? Integer.parseInt(productId)
                : (productId == null ? 0 : Math.abs(productId.hashCode()));
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
            CartItem item = new CartItem(cartProductId, title, price, imageUrl, quantity);
            // Nếu là sản phẩm cloud (id ObjectId) thì lưu lại để đặt đơn thật lên MongoDB.
            boolean isCloud = productId != null && !productId.matches("\\d+");
            if (isCloud) item.setProductOid(productId);
            String variantOid = selectedVariant != null ? selectedVariant.getOid() : null;
            if (selectedVariant != null) item.setVariantName(selectedVariant.getName());
            item.setVariantId(variantOid);
            CartManager.getInstance(requireContext()).addItem(item);

            // Đồng bộ lên giỏ hàng server (nếu đã đăng nhập & là sản phẩm cloud) — để giỏ theo được nhiều thiết bị.
            String userOid = com.pompom.group6.network.Session.getUserOid(requireContext());
            if (isCloud && userOid != null) {
                com.pompom.group6.network.ApiClient.get()
                        .addCartItem(new com.pompom.group6.network.dto.CartItemRequest(userOid, productId, variantOid, quantity))
                        .enqueue(new retrofit2.Callback<Void>() {
                            @Override public void onResponse(retrofit2.Call<Void> c, retrofit2.Response<Void> r) {}
                            @Override public void onFailure(retrofit2.Call<Void> c, Throwable t) {}
                        });
            }

            if (actionType == ACTION_BUY_NOW) {
                // "Mua ngay" — đi thẳng tới Checkout
                dismiss();
                Intent intent = new Intent(requireContext(),
                        com.pompom.group6.activities.CheckoutActivity.class);
                requireActivity().startActivity(intent);
                requireActivity().overridePendingTransition(
                        R.anim.slide_in_right, R.anim.slide_out_left);
                return;
            }

            // ACTION_ADD_TO_CART / ACTION_ADD_TO_CART_DETAIL:
            // Không toast — popup hóa thành bong bóng hồng bay vào giỏ hàng.
            androidx.fragment.app.FragmentActivity act = getActivity();
            int qty = quantity;
            float startX = 0f, startY = 0f;
            if (act != null && binding != null) {
                View root = binding.getRoot();
                int[] loc = new int[2];
                root.getLocationOnScreen(loc);
                startX = loc[0] + root.getWidth() / 2f;
                startY = loc[1] + root.getHeight() / 2f;
            }
            dismiss();
            if (act != null) {
                com.pompom.group6.utils.CartFlyAnimator.fly(act, qty, startX, startY);
            }
        });
    }
}
