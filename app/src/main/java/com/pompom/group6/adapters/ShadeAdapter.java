package com.pompom.group6.adapters;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.pompom.group6.R;
import com.pompom.group6.models.Shade;

import java.util.List;

/**
 * Horizontal swatch picker shared by AI Makeup Artist and AR Try-on.
 * Highlights the selected swatch with a ring and reports selection through a listener.
 */
public class ShadeAdapter extends RecyclerView.Adapter<ShadeAdapter.ShadeViewHolder> {

    public interface OnShadeSelectedListener {
        void onShadeSelected(Shade shade, int position);
    }

    private final List<Shade> shades;
    private final OnShadeSelectedListener listener;
    private int selectedPosition = 0;

    public ShadeAdapter(List<Shade> shades, OnShadeSelectedListener listener) {
        this.shades = shades;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ShadeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_shade_swatch, parent, false);
        return new ShadeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShadeViewHolder holder, int position) {
        Shade shade = shades.get(position);
        int color = ContextCompat.getColor(holder.itemView.getContext(), shade.getColorRes());

        holder.circle.setBackgroundTintList(ColorStateList.valueOf(color));
        holder.name.setText(shade.getName());
        holder.ring.setVisibility(position == selectedPosition ? View.VISIBLE : View.INVISIBLE);

        holder.itemView.setOnClickListener(v -> {
            int adapterPos = holder.getAdapterPosition();
            if (adapterPos == RecyclerView.NO_POSITION) return;
            selectShade(adapterPos);
            if (listener != null) {
                listener.onShadeSelected(shades.get(adapterPos), adapterPos);
            }
        });
    }

    private void selectShade(int position) {
        int previous = selectedPosition;
        selectedPosition = position;
        notifyItemChanged(previous);
        notifyItemChanged(selectedPosition);
    }

    @Override
    public int getItemCount() {
        return shades.size();
    }

    static class ShadeViewHolder extends RecyclerView.ViewHolder {
        final View circle;
        final View ring;
        final TextView name;

        ShadeViewHolder(@NonNull View itemView) {
            super(itemView);
            circle = itemView.findViewById(R.id.v_shade_circle);
            ring = itemView.findViewById(R.id.v_shade_ring);
            name = itemView.findViewById(R.id.tv_shade_name);
        }
    }
}
