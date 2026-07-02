package com.pompom.group6.adapters;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pompom.group6.R;
import com.pompom.group6.models.CartItem;

import java.util.List;
import java.util.Locale;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartViewHolder> {

    public interface CartItemListener {
        void onQuantityChanged(CartItem item, int newQty);
        void onRemove(CartItem item);
    }

    private final List<CartItem> items;
    private final CartItemListener listener;

    public CartItemAdapter(List<CartItem> items, CartItemListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem item = items.get(position);

        holder.tvName.setText(item.getTitle());
        holder.tvQty.setText(String.valueOf(item.getQuantity()));
        
        // Use Locale.getDefault() or specific for currency
        holder.tvSubtotal.setText(String.format(Locale.getDefault(), "$%.2f", item.getSubtotal()));

        if (item.getOriginalPrice() != null) {
            holder.tvPrice.setVisibility(View.VISIBLE);
            holder.tvPrice.setText(item.getOriginalPrice());
            holder.tvPrice.setPaintFlags(holder.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.tvPrice.setVisibility(View.GONE);
        }

        Glide.with(holder.itemView.getContext())
                .load(item.getImageUrl())
                .placeholder(R.drawable.logo_pompom)
                .into(holder.ivProduct);

        holder.btnPlus.setOnClickListener(v -> listener.onQuantityChanged(item, item.getQuantity() + 1));
        holder.btnMinus.setOnClickListener(v -> {
            if (item.getQuantity() > 1) {
                listener.onQuantityChanged(item, item.getQuantity() - 1);
            }
        });
        holder.btnRemove.setOnClickListener(v -> listener.onRemove(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProduct, btnPlus, btnMinus, btnRemove;
        TextView tvName, tvPrice, tvQty, tvSubtotal;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProduct = itemView.findViewById(R.id.ivCartProduct);
            btnPlus = itemView.findViewById(R.id.btnCartPlus);
            btnMinus = itemView.findViewById(R.id.btnCartMinus);
            btnRemove = itemView.findViewById(R.id.btnCartRemove);
            tvName = itemView.findViewById(R.id.tvCartName);
            tvPrice = itemView.findViewById(R.id.tvCartPrice);
            tvQty = itemView.findViewById(R.id.tvCartQty);
            tvSubtotal = itemView.findViewById(R.id.tvCartSubtotal);
        }
    }
}
