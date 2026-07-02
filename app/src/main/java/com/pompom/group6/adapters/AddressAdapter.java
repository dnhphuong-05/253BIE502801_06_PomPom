package com.pompom.group6.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pompom.group6.databinding.ItemAddressBinding;
import com.pompom.group6.models.Address;

import java.util.List;

public class AddressAdapter extends RecyclerView.Adapter<AddressAdapter.VH> {

    public interface Listener {
        void onDelete(Address address);
        void onSetDefault(Address address);
    }

    private final List<Address> addresses;
    private final Listener listener;

    public AddressAdapter(List<Address> addresses, Listener listener) {
        this.addresses = addresses;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAddressBinding b = ItemAddressBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new VH(b);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Address a = addresses.get(position);
        holder.b.tvLabel.setText(a.getLabel() != null ? a.getLabel() : "Địa chỉ");
        holder.b.tvRecipient.setText(a.getRecipientName() + " · " + a.getPhone());
        holder.b.tvAddress.setText(a.getFullAddress());

        holder.b.tvDefaultBadge.setVisibility(a.isDefault() ? View.VISIBLE : View.GONE);
        holder.b.btnSetDefault.setVisibility(a.isDefault() ? View.GONE : View.VISIBLE);

        holder.b.btnDelete.setOnClickListener(v -> listener.onDelete(a));
        holder.b.btnSetDefault.setOnClickListener(v -> listener.onSetDefault(a));
    }

    @Override
    public int getItemCount() {
        return addresses.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final ItemAddressBinding b;

        VH(ItemAddressBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }
}
