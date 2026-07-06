package com.pompom.group6.adapters;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.pompom.group6.R;
import com.pompom.group6.activities.PostDetailActivity;
import com.pompom.group6.models.CommunityPost;
import com.pompom.group6.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class CommunityPostAdapter extends RecyclerView.Adapter<CommunityPostAdapter.PostViewHolder> {

    private final List<CommunityPost> posts = new ArrayList<>();

    public void setPosts(List<CommunityPost> newPosts) {
        posts.clear();
        posts.addAll(newPosts);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_community_post_card, parent, false);
        return new PostViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostViewHolder holder, int position) {
        CommunityPost post = posts.get(position);
        
        holder.tvUserName.setText(post.getUserName());
        holder.tvPostTitle.setText(post.getContent());
        holder.tvLikeCount.setText(formatCount(post.getLikeCount()));
        holder.tvCommentCount.setText(String.valueOf(post.getCommentCount()));
        holder.tvShareCount.setText("132"); 
        
        holder.tvPostTime.setText("2 giờ trước • " + post.getPostType());

        Glide.with(holder.itemView.getContext())
                .load(post.getUserAvatar())
                .circleCrop()
                .placeholder(R.drawable.ic_avatar)
                .into(holder.ivUserAvatar);

        // SLIDER LOGIC
        List<String> imageList = post.getImages();
        if (imageList != null && !imageList.isEmpty()) {
            holder.layoutPostImages.setVisibility(View.VISIBLE);
            PostImageSliderAdapter sliderAdapter = new PostImageSliderAdapter(imageList);
            sliderAdapter.setOnImageLongClickListener(imageUrl -> showImagePreview(holder.itemView.getContext(), imageUrl));
            holder.vpPostImages.setAdapter(sliderAdapter);
            
            if (imageList.size() > 1) {
                holder.tabIndicator.setVisibility(View.VISIBLE);
                new TabLayoutMediator(holder.tabIndicator, holder.vpPostImages, (tab, pos) -> {}).attach();
            } else {
                holder.tabIndicator.setVisibility(View.GONE);
            }
        } else {
            holder.layoutPostImages.setVisibility(View.GONE);
        }

        bindLikeState(holder, post);
        bindBookmarkState(holder, post);
        // like_count từ server đã tính theo đúng trạng thái is_liked lúc tải — cố định làm mốc
        // để tính lại số hiển thị khi người dùng bấm thích/bỏ thích nhiều lần trong cùng 1 lần bind.
        boolean originalLiked = post.isLiked();
        setupLikeLogic(holder, post, originalLiked);
        setupFollowLogic(holder);
        setupSocialActions(holder, post);

        holder.btnMore.setOnClickListener(v -> showPostMenu(v, holder.getAdapterPosition()));
    }

    private void bindLikeState(PostViewHolder holder, CommunityPost post) {
        holder.layoutLike.setTag(post.isLiked());
        ImageView ivHeart = (ImageView) ((ViewGroup) holder.layoutLike).getChildAt(0);
        int color = ContextCompat.getColor(holder.itemView.getContext(),
                post.isLiked() ? R.color.brand_pink : R.color.text_secondary);
        ivHeart.setColorFilter(color);
    }

    private void bindBookmarkState(PostViewHolder holder, CommunityPost post) {
        holder.btnBookmark.setTag(post.isSaved());
        holder.btnBookmark.setImageResource(post.isSaved() ? R.drawable.ic_save : R.drawable.ic_bookmark);
        holder.btnBookmark.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(),
                post.isSaved() ? R.color.brand_pink : R.color.text_secondary));
    }

    private void setupSocialActions(PostViewHolder holder, CommunityPost post) {
        // Comment Button Click
        holder.layoutComment.setOnClickListener(v -> openPostDetail(v, post.getPostId()));

        // Share Button Click
        holder.layoutShare.setOnClickListener(v -> {
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "Xem bài viết hay này trên PomPom: " + post.getContent());
            v.getContext().startActivity(Intent.createChooser(shareIntent, "Chia sẻ bài viết qua"));
        });

        // Bookmark Button Click — lưu/bỏ lưu thật trên server (cho tab "Đã lưu")
        holder.btnBookmark.setOnClickListener(v -> {
            String userOid = com.pompom.group6.network.Session.getUserOid(v.getContext());
            if (userOid == null) {
                Toast.makeText(v.getContext(), "Vui lòng đăng nhập để lưu bài viết", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean wasSaved = post.isSaved();
            post.setSaved(!wasSaved);
            bindBookmarkState(holder, post);
            Toast.makeText(v.getContext(),
                    wasSaved ? "Đã bỏ lưu bài viết" : "Đã lưu bài viết vào mục Đã lưu", Toast.LENGTH_SHORT).show();

            java.util.Map<String, String> body = new java.util.HashMap<>();
            body.put("user_id", userOid);
            com.pompom.group6.network.ApiClient.get().toggleSavePost(post.getPostId(), body)
                    .enqueue(new retrofit2.Callback<java.util.Map<String, Boolean>>() {
                        @Override
                        public void onResponse(retrofit2.Call<java.util.Map<String, Boolean>> call,
                                               retrofit2.Response<java.util.Map<String, Boolean>> resp) {
                            if (!resp.isSuccessful()) {
                                post.setSaved(wasSaved);
                                bindBookmarkState(holder, post);
                            }
                        }
                        @Override public void onFailure(retrofit2.Call<java.util.Map<String, Boolean>> call, Throwable t) {
                            post.setSaved(wasSaved);
                            bindBookmarkState(holder, post);
                        }
                    });
        });
    }

    private void setupLikeLogic(PostViewHolder holder, CommunityPost post, boolean originalLiked) {
        GestureDetector gestureDetector = new GestureDetector(holder.itemView.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(@NonNull MotionEvent e) {
                toggleLike(holder, post, originalLiked);
                return true;
            }
            @Override
            public boolean onSingleTapConfirmed(@NonNull MotionEvent e) {
                openPostDetail(holder.itemView, post.getPostId());
                return true;
            }
        });

        // Enable touch detection on images and entire card
        holder.vpPostImages.getChildAt(0).setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));
        holder.itemView.setOnTouchListener((v, event) -> gestureDetector.onTouchEvent(event));

        holder.layoutLike.setOnClickListener(v -> toggleLike(holder, post, originalLiked));
    }

    private void openPostDetail(View v, String postId) {
        Intent intent = new Intent(v.getContext(), PostDetailActivity.class);
        intent.putExtra("post_id", postId);
        v.getContext().startActivity(intent);
    }

    /** originalLiked = trạng thái is_liked lúc bind (cố định) — mốc để like_count luôn tính đúng dù bấm nhiều lần. */
    private void toggleLike(PostViewHolder holder, CommunityPost post, boolean originalLiked) {
        String userOid = com.pompom.group6.network.Session.getUserOid(holder.itemView.getContext());
        if (userOid == null) {
            Toast.makeText(holder.itemView.getContext(), "Vui lòng đăng nhập để thích bài viết", Toast.LENGTH_SHORT).show();
            return;
        }
        post.setLiked(!post.isLiked());
        renderLikeState(holder, post, originalLiked);
        if (post.isLiked()) showBigHeartAnimation(holder.ivBigHeart);

        java.util.Map<String, String> body = new java.util.HashMap<>();
        body.put("user_id", userOid);
        com.pompom.group6.network.ApiClient.get().toggleLike(post.getPostId(), body)
                .enqueue(new retrofit2.Callback<java.util.Map<String, Boolean>>() {
                    @Override
                    public void onResponse(retrofit2.Call<java.util.Map<String, Boolean>> call,
                                           retrofit2.Response<java.util.Map<String, Boolean>> resp) {
                        if (!resp.isSuccessful()) {
                            post.setLiked(!post.isLiked());
                            renderLikeState(holder, post, originalLiked);
                        }
                    }
                    @Override public void onFailure(retrofit2.Call<java.util.Map<String, Boolean>> call, Throwable t) {
                        post.setLiked(!post.isLiked());
                        renderLikeState(holder, post, originalLiked);
                    }
                });
    }

    private void renderLikeState(PostViewHolder holder, CommunityPost post, boolean originalLiked) {
        ImageView ivHeart = (ImageView) ((ViewGroup) holder.layoutLike).getChildAt(0);
        ivHeart.setColorFilter(ContextCompat.getColor(holder.itemView.getContext(),
                post.isLiked() ? R.color.brand_pink : R.color.text_secondary));
        int displayCount = post.getLikeCount() + (post.isLiked() ? 1 : 0) - (originalLiked ? 1 : 0);
        holder.tvLikeCount.setText(formatCount(displayCount));
    }

    private void showBigHeartAnimation(ImageView ivBigHeart) {
        ivBigHeart.setAlpha(0.8f);
        ivBigHeart.setScaleX(0f);
        ivBigHeart.setScaleY(0f);
        ivBigHeart.animate().scaleX(1.2f).scaleY(1.2f).alpha(1f).setDuration(300).withEndAction(() -> 
                ivBigHeart.animate().scaleX(1f).scaleY(1f).alpha(0f).setDuration(300).start()).start();
    }

    private void setupFollowLogic(PostViewHolder holder) {
        holder.btnFollow.setOnClickListener(v -> {
            boolean isFollowed = v.getTag() != null && (boolean) v.getTag();
            MaterialButton btn = (MaterialButton) v;
            
            if (isFollowed) {
                v.setTag(false);
                btn.setText("Theo dõi");
                btn.setBackgroundTintList(ContextCompat.getColorStateList(v.getContext(), R.color.brand_pink_light));
                btn.setTextColor(ContextCompat.getColor(v.getContext(), R.color.brand_pink));
                Toast.makeText(v.getContext(), "Đã hủy theo dõi", Toast.LENGTH_SHORT).show();
            } else {
                v.setTag(true);
                btn.setText("Đang theo dõi");
                btn.setBackgroundTintList(ContextCompat.getColorStateList(v.getContext(), R.color.text_secondary));
                btn.setTextColor(Color.WHITE);
                Toast.makeText(v.getContext(), "Đã theo dõi người dùng này", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showPostMenu(View view, int position) {
        PopupMenu popup = new PopupMenu(view.getContext(), view);
        popup.getMenu().add("Ẩn bài viết");
        popup.getMenu().add("Báo cáo vi phạm");
        popup.getMenu().add("Sao chép liên kết");
        
        popup.setOnMenuItemClickListener(item -> {
            String title = item.getTitle().toString();
            if (title.equals("Ẩn bài viết") && position != RecyclerView.NO_POSITION) {
                posts.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, posts.size());
                Toast.makeText(view.getContext(), "Đã ẩn bài viết", Toast.LENGTH_SHORT).show();
            }
            return true;
        });
        popup.show();
    }

    private void showImagePreview(android.content.Context context, String imageUrl) {
        Dialog dialog = new Dialog(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_image_preview);
        
        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        ImageView ivFullScreen = dialog.findViewById(R.id.ivFullScreenImage);
        View btnSave = dialog.findViewById(R.id.btnSaveImage);
        View rootLayout = dialog.findViewById(R.id.rootLayout);

        Glide.with(context).load(imageUrl).into(ivFullScreen);

        btnSave.setOnClickListener(v -> {
            ImageUtils.saveImageToGallery(context, imageUrl);
            dialog.dismiss();
        });

        rootLayout.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private String formatCount(int count) {
        if (count >= 1000) {
            return String.format(Locale.getDefault(), "%.1fK", count / 1000.0);
        }
        return String.valueOf(count);
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    static class PostViewHolder extends RecyclerView.ViewHolder {
        ImageView ivUserAvatar, btnMore, ivBigHeart, btnBookmark;
        TextView tvUserName, tvPostTime, tvPostTitle, tvLikeCount, tvCommentCount, tvShareCount;
        ViewPager2 vpPostImages;
        TabLayout tabIndicator;
        View layoutPostImages, layoutLike, layoutComment, layoutShare;
        MaterialButton btnFollow;

        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            ivUserAvatar = itemView.findViewById(R.id.ivUserAvatar);
            btnMore = itemView.findViewById(R.id.btnMore);
            ivBigHeart = itemView.findViewById(R.id.ivBigHeart);
            btnFollow = itemView.findViewById(R.id.btnFollow);
            layoutLike = itemView.findViewById(R.id.layoutLike);
            layoutComment = itemView.findViewById(R.id.layoutComment);
            layoutShare = itemView.findViewById(R.id.layoutShare);
            btnBookmark = itemView.findViewById(R.id.btnBookmark);
            vpPostImages = itemView.findViewById(R.id.vpPostImages);
            tabIndicator = itemView.findViewById(R.id.tabIndicator);
            layoutPostImages = itemView.findViewById(R.id.layoutPostImages);
            
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvPostTime = itemView.findViewById(R.id.tvPostTime);
            tvPostTitle = itemView.findViewById(R.id.tvPostTitle);
            tvLikeCount = itemView.findViewById(R.id.tvLikeCount);
            tvCommentCount = itemView.findViewById(R.id.tvCommentCount);
            tvShareCount = itemView.findViewById(R.id.tvShareCount);
        }
    }
}
