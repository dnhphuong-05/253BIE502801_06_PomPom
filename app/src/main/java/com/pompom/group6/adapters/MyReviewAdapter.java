package com.pompom.group6.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pompom.group6.R;
import com.pompom.group6.databinding.ItemMyReviewBinding;
import com.pompom.group6.network.dto.ApiMyReview;
import com.pompom.group6.utils.ProfileFormat;

import java.util.List;

/** Danh sách đánh giá do chính user viết (màn "Đánh giá của tôi"). */
public class MyReviewAdapter extends RecyclerView.Adapter<MyReviewAdapter.VH> {

    private final List<ApiMyReview> items;

    public MyReviewAdapter(List<ApiMyReview> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(ItemMyReviewBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        ApiMyReview r = items.get(position);

        h.b.tvProductName.setText(r.productName != null ? r.productName : "Sản phẩm");
        h.b.ratingBar.setRating(r.rating);

        String date = ProfileFormat.birthDate(r.createdAt);
        h.b.tvDate.setText(date != null ? date : "");

        String comment = ProfileFormat.orNull(r.comment);
        h.b.tvComment.setVisibility(comment != null ? View.VISIBLE : View.GONE);
        if (comment != null) h.b.tvComment.setText(comment);

        Glide.with(h.itemView.getContext())
                .load(r.productThumbnail)
                .placeholder(R.drawable.ic_avatar)
                .error(R.drawable.ic_avatar)
                .into(h.b.ivProduct);
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class VH extends RecyclerView.ViewHolder {
        final ItemMyReviewBinding b;
        VH(ItemMyReviewBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }
}
