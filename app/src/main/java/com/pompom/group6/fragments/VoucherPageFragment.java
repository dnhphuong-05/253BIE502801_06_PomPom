package com.pompom.group6.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pompom.group6.adapters.VoucherPromoAdapter;
import com.pompom.group6.database.PromotionDAO;
import com.pompom.group6.databinding.FragmentVoucherPageBinding;
import com.pompom.group6.models.Voucher;

import java.util.ArrayList;
import java.util.List;

/**
 * A single tab page inside the "Voucher của tôi" screen.
 * Tab types: 0 = Tất cả, 1 = Sắp hết hạn, 2 = Đã dùng.
 */
public class VoucherPageFragment extends Fragment {

    private static final String ARG_TAB_TYPE = "tab_type";

    public static final int TYPE_ALL = 0;
    public static final int TYPE_EXPIRING = 1;
    public static final int TYPE_USED = 2;

    private FragmentVoucherPageBinding binding;

    public static VoucherPageFragment newInstance(int tabType) {
        VoucherPageFragment fragment = new VoucherPageFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TAB_TYPE, tabType);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentVoucherPageBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int tabType = getArguments() != null ? getArguments().getInt(ARG_TAB_TYPE, TYPE_ALL) : TYPE_ALL;

        PromotionDAO promotionDAO = new PromotionDAO(requireContext());
        List<Voucher> vouchers = loadForType(promotionDAO, tabType);

        if (vouchers.isEmpty()) {
            binding.rvVouchers.setVisibility(View.GONE);
            binding.tvEmptyState.setVisibility(View.VISIBLE);
            binding.tvEmptyState.setText(emptyMessage(tabType));
            return;
        }

        VoucherPromoAdapter adapter = new VoucherPromoAdapter();
        adapter.setUsed(tabType == TYPE_USED);
        binding.rvVouchers.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.rvVouchers.setAdapter(adapter);
        adapter.setVouchers(vouchers);
    }

    private List<Voucher> loadForType(PromotionDAO dao, int tabType) {
        List<Voucher> all = dao.getAllVouchers();
        switch (tabType) {
            case TYPE_USED:
                // No per-user usage history is stored yet.
                return new ArrayList<>();
            case TYPE_EXPIRING:
                // Vouchers close to running out of uses are treated as "expiring".
                List<Voucher> expiring = new ArrayList<>();
                for (Voucher v : all) {
                    if (v.getRemainingCount() <= 10) {
                        expiring.add(v);
                    }
                }
                return expiring;
            case TYPE_ALL:
            default:
                return all;
        }
    }

    private String emptyMessage(int tabType) {
        switch (tabType) {
            case TYPE_USED:
                return "Bạn chưa dùng voucher nào";
            case TYPE_EXPIRING:
                return "Không có voucher sắp hết hạn";
            default:
                return "Chưa có voucher nào";
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
