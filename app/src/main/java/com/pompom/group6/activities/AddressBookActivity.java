package com.pompom.group6.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pompom.group6.adapters.AddressAdapter;
import com.pompom.group6.databinding.ActivityAddressBookBinding;
import com.pompom.group6.models.Address;
import com.pompom.group6.network.ApiClient;
import com.pompom.group6.network.Session;
import com.pompom.group6.network.dto.ApiAddress;
import com.pompom.group6.network.dto.AddressRequest;
import com.pompom.group6.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddressBookActivity extends SwipeBackActivity implements AddressAdapter.Listener {

    private ActivityAddressBookBinding binding;
    private String userOid;
    private final List<Address> addresses = new ArrayList<>();
    private AddressAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddressBookBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        UiUtils.applyPinkStatusBar(this);

        binding.header.tvHeaderTitle.setText("Sổ địa chỉ");
        binding.header.btnBack.setOnClickListener(v -> finish());

        userOid = Session.getUserOid(this);

        adapter = new AddressAdapter(addresses, this);
        binding.rvAddresses.setLayoutManager(new LinearLayoutManager(this));
        binding.rvAddresses.setAdapter(adapter);

        binding.emptyState.tvEmptyText.setText("Bạn chưa có địa chỉ giao hàng nào");
        binding.emptyState.ivEmptyIcon.setImageResource(com.pompom.group6.R.drawable.ic_pin);

        binding.btnAddAddress.setOnClickListener(v -> showAddDialog());

        loadAddresses();
    }

    private void loadAddresses() {
        if (userOid == null) {
            binding.emptyState.getRoot().setVisibility(View.VISIBLE);
            return;
        }
        ApiClient.get().getAddresses(userOid).enqueue(new Callback<List<ApiAddress>>() {
            @Override
            public void onResponse(Call<List<ApiAddress>> call, Response<List<ApiAddress>> resp) {
                if (binding == null) return;
                addresses.clear();
                if (resp.isSuccessful() && resp.body() != null) {
                    for (ApiAddress a : resp.body()) {
                        addresses.add(new Address(a.id, a.label, a.recipientName, a.phone,
                                a.addressLine, a.ward, a.district, a.city, a.isDefault));
                    }
                }
                adapter.notifyDataSetChanged();
                binding.emptyState.getRoot().setVisibility(addresses.isEmpty() ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onFailure(Call<List<ApiAddress>> call, Throwable t) {
                if (binding == null) return;
                binding.emptyState.getRoot().setVisibility(addresses.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void showAddDialog() {
        int pad = (int) (20 * getResources().getDisplayMetrics().density);
        LinearLayout container = new LinearLayout(this);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setPadding(pad, pad / 2, pad, 0);

        final EditText etLabel = field("Nhãn (Nhà, Công ty...)");
        final EditText etName = field("Tên người nhận");
        final EditText etPhone = field("Số điện thoại");
        final EditText etLine = field("Địa chỉ (số nhà, đường)");
        final EditText etWard = field("Phường/Xã");
        final EditText etDistrict = field("Quận/Huyện");
        final EditText etCity = field("Tỉnh/Thành phố");
        container.addView(etLabel);
        container.addView(etName);
        container.addView(etPhone);
        container.addView(etLine);
        container.addView(etWard);
        container.addView(etDistrict);
        container.addView(etCity);

        androidx.core.widget.NestedScrollView scroll = new androidx.core.widget.NestedScrollView(this);
        scroll.addView(container);

        new AlertDialog.Builder(this)
                .setTitle("Thêm địa chỉ")
                .setView(scroll)
                .setPositiveButton("Lưu", (d, w) -> {
                    String name = etName.getText().toString().trim();
                    String phone = etPhone.getText().toString().trim();
                    String line = etLine.getText().toString().trim();
                    if (name.isEmpty() || phone.isEmpty() || line.isEmpty()) {
                        Toast.makeText(this, "Vui lòng nhập tên, SĐT và địa chỉ", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (userOid == null) {
                        Toast.makeText(this, "Bạn cần đăng nhập", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    boolean makeDefault = addresses.isEmpty(); // first address becomes default
                    AddressRequest body = new AddressRequest(
                            emptyToDefault(etLabel.getText().toString().trim(), "Địa chỉ"),
                            name, phone, line,
                            etWard.getText().toString().trim(),
                            etDistrict.getText().toString().trim(),
                            etCity.getText().toString().trim(),
                            makeDefault);
                    ApiClient.get().addAddress(userOid, body).enqueue(new Callback<ApiAddress>() {
                        @Override
                        public void onResponse(Call<ApiAddress> call, Response<ApiAddress> resp) {
                            if (resp.isSuccessful()) {
                                Toast.makeText(AddressBookActivity.this, "Đã thêm địa chỉ", Toast.LENGTH_SHORT).show();
                                loadAddresses();
                            } else {
                                Toast.makeText(AddressBookActivity.this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<ApiAddress> call, Throwable t) {
                            Toast.makeText(AddressBookActivity.this, "Không kết nối được máy chủ", Toast.LENGTH_SHORT).show();
                        }
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    private EditText field(String hint) {
        EditText et = new EditText(this);
        et.setHint(hint);
        return et;
    }

    private String emptyToDefault(String value, String fallback) {
        return value.isEmpty() ? fallback : value;
    }

    @Override
    public void onDelete(Address address) {
        new AlertDialog.Builder(this)
                .setTitle("Xóa địa chỉ")
                .setMessage("Bạn có chắc muốn xóa địa chỉ này?")
                .setPositiveButton("Xóa", (d, w) -> {
                    if (userOid == null) return;
                    ApiClient.get().deleteAddress(userOid, address.getAddressId()).enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> resp) {
                            if (resp.isSuccessful()) {
                                Toast.makeText(AddressBookActivity.this, "Đã xóa", Toast.LENGTH_SHORT).show();
                                loadAddresses();
                            }
                        }
                        @Override public void onFailure(Call<Void> call, Throwable t) {}
                    });
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onSetDefault(Address address) {
        if (userOid == null) return;
        ApiClient.get().setDefaultAddress(userOid, address.getAddressId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> resp) {
                if (resp.isSuccessful()) loadAddresses();
            }
            @Override public void onFailure(Call<Void> call, Throwable t) {}
        });
    }
}
