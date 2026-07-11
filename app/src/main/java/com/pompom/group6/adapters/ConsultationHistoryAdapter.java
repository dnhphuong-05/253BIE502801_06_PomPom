package com.pompom.group6.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pompom.group6.R;
import com.pompom.group6.databinding.ItemConsultationRequestBinding;
import com.pompom.group6.network.dto.ApiConsultationRequest;
import com.pompom.group6.utils.ProfileFormat;

import java.util.List;

/** Danh sách yêu cầu tư vấn đã gửi của user (màn "Lịch sử tư vấn"). */
public class ConsultationHistoryAdapter extends RecyclerView.Adapter<ConsultationHistoryAdapter.VH> {

    private final List<ApiConsultationRequest> items;

    public ConsultationHistoryAdapter(List<ApiConsultationRequest> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new VH(ItemConsultationRequestBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int position) {
        ApiConsultationRequest r = items.get(position);

        h.b.tvExpertName.setText(r.expertName != null ? r.expertName : "Chuyên gia PomPom");
        h.b.tvExpertTitle.setText(r.expertTitle != null ? r.expertTitle : "");
        h.b.tvExpertTitle.setVisibility(r.expertTitle != null ? View.VISIBLE : View.GONE);

        h.b.tvTopic.setText(ProfileFormat.orNull(r.topic) != null ? r.topic : "Yêu cầu tư vấn");

        String message = ProfileFormat.orNull(r.message);
        h.b.tvMessage.setVisibility(message != null ? View.VISIBLE : View.GONE);
        if (message != null) h.b.tvMessage.setText(message);

        String date = ProfileFormat.birthDate(r.createdAt);
        h.b.tvDate.setText(date != null ? "Đã gửi " + date : "");

        bindStatus(h, r.status);

        Glide.with(h.itemView.getContext())
                .load(r.expertAvatar)
                .placeholder(R.drawable.ic_avatar)
                .error(R.drawable.ic_avatar)
                .into(h.b.ivExpertAvatar);
    }

    private void bindStatus(VH h, String status) {
        String s = status != null ? status : "pending";
        switch (s) {
            case "resolved":
            case "completed":
            case "done":
            case "confirmed":
                h.b.tvStatus.setText("Đã hoàn tất");
                h.b.tvStatus.setBackgroundResource(R.drawable.bg_label_green);
                h.b.tvStatus.setTextColor(ContextCompat.getColor(h.itemView.getContext(), R.color.status_green));
                break;
            case "contacted":
                h.b.tvStatus.setText("Đã liên hệ");
                h.b.tvStatus.setBackgroundResource(R.drawable.bg_label_green);
                h.b.tvStatus.setTextColor(ContextCompat.getColor(h.itemView.getContext(), R.color.status_green));
                break;
            case "cancelled":
            case "rejected":
                h.b.tvStatus.setText("Đã huỷ");
                h.b.tvStatus.setBackgroundResource(R.drawable.bg_label_grey);
                h.b.tvStatus.setTextColor(ContextCompat.getColor(h.itemView.getContext(), R.color.text_secondary));
                break;
            default:
                h.b.tvStatus.setText("Chờ xử lý");
                h.b.tvStatus.setBackgroundResource(R.drawable.bg_label_gold);
                h.b.tvStatus.setTextColor(ContextCompat.getColor(h.itemView.getContext(), R.color.white));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    static class VH extends RecyclerView.ViewHolder {
        final ItemConsultationRequestBinding b;
        VH(ItemConsultationRequestBinding b) {
            super(b.getRoot());
            this.b = b;
        }
    }
}
