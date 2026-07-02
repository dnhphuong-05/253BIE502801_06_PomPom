package com.pompom.group6.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pompom.group6.MainActivity;
import com.pompom.group6.R;
import com.pompom.group6.adapters.CartItemAdapter;
import com.pompom.group6.databinding.ActivityCheckoutBinding;
import com.pompom.group6.models.CartItem;
import com.pompom.group6.utils.CartManager;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CheckoutActivity extends AppCompatActivity {

    private ActivityCheckoutBinding binding;
    private CartManager cartManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cartManager = CartManager.getInstance(this);

        binding.btnBack.setOnClickListener(v -> finish());
        binding.tvSignIn.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });

        binding.btnShopPay.setOnClickListener(v -> simulateExpressPayment("Shop Pay"));
        binding.btnPayPal.setOnClickListener(v -> simulateExpressPayment("PayPal"));
        binding.btnGooglePay.setOnClickListener(v -> simulateExpressPayment("Google Pay"));

        binding.btnPayNow.setOnClickListener(v -> validateAndPay());

        loadOrderSummary();
    }

    private void loadOrderSummary() {
        List<CartItem> items = cartManager.getItems();
        NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("vi", "VN"));

        // Order items list (read-only, no +/- needed here)
        CartItemAdapter summaryAdapter = new CartItemAdapter(items, new CartItemAdapter.CartItemListener() {
            @Override public void onQuantityChanged(CartItem item, int newQty) { /* read-only */ }
            @Override public void onRemove(CartItem item) { /* read-only */ }
        });
        binding.rvOrderItems.setLayoutManager(new LinearLayoutManager(this));
        binding.rvOrderItems.setAdapter(summaryAdapter);

        double subtotal = cartManager.getTotalPrice();
        double tax = subtotal * 0.08;
        double total = subtotal + tax;

        binding.tvSubtotal.setText(fmt.format((long) subtotal) + "₫");
        binding.tvTax.setText(fmt.format((long) tax) + "₫");
        binding.tvTotal.setText(fmt.format((long) total) + "₫");
        binding.btnPayNow.setText("THANH TOÁN NGAY — " + fmt.format((long) total) + "₫");
    }

    private void validateAndPay() {
        // Basic validation
        String email = binding.etEmail.getText() != null ? binding.etEmail.getText().toString().trim() : "";
        String firstName = binding.etFirstName.getText() != null ? binding.etFirstName.getText().toString().trim() : "";
        String lastName = binding.etLastName.getText() != null ? binding.etLastName.getText().toString().trim() : "";
        String address = binding.etAddress.getText() != null ? binding.etAddress.getText().toString().trim() : "";
        String city = binding.etCity.getText() != null ? binding.etCity.getText().toString().trim() : "";
        String postal = binding.etPostal.getText() != null ? binding.etPostal.getText().toString().trim() : "";
        String phone = binding.etPhone.getText() != null ? binding.etPhone.getText().toString().trim() : "";
        String cardNumber = binding.etCardNumber.getText() != null ? binding.etCardNumber.getText().toString().trim() : "";
        String expiry = binding.etExpiry.getText() != null ? binding.etExpiry.getText().toString().trim() : "";
        String cvv = binding.etCvv.getText() != null ? binding.etCvv.getText().toString().trim() : "";

        if (TextUtils.isEmpty(email) || !email.contains("@")) {
            binding.tilEmail.setError("Vui lòng nhập email hợp lệ");
            binding.tilEmail.requestFocus();
            return;
        }
        binding.tilEmail.setError(null);

        if (TextUtils.isEmpty(firstName)) {
            binding.tilFirstName.setError("Nhập tên của bạn");
            binding.tilFirstName.requestFocus();
            return;
        }
        binding.tilFirstName.setError(null);

        if (TextUtils.isEmpty(lastName)) {
            binding.tilLastName.setError("Nhập họ của bạn");
            binding.tilLastName.requestFocus();
            return;
        }
        binding.tilLastName.setError(null);

        if (TextUtils.isEmpty(address)) {
            binding.tilAddress.setError("Nhập địa chỉ giao hàng");
            binding.tilAddress.requestFocus();
            return;
        }
        binding.tilAddress.setError(null);

        if (TextUtils.isEmpty(city)) {
            binding.tilCity.setError("Nhập thành phố");
            binding.tilCity.requestFocus();
            return;
        }
        binding.tilCity.setError(null);

        if (TextUtils.isEmpty(postal)) {
            binding.tilPostal.setError("Trường này bắt buộc");
            binding.tilPostal.requestFocus();
            return;
        }
        binding.tilPostal.setError(null);

        if (TextUtils.isEmpty(phone)) {
            binding.tilPhone.setError("Nhập số điện thoại");
            binding.tilPhone.requestFocus();
            return;
        }
        binding.tilPhone.setError(null);

        if (TextUtils.isEmpty(cardNumber) || cardNumber.length() < 12) {
            binding.tilCardNumber.setError("Số thẻ không hợp lệ");
            binding.tilCardNumber.requestFocus();
            return;
        }
        binding.tilCardNumber.setError(null);

        if (TextUtils.isEmpty(expiry) || expiry.length() < 4) {
            binding.tilExpiry.setError("Ngày hết hạn không hợp lệ");
            binding.tilExpiry.requestFocus();
            return;
        }
        binding.tilExpiry.setError(null);

        if (TextUtils.isEmpty(cvv) || cvv.length() < 3) {
            binding.tilCvv.setError("CVV không hợp lệ");
            binding.tilCvv.requestFocus();
            return;
        }
        binding.tilCvv.setError(null);

        processPayment("Thẻ tín dụng");
    }

    private void simulateExpressPayment(String method) {
        processPayment(method);
    }

    private void processPayment(final String method) {
        binding.loadingOverlay.setVisibility(View.VISIBLE);
        binding.tvProcessing.setText("Đang xử lý " + method + "...");

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            binding.loadingOverlay.setVisibility(View.GONE);
            showSuccessDialog();
        }, 2500);
    }

    private void showSuccessDialog() {
        NumberFormat fmt = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        double total = cartManager.getTotalPrice() * 1.08;
        String totalStr = fmt.format((long) total) + "₫";

        new AlertDialog.Builder(this)
                .setTitle("🎉 Đặt hàng thành công!")
                .setMessage("Cảm ơn bạn đã mua hàng tại Pom Pom!\n\n" +
                        "Tổng thanh toán: " + totalStr + "\n\n" +
                        "Đơn hàng sẽ được giao trong 3-5 ngày làm việc.\n" +
                        "Chúng tôi sẽ gửi xác nhận qua email của bạn.")
                .setCancelable(false)
                .setPositiveButton("Về trang chủ", (dialog, which) -> {
                    cartManager.clearCart();
                    // Navigate back to MainActivity and clear the back stack
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                })
                .show();
    }
}
