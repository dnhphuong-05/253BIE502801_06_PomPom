package com.pompom.group6.adapters;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pompom.group6.R;

import java.util.List;

public class ImagePreviewAdapter extends RecyclerView.Adapter<ImagePreviewAdapter.PreviewViewHolder> {

    private final List<Uri> uris;
    private final OnRemoveListener listener;

    public interface OnRemoveListener {
        void onRemove(int position);
    }

    public ImagePreviewAdapter(List<Uri> uris, OnRemoveListener listener) {
        this.uris = uris;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PreviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_preview, parent, false);
        return new PreviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PreviewViewHolder holder, int position) {
        holder.imageView.setImageURI(uris.get(position));
        holder.btnRemove.setOnClickListener(v -> listener.onRemove(position));
    }

    @Override
    public int getItemCount() {
        return uris.size();
    }

    static class PreviewViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView, btnRemove;

        public PreviewViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivPreview);
            btnRemove = itemView.findViewById(R.id.btnRemove);
        }
    }
}
