package com.pompom.group6.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pompom.group6.R;
import com.pompom.group6.adapters.AddressAdapter;
import com.pompom.group6.databinding.ActivityAddressBookBinding;
import com.pompom.group6.models.Address;
import com.pompom.group6.network.ApiClient;
import com.pompom.group6.network.Session;
import com.pompom.group6.network.dto.ApiAddress;
import com.pompom.group6.network.dto.AddressRequest;
import com.pompom.group6.utils.StatusBarUtils;

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
        StatusBarUtils.applyPinkHeader(this);
        StatusBarUtils.applyHeaderContentInsets(this, binding.header.getRoot(), binding.getRoot());

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
        View content = getLayoutInflater().inflate(R.layout.dialog_add_address, null);
        final EditText etLabel = content.findViewById(R.id.etAddrLabel);
        final EditText etName = content.findViewById(R.id.etAddrName);
        final EditText etPhone = content.findViewById(R.id.etAddrPhone);
        final EditText etLine = content.findViewById(R.id.etAddrLine);
        final EditText etWard = content.findViewById(R.id.etAddrWard);
        final EditText etDistrict = content.findViewById(R.id.etAddrDistrict);
        final EditText etCity = content.findViewById(R.id.etAddrCity);

        AlertDialog dialog = new AlertDialog.Builder(this).setView(content).create();
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(
                    new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));
        }

        content.findViewById(R.id.btnAddrCancel).setOnClickListener(v -> dialog.dismiss());
        content.findViewById(R.id.btnAddrSave).setOnClickListener(v -> {
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
                        dialog.dismiss();
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
        });

        dialog.show();
    }

    private String emptyToDefault(String value, String fallback) {
        return value.isEmpty() ? fallback : value;
    }

    @Override
    public void onDelete(Address address) {
        com.pompom.group6.utils.PomPomDialog.confirm(this, "🗑️", "Xóa địa chỉ",
                "Bạn có chắc muốn xóa địa chỉ này?", "Xóa", "Hủy", () -> {
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
                });
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
