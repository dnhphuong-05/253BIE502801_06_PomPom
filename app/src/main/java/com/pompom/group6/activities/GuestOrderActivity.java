package com.pompom.group6.activities;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pompom.group6.R;
import com.pompom.group6.adapters.GuestOrderAdapter;
import com.pompom.group6.database.OrderDAO;
import com.pompom.group6.databinding.ActivityGuestOrderBinding;
import com.pompom.group6.models.Order;
import com.pompom.group6.utils.StatusBarUtils;

import java.util.List;
import java.util.regex.Pattern;

public class GuestOrderActivity extends SwipeBackActivity {

    /** 10 digits starting with "0", e.g. 0912345678 */
    private static final Pattern PHONE_REGEX = Pattern.compile("^0[0-9]{9}$");

    private ActivityGuestOrderBinding binding;
    private OrderDAO orderDAO;
    private GuestOrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityGuestOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        orderDAO = new OrderDAO(this);

        setupSystemUi();
        setupListeners();
    }

    private void setupSystemUi() {
        // Header của màn này nền trắng — status bar phải trắng theo, không để lộ màu tím
        // mặc định của theme (trước đây chỉ set icon tối mà quên set màu nền status bar).
        StatusBarUtils.applyWhiteHeader(this);
    }

    private void setupListeners() {
        // Back button
        binding.btnBack.setOnClickListener(v -> {
            finish();
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        });

        // Clear error on typing
        binding.etPhone.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.tilPhone.setError(null);
                // Hide previous results while user is typing
                binding.layoutEmpty.setVisibility(View.GONE);
                binding.layoutResults.setVisibility(View.GONE);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Search button
        binding.btnSearch.setOnClickListener(v -> {
            String phone = binding.etPhone.getText() != null
                    ? binding.etPhone.getText().toString().trim()
                    : "";

            // Dismiss keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(binding.etPhone.getWindowToken(), 0);

            if (!validatePhone(phone)) return;

            searchOrders(phone);
        });
    }

    /**
     * Validates the phone string against Vietnamese 10-digit format.
     * Shows a red error on the TextInputLayout if invalid.
     * @return true if valid
     */
    private boolean validatePhone(String phone) {
        if (phone.isEmpty()) {
            binding.tilPhone.setError("Vui lòng nhập số điện thoại");
            return false;
        }
        if (!phone.matches("[0-9]+")) {
            binding.tilPhone.setError("Số điện thoại chỉ được chứa chữ số");
            return false;
        }
        if (!PHONE_REGEX.matcher(phone).matches()) {
            binding.tilPhone.setError("Số điện thoại không hợp lệ (phải bắt đầu bằng 0, đủ 10 chữ số)");
            return false;
        }
        binding.tilPhone.setError(null);
        return true;
    }

    private void searchOrders(String phone) {
        binding.layoutEmpty.setVisibility(View.GONE);
        binding.layoutResults.setVisibility(View.GONE);

        // Tra cứu đơn theo số điện thoại từ MongoDB.
        com.pompom.group6.network.ApiClient.get().getOrdersByPhone(phone)
                .enqueue(new retrofit2.Callback<List<com.pompom.group6.network.dto.ApiOrder>>() {
                    @Override
                    public void onResponse(retrofit2.Call<List<com.pompom.group6.network.dto.ApiOrder>> call,
                                           retrofit2.Response<List<com.pompom.group6.network.dto.ApiOrder>> resp) {
                        if (binding == null) return;
                        List<Order> orders = new java.util.ArrayList<>();
                        if (resp.isSuccessful() && resp.body() != null) {
                            for (com.pompom.group6.network.dto.ApiOrder o : resp.body()) {
                                orders.add(new Order(0, o.id, o.orderNumber, o.finalAmount, o.status, o.paymentMethod,
                                        o.createdAt, o.itemCount, o.firstItemName, o.firstItemImage));
                            }
                        }
                        if (orders.isEmpty()) {
                            binding.layoutEmpty.setVisibility(View.VISIBLE);
                        } else {
                            binding.tvResultCount.setText("Tìm thấy " + orders.size() + " đơn hàng");
                            binding.layoutResults.setVisibility(View.VISIBLE);
                            if (adapter == null) {
                                adapter = new GuestOrderAdapter(orders, order ->
                                        Toast.makeText(GuestOrderActivity.this, "Chi tiết đơn #" + order.getOrderNumber(), Toast.LENGTH_SHORT).show());
                                binding.rvOrders.setLayoutManager(new LinearLayoutManager(GuestOrderActivity.this));
                                binding.rvOrders.setAdapter(adapter);
                            } else {
                                adapter.updateOrders(orders);
                            }
                        }
                    }

                    @Override
                    public void onFailure(retrofit2.Call<List<com.pompom.group6.network.dto.ApiOrder>> call, Throwable t) {
                        if (binding == null) return;
                        Toast.makeText(GuestOrderActivity.this, "Không kết nối được máy chủ", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
