package com.pompom.group6.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.pompom.group6.R;
import com.pompom.group6.databinding.ItemPointsTransactionBinding;
import com.pompom.group6.models.PointsTransaction;

import java.util.List;
import java.util.Locale;

public class PointsTransactionAdapter extends RecyclerView.Adapter<PointsTransactionAdapter.VH> {

    private final List<PointsTransaction> items;

    public PointsTransactionAdapter(List<PointsTransaction> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPointsTransactionBinding b = ItemPointsTransactionBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(b);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        PointsTransaction t = items.get(position);
        holder.b.tvReason.setText(t.getReasonLabel());
        holder.b.tvDate.setText(safeDate(t.getCreatedAt()));

        int change = t.getPointsChange();
        holder.b.tvPointsChange.setText(String.format(Locale.getDefault(),
                "%s%d", change >= 0 ? "+" : "", change));
        int color = change >= 0 ? R.color.brand_pink : R.color.text_secondary;
        holder.b.tvPointsChange.setTextColor(
                ContextCompat.getColor(holder.itemView.getContext(), color));
    }

    private String safeDate(String raw) {
        if (raw == null) return "";
        return raw.length() >= 10 ? raw.substring(0, 10) : raw;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final ItemPointsTransactionBinding b;

        VH(ItemPointsTransactionBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }
}
