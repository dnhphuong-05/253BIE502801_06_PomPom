package com.pompom.group6.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pompom.group6.R;
import com.pompom.group6.network.dto.ApiNotification;
import com.pompom.group6.utils.TimeUtils;

import java.util.List;

/**
 * Danh sách thông báo thật (khớp GET /api/notifications) — 2 dạng: tương tác xã hội
 * (like/comment/follow, có actor) và khuyến mãi hệ thống (promotion, có title/image_url).
 */
public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotifViewHolder> {
    private final List<ApiNotification> items;

    public interface OnNotificationClick {
        void onClick(ApiNotification notification);
    }

    private final OnNotificationClick onClick;

    public NotificationAdapter(List<ApiNotification> items, OnNotificationClick onClick) {
        this.items = items;
        this.onClick = onClick;
    }

    @NonNull
    @Override
    public NotifViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notification, parent, false);
        return new NotifViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull NotifViewHolder holder, int position) {
        ApiNotification n = items.get(position);
        boolean isPromotion = "promotion".equals(n.type);

        if (isPromotion) {
            Glide.with(holder.itemView.getContext())
                    .load(n.imageUrl)
                    .placeholder(R.drawable.ic_gift)
                    .into(holder.ivActorAvatar);
            String title = n.title != null ? n.title : "Ưu đãi từ PomPom";
            holder.tvMessage.setText(title + (n.message != null ? " — " + n.message : ""));
            holder.ivTypeBadge.setImageResource(R.drawable.ic_gift);
        } else {
            Glide.with(holder.itemView.getContext())
                    .load(n.actorAvatar)
                    .placeholder(R.drawable.ic_avatar)
                    .into(holder.ivActorAvatar);
            String actor = n.actorName != null ? n.actorName : "Người dùng";
            holder.tvMessage.setText(actor + " " + (n.message != null ? n.message : ""));
            holder.ivTypeBadge.setImageResource(
                    "comment".equals(n.type) ? R.drawable.ic_comment
                            : "follow".equals(n.type) ? R.drawable.ic_avatar
                            : R.drawable.ic_heart);
        }

        holder.tvTime.setText(TimeUtils.relativeTime(n.createdAt));
        holder.dotUnread.setVisibility(n.isRead ? View.GONE : View.VISIBLE);

        holder.itemView.setOnClickListener(v -> {
            if (onClick != null) onClick.onClick(n);
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class NotifViewHolder extends RecyclerView.ViewHolder {
        ImageView ivActorAvatar, ivTypeBadge;
        TextView tvMessage, tvTime;
        View dotUnread;

        NotifViewHolder(@NonNull View itemView) {
            super(itemView);
            ivActorAvatar = itemView.findViewById(R.id.ivActorAvatar);
            ivTypeBadge = itemView.findViewById(R.id.ivTypeBadge);
            tvMessage = itemView.findViewById(R.id.tvNotifMessage);
            tvTime = itemView.findViewById(R.id.tvNotifTime);
            dotUnread = itemView.findViewById(R.id.dotUnread);
        }
    }
}
