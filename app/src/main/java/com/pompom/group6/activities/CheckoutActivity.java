package com.pompom.group6.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsControllerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pompom.group6.MainActivity;
import com.pompom.group6.R;
import com.pompom.group6.adapters.CartItemAdapter;
import com.pompom.group6.adapters.CheckoutPreviewAdapter;
import com.pompom.group6.database.OrderDAO;
import com.pompom.group6.database.UserDAO;
import com.pompom.group6.databinding.ActivityCheckoutBinding;
import com.pompom.group6.models.Address;
import com.pompom.group6.models.CartItem;
import com.pompom.group6.models.User;
import com.pompom.group6.utils.CartManager;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class CheckoutActivity extends SwipeBackActivity {

    private static final int STATE_INACTIVE = 0, STATE_ACTIVE = 1, STATE_DONE = 2;
    private static final int LAST_STEP = 3;
    private static final String[] STEP_TITLES = {"Liên hệ", "Giao hàng", "Thanh toán", "Xác nhận"};

    private ActivityCheckoutBinding binding;
    private CartManager cartManager;
    private CheckoutPreviewAdapter previewAdapter;
    private final NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

    private int currentStep = 0;
    private String totalStr = "0₫";
    private boolean orderExpanded = false;
    private String loadedUserOid = null;
    private float density;

    // Vận chuyển
    private final List<MaterialCardView> shipCards = new ArrayList<>();
    private final List<RadioButton> shipRadios = new ArrayList<>();
    private final List<Long> shipFees = new ArrayList<>();
    private long selectedShippingFee = 0;

    // Thanh toán
    private final List<MaterialCardView> payCards = new ArrayList<>();
    private final List<RadioButton> payRadios = new ArrayList<>();
    private final List<String> payCodes = new ArrayList<>();
    private String selectedPayment = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        density = getResources().getDisplayMetrics().density;

        getWindow().setStatusBarColor(Color.WHITE);
        WindowInsetsControllerCompat wic = WindowCompat.getInsetsController(getWindow(), binding.getRoot());
        if (wic != null) wic.setAppearanceLightStatusBars(true);

        cartManager = CartManager.getInstance(this);

        binding.btnBack.setOnClickListener(v -> finish());
        binding.tvSignIn.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));

        binding.btnPrev.setOnClickListener(v -> { if (currentStep > 0) showStep(currentStep - 1); });
        binding.btnNext.setOnClickListener(v -> {
            if (currentStep < LAST_STEP) showStep(currentStep + 1);
            else attemptPay();
        });
        binding.btnToggleOrder.setOnClickListener(v -> toggleOrder());

        binding.cbTerms.setOnCheckedChangeListener((b, checked) ->
                binding.tvTermsWarning.setVisibility(checked ? View.GONE : View.VISIBLE));

        setupShipping();
        setupPayment();
        refreshOrder();
        showStep(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setupUserFlow();
    }

    // ── Luồng đăng nhập / khách (Task 1) + auto-fill (Task 2) ────────────────

    private void setupUserFlow() {
        String userOid = com.pompom.group6.network.Session.getUserOid(this);

        if (userOid != null) {
            com.pompom.group6.network.ApiClient.get().getUser(userOid)
                    .enqueue(new retrofit2.Callback<com.pompom.group6.network.dto.ApiUser>() {
                        @Override
                        public void onResponse(retrofit2.Call<com.pompom.group6.network.dto.ApiUser> call,
                                               retrofit2.Response<com.pompom.group6.network.dto.ApiUser> resp) {
                            if (binding == null || !resp.isSuccessful() || resp.body() == null) return;
                            com.pompom.group6.network.dto.ApiUser user = resp.body();
                            binding.tvSignIn.setVisibility(View.GONE);
                            binding.userChip.setVisibility(View.VISIBLE);
                            binding.tvUserName.setText(user.fullName);
                            Glide.with(CheckoutActivity.this).load(user.avatarUrl)
                                    .placeholder(R.drawable.ic_avatar).error(R.drawable.ic_avatar)
                                    .circleCrop().into(binding.ivUserAvatar);
                            if (!userOid.equals(loadedUserOid)) {
                                prefillFromUser(user, userOid);
                                loadedUserOid = userOid;
                            }
                        }
                        @Override public void onFailure(retrofit2.Call<com.pompom.group6.network.dto.ApiUser> call, Throwable t) {}
                    });
            return;
        }
        binding.userChip.setVisibility(View.GONE);
        binding.tvSignIn.setVisibility(View.VISIBLE);
        loadedUserOid = null;
    }

    private void prefillFromUser(com.pompom.group6.network.dto.ApiUser user, String userOid) {
        if (user.email != null) binding.etEmail.setText(user.email);
        if (user.phoneNumber != null) binding.etPhone.setText(user.phoneNumber);

        String fullName = user.fullName;
        if (fullName != null && !fullName.trim().isEmpty()) {
            String[] parts = fullName.trim().split("\\s+");
            if (parts.length >= 2) {
                binding.etLastName.setText(parts[0]);
                StringBuilder first = new StringBuilder();
                for (int i = 1; i < parts.length; i++) {
                    if (i > 1) first.append(' ');
                    first.append(parts[i]);
                }
                binding.etFirstName.setText(first.toString());
            } else {
                binding.etFirstName.setText(fullName);
            }
        }

        // Địa chỉ mặc định từ MongoDB.
        com.pompom.group6.network.ApiClient.get().getAddresses(userOid)
                .enqueue(new retrofit2.Callback<List<com.pompom.group6.network.dto.ApiAddress>>() {
                    @Override
                    public void onResponse(retrofit2.Call<List<com.pompom.group6.network.dto.ApiAddress>> call,
                                           retrofit2.Response<List<com.pompom.group6.network.dto.ApiAddress>> resp) {
                        if (binding == null || !resp.isSuccessful() || resp.body() == null || resp.body().isEmpty()) return;
                        com.pompom.group6.network.dto.ApiAddress a = resp.body().get(0); // đã sắp is_default DESC
                        if (a.addressLine != null) binding.etAddress.setText(a.addressLine);
                        if (a.city != null) binding.etCity.setText(a.city);
                        String detail = "";
                        if (a.ward != null && !a.ward.isEmpty()) detail += a.ward;
                        if (a.district != null && !a.district.isEmpty())
                            detail += (detail.isEmpty() ? "" : ", ") + a.district;
                        if (!detail.isEmpty()) binding.etAddress2.setText(detail);
                    }
                    @Override public void onFailure(retrofit2.Call<List<com.pompom.group6.network.dto.ApiAddress>> call, Throwable t) {}
                });
    }

    // ── Vận chuyển từ DB (Task 1) ────────────────────────────────────────────

    private void setupShipping() {
        List<String> carriers = Arrays.asList("GHTK", "GHN", "Viettel");

        binding.shippingContainer.removeAllViews();
        shipCards.clear(); shipRadios.clear(); shipFees.clear();

        for (int i = 0; i < carriers.size(); i++) {
            String[] info = shipInfo(carriers.get(i));
            View row = getLayoutInflater().inflate(R.layout.item_shipping_method, binding.shippingContainer, false);
            ((TextView) row.findViewById(R.id.tvShipName)).setText(info[0]);
            ((TextView) row.findViewById(R.id.tvShipDesc)).setText(info[1]);
            ((TextView) row.findViewById(R.id.tvShipFee)).setText(info[2]);
            shipCards.add(row.findViewById(R.id.cardShip));
            shipRadios.add(row.findViewById(R.id.rbShip));
            shipFees.add(Long.parseLong(info[3]));
            final int idx = i;
            row.setOnClickListener(v -> selectShipping(idx));
            binding.shippingContainer.addView(row);
        }
        if (!shipCards.isEmpty()) selectShipping(0);
    }

    private void selectShipping(int idx) {
        int pink = ContextCompat.getColor(this, R.color.brand_pink);
        int pinkLight = ContextCompat.getColor(this, R.color.brand_pink_light);
        for (int i = 0; i < shipCards.size(); i++) {
            boolean sel = i == idx;
            shipRadios.get(i).setChecked(sel);
            shipCards.get(i).setStrokeColor(sel ? pink : pinkLight);
            shipCards.get(i).setStrokeWidth((int) ((sel ? 2f : 1.5f) * density));
        }
        selectedShippingFee = shipFees.get(idx);
        refreshOrder();
    }

    private String[] shipInfo(String code) {
        switch (code.toUpperCase(Locale.ROOT)) {
            case "GHTK": return new String[]{"Giao Hàng Tiết Kiệm", "Tiêu chuẩn 3–5 ngày", "Miễn phí", "0"};
            case "GHN": return new String[]{"Giao Hàng Nhanh", "Nhanh 2–3 ngày", "20.000₫", "20000"};
            case "VIETTEL": return new String[]{"Viettel Post", "Tiết kiệm 4–6 ngày", "15.000₫", "15000"};
            default: return new String[]{code, "Giao hàng tiêu chuẩn", "Miễn phí", "0"};
        }
    }

    // ── Thanh toán từ DB (Task 4) ────────────────────────────────────────────

    private void setupPayment() {
        List<String> methods = Arrays.asList("COD", "VISA", "VNPAY", "MOMO");

        binding.paymentContainer.removeAllViews();
        payCards.clear(); payRadios.clear(); payCodes.clear();

        for (int i = 0; i < methods.size(); i++) {
            String code = methods.get(i).toUpperCase(Locale.ROOT);
            String[] info = payInfo(code); // name, desc, badge, color
            View row = getLayoutInflater().inflate(R.layout.item_payment_method, binding.paymentContainer, false);
            ((TextView) row.findViewById(R.id.tvPayName)).setText(info[0]);
            ((TextView) row.findViewById(R.id.tvPayDesc)).setText(info[1]);
            TextView badge = row.findViewById(R.id.tvPayBadge);
            badge.setText(info[2]);
            GradientDrawable bg = new GradientDrawable();
            bg.setShape(GradientDrawable.RECTANGLE);
            bg.setCornerRadius(4 * density);
            bg.setColor(Color.parseColor(info[3]));
            badge.setBackground(bg);

            payCards.add(row.findViewById(R.id.cardPay));
            payRadios.add(row.findViewById(R.id.rbPay));
            payCodes.add(code);
            final int idx = i;
            row.setOnClickListener(v -> selectPayment(idx));
            binding.paymentContainer.addView(row);
        }
        if (!payCards.isEmpty()) selectPayment(0);
    }

    private void selectPayment(int idx) {
        int pink = ContextCompat.getColor(this, R.color.brand_pink);
        int pinkLight = ContextCompat.getColor(this, R.color.brand_pink_light);
        for (int i = 0; i < payCards.size(); i++) {
            boolean sel = i == idx;
            payRadios.get(i).setChecked(sel);
            payCards.get(i).setStrokeColor(sel ? pink : pinkLight);
            payCards.get(i).setStrokeWidth((int) ((sel ? 2f : 1.5f) * density));
        }
        selectedPayment = payCodes.get(idx);
        binding.layoutCardFields.setVisibility(
                "VISA".equals(selectedPayment) ? View.VISIBLE : View.GONE);
    }

    private String[] payInfo(String code) {
        switch (code) {
            case "COD": return new String[]{"Thanh toán khi nhận hàng", "Trả tiền mặt khi giao", "COD", "#43A047"};
            case "VISA": return new String[]{"Thẻ VISA / Mastercard", "Thanh toán bằng thẻ", "VISA", "#1A1F71"};
            case "VNPAY": return new String[]{"Cổng VNPAY", "Quét mã / thẻ ATM nội địa", "VNPAY", "#005BAA"};
            case "MOMO": return new String[]{"Ví MoMo", "Thanh toán qua ví MoMo", "MoMo", "#A50064"};
            default: return new String[]{code, "Phương thức thanh toán", code, "#E8989A"};
        }
    }

    // ── Điều khiển bước ──────────────────────────────────────────────────────

    private void showStep(int step) {
        currentStep = step;
        binding.step1Layout.setVisibility(step == 0 ? View.VISIBLE : View.GONE);
        binding.step2Layout.setVisibility(step == 1 ? View.VISIBLE : View.GONE);
        binding.step3Layout.setVisibility(step == 2 ? View.VISIBLE : View.GONE);
        binding.step4Layout.setVisibility(step == 3 ? View.VISIBLE : View.GONE);

        binding.tvStepTitle.setText(STEP_TITLES[step]);
        binding.btnPrev.setVisibility(step == 0 ? View.GONE : View.VISIBLE);
        binding.btnNext.setText(step == LAST_STEP ? "Thanh toán • " + totalStr : "Tiếp tục");

        updateStepper(step);
        binding.scrollContent.smoothScrollTo(0, 0);
    }

    private void updateStepper(int step) {
        TextView[] circles = {binding.circle1, binding.circle2, binding.circle3, binding.circle4};
        TextView[] labels = {binding.label1, binding.label2, binding.label3, binding.label4};
        View[] lines = {binding.line1, binding.line2, binding.line3};
        int pink = ContextCompat.getColor(this, R.color.brand_pink);
        int gray = Color.parseColor("#C9C9C9");
        for (int i = 0; i < circles.length; i++) {
            int state = i < step ? STATE_DONE : (i == step ? STATE_ACTIVE : STATE_INACTIVE);
            styleCircle(circles[i], state, String.valueOf(i + 1));
            labels[i].setTextColor(i <= step ? pink : Color.parseColor("#9E9E9E"));
        }
        for (int i = 0; i < lines.length; i++) {
            lines[i].setBackgroundColor(i < step ? pink : gray);
        }
    }

    private void styleCircle(TextView circle, int state, String number) {
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.OVAL);
        int pink = ContextCompat.getColor(this, R.color.brand_pink);
        switch (state) {
            case STATE_DONE: bg.setColor(pink); circle.setText("✓"); circle.setTextColor(Color.WHITE); break;
            case STATE_ACTIVE: bg.setColor(pink); circle.setText(number); circle.setTextColor(Color.WHITE); break;
            default: bg.setColor(Color.parseColor("#EEEEEE")); circle.setText(number); circle.setTextColor(Color.parseColor("#9E9E9E")); break;
        }
        circle.setBackground(bg);
    }

    // ── Xác nhận thanh toán (Task 2 + Task 5 terms + Task 6 popup) ───────────

    private void attemptPay() {
        if (isEmpty(binding.etEmail) || !text(binding.etEmail).contains("@")) {
            warnMissing(0, binding.etEmail, binding.tilEmail, "Email"); return;
        }
        if (isEmpty(binding.etPhone)) { warnMissing(0, binding.etPhone, binding.tilPhone, "Số điện thoại"); return; }
        if (isEmpty(binding.etFirstName)) { warnMissing(1, binding.etFirstName, binding.tilFirstName, "Tên"); return; }
        if (isEmpty(binding.etLastName)) { warnMissing(1, binding.etLastName, binding.tilLastName, "Họ"); return; }
        if (isEmpty(binding.etAddress)) { warnMissing(1, binding.etAddress, binding.tilAddress, "Địa chỉ"); return; }
        if (isEmpty(binding.etCity)) { warnMissing(1, binding.etCity, binding.tilCity, "Thành phố"); return; }

        if ("VISA".equals(selectedPayment)) {
            if (text(binding.etCardNumber).length() < 12) { warnMissing(2, binding.etCardNumber, binding.tilCardNumber, "Số thẻ"); return; }
            if (text(binding.etExpiry).length() < 4) { warnMissing(2, binding.etExpiry, binding.tilExpiry, "Ngày hết hạn"); return; }
            if (text(binding.etCvv).length() < 3) { warnMissing(2, binding.etCvv, binding.tilCvv, "Mã bảo mật CVV"); return; }
        }

        if (!binding.cbTerms.isChecked()) {
            binding.tvTermsWarning.setVisibility(View.VISIBLE);
            binding.scrollContent.post(() -> binding.scrollContent.smoothScrollTo(0,
                    binding.tvTermsWarning.getBottom()));
            return;
        }

        processPayment(selectedPayment.isEmpty() ? "Đơn hàng" : selectedPayment);
    }

    private void warnMissing(int step, TextInputEditText et, TextInputLayout til, String name) {
        View content = getLayoutInflater().inflate(R.layout.dialog_warn_missing, null);
        ((TextView) content.findViewById(R.id.tvWarnMsg))
                .setText("Bạn chưa nhập \"" + name + "\".\nVui lòng bổ sung để hoàn tất thanh toán.");
        AlertDialog dialog = new AlertDialog.Builder(this).setView(content).create();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        content.findViewById(R.id.btnWarnLater).setOnClickListener(v -> dialog.dismiss());
        content.findViewById(R.id.btnWarnGo).setOnClickListener(v -> {
            dialog.dismiss();
            navigateToField(step, et, til, "Vui lòng nhập " + name);
        });
        dialog.show();
    }

    private void navigateToField(int step, TextInputEditText et, TextInputLayout til, String errorMsg) {
        showStep(step);
        til.setError(errorMsg);
        binding.scrollContent.post(() -> {
            et.requestFocus();
            Rect r = new Rect();
            et.getDrawingRect(r);
            binding.scrollContent.offsetDescendantRectToMyCoords(et, r);
            binding.scrollContent.smoothScrollTo(0, Math.max(0, r.top - 120));
        });
    }

    private boolean isEmpty(TextInputEditText et) { return text(et).isEmpty(); }
    private String text(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }

    // ── Khung đơn hàng (Task 3) ──────────────────────────────────────────────

    private void toggleOrder() {
        orderExpanded = !orderExpanded;
        binding.rvPreviewItems.setVisibility(orderExpanded ? View.VISIBLE : View.GONE);
        binding.ivToggleArrow.animate().rotation(orderExpanded ? -90f : 90f).setDuration(200).start();
    }

    private void refreshOrder() {
        List<CartItem> items = cartManager.getItems();

        CartItemAdapter summary = new CartItemAdapter(items, new CartItemAdapter.CartItemListener() {
            @Override public void onQuantityChanged(CartItem item, int newQty) { }
            @Override public void onRemove(CartItem item) { }
        });
        binding.rvOrderItems.setLayoutManager(new LinearLayoutManager(this));
        binding.rvOrderItems.setAdapter(summary);

        double subtotal = cartManager.getTotalPrice();
        double tax = subtotal * 0.08;
        double total = subtotal + tax + selectedShippingFee;
        binding.tvSubtotal.setText(fmt.format((long) subtotal) + "₫");
        binding.tvTax.setText(fmt.format((long) tax) + "₫");
        binding.tvTotal.setText(fmt.format((long) total) + "₫");
        if (binding.tvShippingLine != null) {
            binding.tvShippingLine.setText(selectedShippingFee <= 0
                    ? "MIỄN PHÍ" : fmt.format(selectedShippingFee) + "₫");
        }
        totalStr = fmt.format((long) total) + "₫";

        binding.orderPreview.setVisibility(items.isEmpty() ? View.GONE : View.VISIBLE);
        binding.tvPreviewCount.setText(cartManager.getTotalCount() + " sản phẩm");
        binding.tvPreviewTotal.setText("Tổng: " + totalStr);
        if (!items.isEmpty()) {
            Glide.with(this).load(items.get(0).getImageUrl())
                    .placeholder(R.drawable.logo_pompom).error(R.drawable.logo_pompom)
                    .into(binding.ivPreviewThumb);
        }

        if (previewAdapter == null) {
            previewAdapter = new CheckoutPreviewAdapter(items, this::confirmRemoveItem);
            binding.rvPreviewItems.setLayoutManager(new LinearLayoutManager(this));
            binding.rvPreviewItems.setAdapter(previewAdapter);
        } else {
            previewAdapter.setItems(items);
        }

        binding.btnToggleOrder.setVisibility(items.size() > 1 ? View.VISIBLE : View.GONE);
        if (items.size() <= 1 && orderExpanded) {
            orderExpanded = false;
            binding.rvPreviewItems.setVisibility(View.GONE);
            binding.ivToggleArrow.setRotation(90f);
        }
        if (currentStep == LAST_STEP) binding.btnNext.setText("Thanh toán • " + totalStr);
    }

    private void confirmRemoveItem(CartItem item, int position, View anchor) {
        spawnRemoveBubble(anchor);

        View content = getLayoutInflater().inflate(R.layout.dialog_remove_item, null);
        ((TextView) content.findViewById(R.id.tvRemoveMsg))
                .setText("Bạn có chắc muốn xoá \"" + item.getTitle() + "\" khỏi đơn hàng?");
        AlertDialog dialog = new AlertDialog.Builder(this).setView(content).create();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        content.findViewById(R.id.btnCancelRemove).setOnClickListener(v -> {
            dialog.dismiss();
            if (previewAdapter != null) previewAdapter.notifyItemChanged(position);
        });
        content.findViewById(R.id.btnConfirmRemove).setOnClickListener(v -> {
            dialog.dismiss();
            cartManager.removeItem(item.getProductId());
            refreshOrder();
        });
        dialog.setOnCancelListener(d -> {
            if (previewAdapter != null) previewAdapter.notifyItemChanged(position);
        });
        dialog.show();
    }

    /** Bong bóng hồng bay ra từ dòng sản phẩm vừa bỏ tick. */
    private void spawnRemoveBubble(View anchor) {
        if (anchor == null) return;
        ViewGroup root = findViewById(android.R.id.content);
        if (root == null) return;
        int[] a = new int[2]; anchor.getLocationOnScreen(a);
        int[] rl = new int[2]; root.getLocationOnScreen(rl);
        int size = (int) (34 * density);
        float cx = a[0] - rl[0] + 42 * density;
        float cy = a[1] - rl[1] + anchor.getHeight() / 2f;

        TextView bubble = new TextView(this);
        GradientDrawable bg = new GradientDrawable();
        bg.setShape(GradientDrawable.OVAL);
        bg.setColor(ContextCompat.getColor(this, R.color.brand_pink));
        bubble.setBackground(bg);
        bubble.setElevation(24 * density);
        root.addView(bubble, new FrameLayout.LayoutParams(size, size));
        bubble.setX(cx - size / 2f);
        bubble.setY(cy - size / 2f);
        bubble.setScaleX(0.3f);
        bubble.setScaleY(0.3f);
        bubble.animate().scaleX(1.15f).scaleY(1.15f)
                .translationYBy(-64 * density).alpha(0f)
                .setInterpolator(new OvershootInterpolator())
                .setDuration(520)
                .withEndAction(() -> root.removeView(bubble))
                .start();
    }

    // ── Thanh toán ───────────────────────────────────────────────────────────

    private void processPayment(final String method) {
        binding.loadingOverlay.setVisibility(View.VISIBLE);
        binding.tvProcessing.setText("Đang xử lý " + method + "...");

        // Gom các sản phẩm cloud (có ObjectId) trong giỏ để tạo đơn thật trên MongoDB.
        String userOid = com.pompom.group6.network.Session.getUserOid(this);
        java.util.List<com.pompom.group6.network.dto.OrderRequest.Item> items = new java.util.ArrayList<>();
        for (CartItem ci : cartManager.getItems()) {
            if (ci.getProductOid() != null) {
                items.add(new com.pompom.group6.network.dto.OrderRequest.Item(ci.getProductOid(), ci.getQuantity()));
            }
        }

        // Khách chưa đăng nhập hoặc giỏ không có sản phẩm cloud → giữ hành vi cũ (đơn demo).
        if (userOid == null || items.isEmpty()) {
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (binding == null) return;
                binding.loadingOverlay.setVisibility(View.GONE);
                showSuccessDialog();
            }, 1500);
            return;
        }

        com.pompom.group6.network.dto.OrderRequest body =
                new com.pompom.group6.network.dto.OrderRequest(userOid, method, selectedShippingFee, "", items);
        com.pompom.group6.network.ApiClient.get().createOrder(body)
                .enqueue(new retrofit2.Callback<com.pompom.group6.network.dto.ApiOrder>() {
                    @Override
                    public void onResponse(retrofit2.Call<com.pompom.group6.network.dto.ApiOrder> call,
                                           retrofit2.Response<com.pompom.group6.network.dto.ApiOrder> resp) {
                        if (binding == null) return;
                        binding.loadingOverlay.setVisibility(View.GONE);
                        if (resp.isSuccessful() && resp.body() != null) {
                            // Đơn đã tạo → xoá giỏ hàng server để không còn tồn ở thiết bị khác.
                            com.pompom.group6.network.ApiClient.get().clearCart(userOid)
                                    .enqueue(new retrofit2.Callback<Void>() {
                                        @Override public void onResponse(retrofit2.Call<Void> c, retrofit2.Response<Void> r) {}
                                        @Override public void onFailure(retrofit2.Call<Void> c, Throwable t) {}
                                    });
                            showSuccessDialog();
                        } else {
                            android.widget.Toast.makeText(CheckoutActivity.this,
                                    "Đặt đơn thất bại, vui lòng thử lại", android.widget.Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<com.pompom.group6.network.dto.ApiOrder> call, Throwable t) {
                        if (binding == null) return;
                        binding.loadingOverlay.setVisibility(View.GONE);
                        android.widget.Toast.makeText(CheckoutActivity.this,
                                "Không kết nối được máy chủ", android.widget.Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showSuccessDialog() {
        double total = cartManager.getTotalPrice() * 1.08 + selectedShippingFee;
        String totalText = fmt.format((long) total) + "₫";

        View content = getLayoutInflater().inflate(R.layout.dialog_order_success, null);
        ((TextView) content.findViewById(R.id.tvSuccessTotal)).setText(totalText);
        AlertDialog dialog = new AlertDialog.Builder(this).setView(content).setCancelable(false).create();
        if (dialog.getWindow() != null)
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        content.findViewById(R.id.btnSuccessHome).setOnClickListener(v -> {
            dialog.dismiss();
            cartManager.clearCart();
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });
        dialog.show();
    }
}
