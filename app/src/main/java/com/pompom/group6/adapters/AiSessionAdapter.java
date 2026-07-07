package com.pompom.group6.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pompom.group6.R;
import com.pompom.group6.models.AiSession;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

/** Danh sách lịch sử các lượt dùng AI Dermatologist/AI Makeup Artist. */
public class AiSessionAdapter extends RecyclerView.Adapter<AiSessionAdapter.SessionViewHolder> {

    public interface OnSessionClickListener {
        void onSessionClick(AiSession session);
    }

    private final List<AiSession> sessions;
    private final OnSessionClickListener listener;

    public AiSessionAdapter(List<AiSession> sessions, OnSessionClickListener listener) {
        this.sessions = sessions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SessionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ai_session, parent, false);
        return new SessionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SessionViewHolder holder, int position) {
        AiSession session = sessions.get(position);
        boolean isDermatologist = AiSession.TYPE_DERMATOLOGIST.equals(session.getAiType());

        holder.ivTypeIcon.setImageResource(isDermatologist ? R.drawable.ic_steth : R.drawable.ic_makeup_brush);
        holder.tvTypeName.setText(isDermatologist ? "AI Dermatologist" : "AI Makeup Artist");
        holder.tvSummary.setText(session.getOutputData());
        holder.tvDate.setText(formatDate(session.getCreatedAt()));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onSessionClick(session);
        });
    }

    private String formatDate(String rawCreatedAt) {
        if (rawCreatedAt == null || rawCreatedAt.isEmpty()) return "";
        try {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
            SimpleDateFormat output = new SimpleDateFormat("HH:mm dd/MM/yyyy", Locale.getDefault());
            return output.format(input.parse(rawCreatedAt));
        } catch (Exception e) {
            return rawCreatedAt;
        }
    }

    @Override
    public int getItemCount() {
        return sessions.size();
    }

    static class SessionViewHolder extends RecyclerView.ViewHolder {
        ImageView ivTypeIcon;
        TextView tvTypeName, tvSummary, tvDate;

        SessionViewHolder(@NonNull View itemView) {
            super(itemView);
            ivTypeIcon = itemView.findViewById(R.id.iv_type_icon);
            tvTypeName = itemView.findViewById(R.id.tv_type_name);
            tvSummary = itemView.findViewById(R.id.tv_summary);
            tvDate = itemView.findViewById(R.id.tv_date);
        }
    }
}
