package com.pompom.group6.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pompom.group6.R;

import java.util.List;

public class PostImageSliderAdapter extends RecyclerView.Adapter<PostImageSliderAdapter.SliderViewHolder> {

    private final List<String> imageUrls;
    private OnImageLongClickListener longClickListener;

    public interface OnImageLongClickListener {
        void onImageLongClick(String imageUrl);
    }

    public PostImageSliderAdapter(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public void setOnImageLongClickListener(OnImageLongClickListener listener) {
        this.longClickListener = listener;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_post_image_slide, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        String url = imageUrls.get(position);
        Glide.with(holder.imageView.getContext())
                .load(url)
                .placeholder(R.drawable.promotion1)
                .centerCrop()
                .into(holder.imageView);

        holder.imageView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onImageLongClick(url);
                return true;
            }
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return imageUrls.size();
    }

    static class SliderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.ivSlideImage);
        }
    }
}
