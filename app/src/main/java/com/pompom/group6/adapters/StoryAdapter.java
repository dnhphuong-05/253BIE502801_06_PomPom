package com.pompom.group6.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.pompom.group6.R;
import com.pompom.group6.activities.AddStoryActivity;
import com.pompom.group6.activities.StoryViewerActivity;
import com.pompom.group6.models.Story;
import com.pompom.group6.network.dto.ApiNearbyPost;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class StoryAdapter extends RecyclerView.Adapter<StoryAdapter.StoryViewHolder> {

    private final List<Story> stories;

    public StoryAdapter(List<Story> stories) {
        this.stories = stories;
    }

    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_community_story, parent, false);
        return new StoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        Story story = stories.get(position);

        if (story.isAddButton()) {
            holder.storyContainer.setVisibility(View.VISIBLE);
            holder.normalStoryLayout.setVisibility(View.GONE);
            holder.itemView.setOnClickListener(v ->
                    v.getContext().startActivity(new Intent(v.getContext(), AddStoryActivity.class)));
        } else {
            holder.storyContainer.setVisibility(View.GONE);
            holder.normalStoryLayout.setVisibility(View.VISIBLE);
            holder.ivAddStory.setVisibility(View.GONE);
            holder.tvLiveBadge.setVisibility(View.GONE);

            ApiNearbyPost post = story.getPost();
            holder.tvStoryName.setText(post.userName != null ? post.userName : "Người dùng");
            holder.tvStorySubtitle.setText(timeAgo(post.createdAt));

            Glide.with(holder.itemView.getContext())
                    .load(post.userAvatar)
                    .placeholder(R.drawable.ic_avatar)
                    .into(holder.ivStoryAvatar);

            // Story hiện nội dung (ảnh/video) làm nền thẻ thay vì chỉ avatar tròn — với video dùng
            // luôn khung hình đầu làm thumbnail, Glide tự lấy frame nếu server không có ảnh riêng.
            Glide.with(holder.itemView.getContext())
                    .load(post.mediaUrl)
                    .placeholder(R.drawable.logo_pompom)
                    .centerCrop()
                    .into(holder.ivStoryMedia);

            holder.itemView.setOnClickListener(v -> {
                ArrayList<ApiNearbyPost> posts = new ArrayList<>();
                int startIndex = 0;
                for (Story s : stories) {
                    if (s.isAddButton()) continue;
                    if (s.getPost() == post) startIndex = posts.size();
                    posts.add(s.getPost());
                }
                Intent intent = new Intent(v.getContext(), StoryViewerActivity.class);
                intent.putExtra(StoryViewerActivity.EXTRA_POSTS, posts);
                intent.putExtra(StoryViewerActivity.EXTRA_START_INDEX, startIndex);
                v.getContext().startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    /** "vài phút trước" / "x giờ trước" từ chuỗi ISO 8601 (created_at của backend). */
    private String timeAgo(String isoDate) {
        if (isoDate == null) return "";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            Date date = sdf.parse(isoDate.length() >= 19 ? isoDate.substring(0, 19) : isoDate);
            if (date == null) return "";
            long diffMs = System.currentTimeMillis() - date.getTime();
            long minutes = diffMs / 60000;
            if (minutes < 1) return "Vừa xong";
            if (minutes < 60) return minutes + " phút trước";
            long hours = minutes / 60;
            return hours + " giờ trước";
        } catch (ParseException e) {
            return "";
        }
    }

    public static class StoryViewHolder extends RecyclerView.ViewHolder {
        ImageView ivStoryAvatar, ivStoryMedia, ivAddStory;
        TextView tvStoryName, tvStorySubtitle, tvLiveBadge;
        View storyContainer, normalStoryLayout;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivStoryAvatar = itemView.findViewById(R.id.ivStoryAvatar);
            ivStoryMedia = itemView.findViewById(R.id.ivStoryMedia);
            ivAddStory = itemView.findViewById(R.id.ivAddStory);
            tvStoryName = itemView.findViewById(R.id.tvStoryName);
            tvStorySubtitle = itemView.findViewById(R.id.tvStorySubtitle);
            tvLiveBadge = itemView.findViewById(R.id.tvLiveBadge);
            storyContainer = itemView.findViewById(R.id.storyContainer);
            normalStoryLayout = itemView.findViewById(R.id.normalStoryLayout);
        }
    }
}
