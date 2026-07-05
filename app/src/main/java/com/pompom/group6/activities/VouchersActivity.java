package com.pompom.group6.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pompom.group6.R;
import com.pompom.group6.adapters.VoucherAdapter;
import com.pompom.group6.database.UserDAO;
import com.pompom.group6.databinding.ActivityVouchersBinding;
import com.pompom.group6.models.Voucher;
import com.pompom.group6.utils.UiUtils;

import java.util.List;

public class VouchersActivity extends SwipeBackActivity {

    private ActivityVouchersBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVouchersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        UiUtils.applyPinkStatusBar(this);

        binding.header.tvHeaderTitle.setText("Voucher của tôi");
        binding.header.btnBack.setOnClickListener(v -> finish());

        UserDAO userDAO = new UserDAO(this);
        SharedPreferences prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", 1);

        List<Voucher> vouchers = userDAO.getUserVouchers(userId, false);

        binding.rvVouchers.setLayoutManager(new LinearLayoutManager(this));
        binding.rvVouchers.setAdapter(new VoucherAdapter(vouchers));

        binding.emptyState.tvEmptyText.setText("Bạn chưa có voucher nào");
        binding.emptyState.ivEmptyIcon.setImageResource(R.drawable.ic_gift);
        binding.emptyState.getRoot().setVisibility(vouchers.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
