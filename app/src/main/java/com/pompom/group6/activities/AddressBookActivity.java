package com.pompom.group6.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pompom.group6.adapters.AddressAdapter;
import com.pompom.group6.database.UserDAO;
import com.pompom.group6.databinding.ActivityAddressBookBinding;
import com.pompom.group6.models.Address;
import com.pompom.group6.utils.UiUtils;

import java.util.ArrayList;
import java.util.List;

public class AddressBookActivity extends SwipeBackActivity implements AddressAdapter.Listener {

    private ActivityAddressBookBinding binding;
    private UserDAO userDAO;
    private int userId;
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

        userDAO = new UserDAO(this);
        SharedPreferences prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        userId = prefs.getInt("user_id", 1);

        adapter = new AddressAdapter(addresses, this);
        binding.rvAddresses.setLayoutManager(new LinearLayoutManager(this));
        binding.rvAddresses.setAdapter(adapter);

        binding.emptyState.tvEmptyText.setText("Bạn chưa có địa chỉ giao hàng nào");
        binding.emptyState.ivEmptyIcon.setImageResource(com.pompom.group6.R.drawable.ic_pin);

        binding.btnAddAddress.setOnClickListener(v -> showAddDialog());

        loadAddresses();
    }

    private void loadAddresses() {
        addresses.clear();
        addresses.addAll(userDAO.getAddresses(userId));
        adapter.notifyDataSetChanged();
        binding.emptyState.getRoot().setVisibility(addresses.isEmpty() ? View.VISIBLE : View.GONE);
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
                    boolean makeDefault = addresses.isEmpty(); // first address becomes default
                    long id = userDAO.addAddress(userId,
                            emptyToDefault(etLabel.getText().toString().trim(), "Địa chỉ"),
                            name, phone, line,
                            etWard.getText().toString().trim(),
                            etDistrict.getText().toString().trim(),
                            etCity.getText().toString().trim(),
                            makeDefault);
                    if (id != -1) {
                        Toast.makeText(this, "Đã thêm địa chỉ", Toast.LENGTH_SHORT).show();
                        loadAddresses();
                    } else {
                        Toast.makeText(this, "Thêm thất bại", Toast.LENGTH_SHORT).show();
                    }
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
                    if (userDAO.deleteAddress(address.getAddressId())) {
                        Toast.makeText(this, "Đã xóa", Toast.LENGTH_SHORT).show();
                        loadAddresses();
                    }
                })
                .setNegativeButton("Hủy", null)
                .show();
    }

    @Override
    public void onSetDefault(Address address) {
        if (userDAO.setDefaultAddress(userId, address.getAddressId())) {
            loadAddresses();
        }
    }
}
