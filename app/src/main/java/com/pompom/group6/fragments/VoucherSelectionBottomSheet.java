package com.pompom.group6.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.pompom.group6.adapters.VoucherSelectableAdapter;
import com.pompom.group6.databinding.DialogVoucherBottomSheetBinding;
import com.pompom.group6.models.Voucher;

import java.util.ArrayList;
import java.util.List;

public class VoucherSelectionBottomSheet extends BottomSheetDialogFragment {

    private static final String SHEET_TAG = "VoucherSelectionSheet";

    public interface OnVoucherSelectedListener {
        void onVoucherSelected(String voucherCode);
    }

    private DialogVoucherBottomSheetBinding binding;
    private VoucherSelectableAdapter adapter;
    private OnVoucherSelectedListener listener;
    private List<Voucher> allVouchers = new ArrayList<>();

    // ── Factory ───────────────────────────────────────────────────────────────────

    public static void show(FragmentManager fm, OnVoucherSelectedListener listener) {
        VoucherSelectionBottomSheet sheet = new VoucherSelectionBottomSheet();
        sheet.listener = listener;
        sheet.show(fm, SHEET_TAG);
    }

    // ── Lifecycle ─────────────────────────────────────────────────────────────────

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = DialogVoucherBottomSheetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // ── 70% height ────────────────────────────────────────────────────────────
        view.post(() -> {
            BottomSheetDialog dialog = (BottomSheetDialog) getDialog();
            if (dialog != null) {
                View sheet = dialog.findViewById(com.google.android.material.R.id.design_bottom_sheet);
                if (sheet != null) {
                    int screenHeight = requireContext().getResources().getDisplayMetrics().heightPixels;
                    int peekH = (int) (screenHeight * 0.70f);
                    BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(sheet);
                    behavior.setPeekHeight(peekH);
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }
            }
        });

        allVouchers = buildMockVouchers();
        setupRecyclerView(allVouchers);
        setupSearch();
        setupApplyButton();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    // ── RecyclerView ──────────────────────────────────────────────────────────────

    private void setupRecyclerView(List<Voucher> vouchers) {
        adapter = new VoucherSelectableAdapter(vouchers);
        binding.rvVouchers.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvVouchers.setAdapter(adapter);
    }

    // ── Search ────────────────────────────────────────────────────────────────────

    private void setupSearch() {
        binding.etVoucherSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterVouchers(s.toString().trim());
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        binding.btnVoucherSearch.setOnClickListener(v ->
                filterVouchers(binding.etVoucherSearch.getText().toString().trim()));
    }

    private void filterVouchers(String query) {
        if (query.isEmpty()) {
            adapter.updateList(allVouchers);
            return;
        }
        String lower = query.toLowerCase();
        List<Voucher> filtered = new ArrayList<>();
        for (Voucher v : allVouchers) {
            if (v.getCode().toLowerCase().contains(lower)
                    || v.getDiscountType().toLowerCase().contains(lower)) {
                filtered.add(v);
            }
        }
        adapter.updateList(filtered);
    }

    // ── Apply button ──────────────────────────────────────────────────────────────

    private void setupApplyButton() {
        binding.btnApplyVoucher.setOnClickListener(v -> {
            Voucher selected = adapter.getSelectedVoucher();
            if (listener != null) {
                listener.onVoucherSelected(selected != null ? selected.getCode() : null);
            }
            dismiss();
        });
    }

    // ── Mock data (using existing Voucher constructor) ────────────────────────────
    // Voucher(int id, String code, String discountType, double discountValue,
    //         double minOrderAmount, String expiryDate, int remainingCount)

    private List<Voucher> buildMockVouchers() {
        List<Voucher> list = new ArrayList<>();
        list.add(new Voucher(1, "POMPOM20",  "percent",  20.0, 300000, "30/07/2026", 5));
        list.add(new Voucher(2, "FREESHIP",  "fixed",    30000, 200000, "31/07/2026", 12));
        list.add(new Voucher(3, "SALE50K",   "fixed",    50000, 500000, "15/07/2026", 3));
        list.add(new Voucher(4, "NEWUSER10", "percent",  10.0, 100000, "31/12/2026", 1));
        list.add(new Voucher(5, "BIRTHDAY",  "percent",  30.0, 200000, "05/07/2026", 2));
        return list;
    }
}
