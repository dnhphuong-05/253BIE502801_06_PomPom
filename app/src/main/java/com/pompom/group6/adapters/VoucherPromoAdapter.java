package com.pompom.group6.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pompom.group6.R;
import com.pompom.group6.models.Voucher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * Voucher list rendered over a promotion background image, with a "Lưu" button.
 * Reused by the membership page ("Ưu đãi dành riêng cho bạn") and the
 * "Voucher của tôi" screen.
 */
public class VoucherPromoAdapter extends RecyclerView.Adapter<VoucherPromoAdapter.VoucherViewHolder> {

    private static final int[] BACKGROUNDS = {
            R.drawable.promotion,
            R.drawable.promotion1,
            R.drawable.promotion2,
            R.drawable.promotion3
    };

    /** Called when the user taps "Lưu"; should persist the voucher and return whether it succeeded. */
    public interface OnVoucherSaveListener {
        boolean onSave(Voucher voucher);
    }

    private final List<Voucher> vouchers = new ArrayList<>();
    private final Set<Integer> savedIds = new HashSet<>();
    private boolean used = false;
    private OnVoucherSaveListener saveListener;

    public VoucherPromoAdapter() {
    }

    public VoucherPromoAdapter(List<Voucher> vouchers) {
        this.vouchers.addAll(vouchers);
    }

    /** When true, the button shows "Đã dùng" and is disabled. */
    public void setUsed(boolean used) {
        this.used = used;
    }

    /** Vouchers already saved by the user render as "Đã lưu" (disabled). */
    public void setSavedVoucherIds(Set<Integer> ids) {
        savedIds.clear();
        if (ids != null) {
            savedIds.addAll(ids);
        }
    }

    public void setOnSaveListener(OnVoucherSaveListener listener) {
        this.saveListener = listener;
    }

    public void setVouchers(List<Voucher> newVouchers) {
        vouchers.clear();
        vouchers.addAll(newVouchers);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VoucherViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_voucher_promo, parent, false);
        return new VoucherViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VoucherViewHolder holder, int position) {
        Voucher voucher = vouchers.get(position);

        holder.ivBg.setImageResource(BACKGROUNDS[position % BACKGROUNDS.length]);

        if ("percent".equals(voucher.getDiscountType())) {
            holder.tvTitle.setText(String.format(Locale.getDefault(), "%d%% OFF", (int) voucher.getDiscountValue()));
        } else {
            holder.tvTitle.setText(String.format(Locale.getDefault(), "Giảm %,.0fđ", voucher.getDiscountValue()));
        }

        if (voucher.getMinOrderAmount() > 0) {
            holder.tvCondition.setText(String.format(Locale.getDefault(),
                    "Cho đơn từ %,.0fđ", voucher.getMinOrderAmount()));
        } else {
            holder.tvCondition.setText("Cho tất cả sản phẩm");
        }
        holder.tvExpiry.setText("HSD: " + voucher.getExpiryDate());

        if (used) {
            holder.btnSave.setText("Đã dùng");
            holder.btnSave.setEnabled(false);
            holder.btnSave.setAlpha(0.6f);
            holder.btnSave.setOnClickListener(null);
        } else if (savedIds.contains(voucher.getId())) {
            holder.btnSave.setText("Đã lưu");
            holder.btnSave.setEnabled(false);
            holder.btnSave.setAlpha(0.6f);
            holder.btnSave.setOnClickListener(null);
        } else {
            holder.btnSave.setText("Lưu");
            holder.btnSave.setEnabled(true);
            holder.btnSave.setAlpha(1f);
            holder.btnSave.setOnClickListener(v -> onSaveClicked(holder, voucher));
        }
    }

    private void onSaveClicked(VoucherViewHolder holder, Voucher voucher) {
        if (saveListener == null) {
            // No persistence wired up: fall back to a simple confirmation.
            Toast.makeText(holder.itemView.getContext(),
                    "Đã lưu voucher " + voucher.getCode(), Toast.LENGTH_SHORT).show();
            return;
        }
        boolean ok = saveListener.onSave(voucher);
        if (ok) {
            savedIds.add(voucher.getId());
            int pos = holder.getBindingAdapterPosition();
            if (pos != RecyclerView.NO_POSITION) {
                notifyItemChanged(pos);
            }
            Toast.makeText(holder.itemView.getContext(),
                    "Đã lưu voucher " + voucher.getCode(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(holder.itemView.getContext(),
                    "Lưu voucher thất bại, vui lòng thử lại", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public int getItemCount() {
        return vouchers.size();
    }

    static class VoucherViewHolder extends RecyclerView.ViewHolder {
        ImageView ivBg;
        TextView tvTitle, tvCondition, tvExpiry, btnSave;

        public VoucherViewHolder(@NonNull View itemView) {
            super(itemView);
            ivBg = itemView.findViewById(R.id.ivVoucherBg);
            tvTitle = itemView.findViewById(R.id.tvVoucherTitle);
            tvCondition = itemView.findViewById(R.id.tvVoucherCondition);
            tvExpiry = itemView.findViewById(R.id.tvVoucherExpiry);
            btnSave = itemView.findViewById(R.id.btnSave);
        }
    }
}
