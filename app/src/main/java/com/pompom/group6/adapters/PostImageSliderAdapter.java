package com.pompom.group6.adapters;

import android.content.Context;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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
    private OnImageTapListener tapListener;

    public interface OnImageLongClickListener {
        void onImageLongClick(String imageUrl);
    }

    /** Chạm 1 lần trên ảnh -> mở chi tiết bài viết; chạm đúp -> thích nhanh (kiểu Instagram). */
    public interface OnImageTapListener {
        void onSingleTap();
        void onDoubleTap();
    }

    public PostImageSliderAdapter(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public void setOnImageLongClickListener(OnImageLongClickListener listener) {
        this.longClickListener = listener;
    }

    public void setOnImageTapListener(OnImageTapListener listener) {
        this.tapListener = listener;
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

        // Gắn trực tiếp lên ImageView của từng trang (không phải lên ViewPager2 bao ngoài) —
        // đây mới là view thực sự nhận sự kiện chạm, tương thích với thao tác vuốt đổi trang.
        Context context = holder.imageView.getContext();
        GestureDetector gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(@NonNull MotionEvent e) {
                if (tapListener != null) tapListener.onDoubleTap();
                return true;
            }
            @Override
            public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
                if (tapListener != null) tapListener.onSingleTap();
                return true;
            }
        });
        holder.imageView.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return true;
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
