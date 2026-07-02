package com.pompom.group6.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pompom.group6.R;
import com.pompom.group6.activities.StoryViewerActivity;
import com.pompom.group6.models.Story;

import java.util.ArrayList;
import java.util.List;

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
        
        if (story.isCreateRoom()) {
            holder.storyContainer.setVisibility(View.VISIBLE);
            holder.normalStoryLayout.setVisibility(View.GONE);
        } else {
            holder.storyContainer.setVisibility(View.GONE);
            holder.normalStoryLayout.setVisibility(View.VISIBLE);
            
            holder.tvStoryName.setText(story.getName());
            holder.tvStorySubtitle.setText(story.getSubtitle());
            holder.ivStoryAvatar.setImageResource(story.getImageResId());
            
            holder.ivAddStory.setVisibility(story.isUserStory() ? View.VISIBLE : View.GONE);
            holder.tvLiveBadge.setVisibility(story.isLive() ? View.VISIBLE : View.GONE);
            
            // Subtitle color logic
            if (story.isLive()) {
                holder.tvStorySubtitle.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.brand_pink));
            } else {
                holder.tvStorySubtitle.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.text_secondary));
            }

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(v.getContext(), StoryViewerActivity.class);
                
                // Pass full list for auto-next
                ArrayList<String> names = new ArrayList<>();
                ArrayList<Integer> images = new ArrayList<>();
                int startIndex = 0;
                
                for (int i = 0; i < stories.size(); i++) {
                    if (stories.get(i).isCreateRoom()) continue;
                    names.add(stories.get(i).getName());
                    images.add(stories.get(i).getImageResId());
                    if (stories.get(i).getId() == story.getId()) {
                        startIndex = names.size() - 1;
                    }
                }
                
                intent.putStringArrayListExtra("names", names);
                intent.putIntegerArrayListExtra("images", images);
                intent.putExtra("startIndex", startIndex);
                v.getContext().startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    public static class StoryViewHolder extends RecyclerView.ViewHolder {
        ImageView ivStoryAvatar, ivAddStory;
        TextView tvStoryName, tvStorySubtitle, tvLiveBadge;
        View storyContainer, normalStoryLayout;

        public StoryViewHolder(@NonNull View itemView) {
            super(itemView);
            ivStoryAvatar = itemView.findViewById(R.id.ivStoryAvatar);
            ivAddStory = itemView.findViewById(R.id.ivAddStory);
            tvStoryName = itemView.findViewById(R.id.tvStoryName);
            tvStorySubtitle = itemView.findViewById(R.id.tvStorySubtitle);
            tvLiveBadge = itemView.findViewById(R.id.tvLiveBadge);
            storyContainer = itemView.findViewById(R.id.storyContainer);
            normalStoryLayout = itemView.findViewById(R.id.normalStoryLayout);
        }
    }
}
