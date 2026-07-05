package com.pompom.group6.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pompom.group6.R;
import com.pompom.group6.adapters.PointsTransactionAdapter;
import com.pompom.group6.database.UserDAO;
import com.pompom.group6.databinding.ActivityPointsBinding;
import com.pompom.group6.models.PointsTransaction;
import com.pompom.group6.models.User;
import com.pompom.group6.utils.UiUtils;

import java.util.List;
import java.util.Locale;

public class PointsActivity extends SwipeBackActivity {

    private ActivityPointsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPointsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        UiUtils.applyPinkStatusBar(this);

        binding.header.tvHeaderTitle.setText("Điểm của tôi");
        binding.header.btnBack.setOnClickListener(v -> finish());

        UserDAO userDAO = new UserDAO(this);
        SharedPreferences prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);
        int userId = prefs.getInt("user_id", 1);

        User user = userDAO.getUserById(userId);
        if (user != null) {
            binding.tvPoints.setText(String.format(Locale.getDefault(), "%,d", user.getPoints()));
            binding.tvLevel.setText(user.getMembershipLevel());
        }

        List<PointsTransaction> transactions = userDAO.getPointsTransactions(userId);
        binding.rvPoints.setLayoutManager(new LinearLayoutManager(this));
        binding.rvPoints.setAdapter(new PointsTransactionAdapter(transactions));

        binding.emptyState.tvEmptyText.setText("Chưa có giao dịch điểm nào");
        binding.emptyState.ivEmptyIcon.setImageResource(R.drawable.ic_loyalty);
        binding.emptyState.getRoot().setVisibility(transactions.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
