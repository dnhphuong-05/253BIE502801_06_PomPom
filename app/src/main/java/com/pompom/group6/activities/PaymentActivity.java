package com.pompom.group6.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.pompom.group6.databinding.ActivityPaymentBinding;
import com.pompom.group6.utils.UiUtils;

public class PaymentActivity extends AppCompatActivity {

    private ActivityPaymentBinding binding;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPaymentBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        UiUtils.applyPinkStatusBar(this);

        binding.header.tvHeaderTitle.setText("Thanh toán");
        binding.header.btnBack.setOnClickListener(v -> finish());

        prefs = getSharedPreferences("user_prefs", Context.MODE_PRIVATE);

        // Restore the saved method
        String saved = prefs.getString("payment_method", "COD");
        switch (saved) {
            case "VISA": binding.rbVisa.setChecked(true); break;
            case "VNPAY": binding.rbVnpay.setChecked(true); break;
            case "MOMO": binding.rbMomo.setChecked(true); break;
            case "ZALOPAY": binding.rbZalopay.setChecked(true); break;
            default: binding.rbCod.setChecked(true); break;
        }

        binding.rgPayment.setOnCheckedChangeListener((group, checkedId) -> {
            String method = codeFor(checkedId);
            prefs.edit().putString("payment_method", method).apply();
            Toast.makeText(this, "Đã chọn " + labelFor(checkedId), Toast.LENGTH_SHORT).show();
        });
    }

    private String codeFor(int checkedId) {
        if (checkedId == binding.rbVisa.getId()) return "VISA";
        if (checkedId == binding.rbVnpay.getId()) return "VNPAY";
        if (checkedId == binding.rbMomo.getId()) return "MOMO";
        if (checkedId == binding.rbZalopay.getId()) return "ZALOPAY";
        return "COD";
    }

    private String labelFor(int checkedId) {
        if (checkedId == binding.rbVisa.getId()) return "VISA/Mastercard";
        if (checkedId == binding.rbVnpay.getId()) return "VNPay";
        if (checkedId == binding.rbMomo.getId()) return "MoMo";
        if (checkedId == binding.rbZalopay.getId()) return "ZaloPay";
        return "COD";
    }
}
