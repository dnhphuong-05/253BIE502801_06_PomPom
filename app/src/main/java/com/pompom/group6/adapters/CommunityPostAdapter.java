package com.pompom.group6.adapters;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
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
        holder.tvShareCount.setText(formatCount(post.getShareCount()));

        holder.tvPostTime.setText(com.pompom.group6.utils.TimeUtils.relativeTime(post.getCreatedAt()));

        Glide.with(holder.itemView.getContext())
                .load(post.getUserAvatar())
                .circleCrop()
                .placeholder(R.drawable.ic_avatar)
                .into(holder.ivUserAvatar);

        // like_count từ server đã tính theo đúng trạng thái is_liked lúc tải — cố định làm mốc
        // để tính lại số hiển thị khi người dùng bấm thích/bỏ thích nhiều lần trong cùng 1 lần bind.
        boolean originalLiked = post.isLiked();

        // SLIDER LOGIC — chạm 1 lần trên ảnh mở chi tiết bài viết, chạm đúp để thích nhanh.
        List<String> imageList = post.getImages();
        if (imageList != null && !imageList.isEmpty()) {
            holder.layoutPostImages.setVisibility(View.VISIBLE);
            PostImageSliderAdapter sliderAdapter = new PostImageSliderAdapter(imageList);
            sliderAdapter.setOnImageLongClickListener(imageUrl -> showImagePreview(holder.itemView.getContext(), imageUrl));
            sliderAdapter.setOnImageTapListener(new PostImageSliderAdapter.OnImageTapListener() {
                @Override public void onSingleTap() { openPostDetail(holder.itemView, post.getPostId()); }
                @Override public void onDoubleTap() { toggleLike(holder, post, originalLiked); }
            });
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
        bindCommentPreview(holder, post);
        setupLikeLogic(holder, post, originalLiked);
        setupFollowLogic(holder, post);
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

    /** Hiển thị tối đa 2 bình luận mới nhất (server trả sẵn trong feed) + link "Xem tất cả N". */
    private void bindCommentPreview(PostViewHolder holder, CommunityPost post) {
        List<CommunityPost.PreviewComment> preview = post.getPreviewComments();
        if (preview == null || preview.isEmpty()) {
            holder.layoutCommentPreview.setVisibility(View.GONE);
            return;
        }
        holder.layoutCommentPreview.setVisibility(View.VISIBLE);
        bindPreviewLine(holder.tvPreviewComment1, preview.size() > 0 ? preview.get(0) : null, post);
        bindPreviewLine(holder.tvPreviewComment2, preview.size() > 1 ? preview.get(1) : null, post);

        int count = post.getCommentCount();
        if (count > preview.size()) {
            holder.tvViewAllComments.setVisibility(View.VISIBLE);
            holder.tvViewAllComments.setText("Xem tất cả " + count + " bình luận");
            holder.tvViewAllComments.setOnClickListener(v -> openPostDetail(v, post.getPostId()));
        } else {
            holder.tvViewAllComments.setVisibility(View.GONE);
        }
    }

    /** Một dòng preview: tên tác giả in đậm + nội dung; chạm mở chi tiết bài viết. */
    private void bindPreviewLine(TextView tv, CommunityPost.PreviewComment c, CommunityPost post) {
        if (c == null) {
            tv.setVisibility(View.GONE);
            return;
        }
        tv.setVisibility(View.VISIBLE);
        String name = c.authorName != null ? c.authorName : "Người dùng";
        String content = c.content != null ? c.content : "";
        android.text.SpannableString s = new android.text.SpannableString(name + "  " + content);
        s.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD),
                0, name.length(), android.text.Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv.setText(s);
        tv.setOnClickListener(v -> openPostDetail(v, post.getPostId()));
    }

    private void setupSocialActions(PostViewHolder holder, CommunityPost post) {
        // Comment Button Click
        holder.layoutComment.setOnClickListener(v -> openPostDetail(v, post.getPostId()));

        // Share Button Click — chỉ tăng lượt chia sẻ khi user THẬT SỰ chọn một nơi để chia sẻ
        // (bắt qua IntentSender callback của chooser), tránh thổi phồng share_count khi user chỉ
        // bấm rồi huỷ hộp thoại. Trước đây tăng ngay lúc bấm nên số bị ảo.
        holder.layoutShare.setOnClickListener(v -> shareWithResultCallback(holder, post));

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

    /**
     * Nhấn vào BẤT KỲ đâu trên card (kể cả vùng ảnh) đều mở bài viết chi tiết — dùng click
     * listener bình thường thay vì gesture detector trên toàn card (trước đây hay bị chặn bởi
     * ViewPager2 của ảnh khiến chỉ icon bình luận là mở được chi tiết đáng tin cậy).
     * Double-tap trên ảnh vẫn giữ để thích nhanh kiểu Instagram, tách riêng khỏi vùng còn lại.
     */
    private void setupLikeLogic(PostViewHolder holder, CommunityPost post, boolean originalLiked) {
        holder.itemView.setOnClickListener(v -> openPostDetail(v, post.getPostId()));
        holder.layoutLike.setOnClickListener(v -> toggleLike(holder, post, originalLiked));
    }

    private void openPostDetail(View v, String postId) {
        Intent intent = new Intent(v.getContext(), PostDetailActivity.class);
        intent.putExtra("post_id", postId);
        v.getContext().startActivity(intent);
    }

    private static long shareRequestSeq = 0;

    /** Mở chooser hệ thống; chỉ khi user CHỌN một app để chia sẻ (chooser bắn EXTRA_CHOSEN_COMPONENT
     * qua PendingIntent) mới gọi API tăng share_count. Nếu user huỷ chooser thì không tính. */
    private void shareWithResultCallback(PostViewHolder holder, CommunityPost post) {
        android.content.Context ctx = holder.itemView.getContext().getApplicationContext();
        String action = ctx.getPackageName() + ".COMMUNITY_SHARE_RESULT." + (shareRequestSeq++);

        android.content.BroadcastReceiver receiver = new android.content.BroadcastReceiver() {
            @Override public void onReceive(android.content.Context c, Intent intent) {
                c.unregisterReceiver(this);
                com.pompom.group6.network.ApiClient.get().sharePost(post.getPostId())
                        .enqueue(new retrofit2.Callback<java.util.Map<String, Integer>>() {
                            @Override
                            public void onResponse(retrofit2.Call<java.util.Map<String, Integer>> call,
                                                   retrofit2.Response<java.util.Map<String, Integer>> resp) {
                                if (resp.isSuccessful() && resp.body() != null && resp.body().get("share_count") != null) {
                                    post.setShareCount(resp.body().get("share_count"));
                                    holder.tvShareCount.setText(formatCount(post.getShareCount()));
                                }
                            }
                            @Override public void onFailure(retrofit2.Call<java.util.Map<String, Integer>> call, Throwable t) {}
                        });
            }
        };

        android.content.IntentFilter filter = new android.content.IntentFilter(action);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            ctx.registerReceiver(receiver, filter, android.content.Context.RECEIVER_NOT_EXPORTED);
        } else {
            ctx.registerReceiver(receiver, filter);
        }

        int piFlags = android.app.PendingIntent.FLAG_UPDATE_CURRENT | android.app.PendingIntent.FLAG_ONE_SHOT;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            piFlags |= android.app.PendingIntent.FLAG_MUTABLE;
        }
        android.app.PendingIntent pi = android.app.PendingIntent.getBroadcast(
                ctx, 0, new Intent(action).setPackage(ctx.getPackageName()), piFlags);

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Xem bài viết hay này trên PomPom: " + post.getContent());

        Intent chooser = Intent.createChooser(shareIntent, "Chia sẻ bài viết qua", pi.getIntentSender());
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ctx.startActivity(chooser);
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

    /** Theo dõi/bỏ theo dõi tác giả bài viết — lưu thật qua API, tạo thông báo cho người được theo dõi. */
    private void setupFollowLogic(PostViewHolder holder, CommunityPost post) {
        String authorId = post.getAuthorId();
        String viewerId = com.pompom.group6.network.Session.getUserOid(holder.itemView.getContext());

        // Ẩn nút theo dõi khi xem bài của chính mình hoặc chưa xác định được tác giả.
        boolean canFollow = authorId != null && viewerId != null && !authorId.equals(viewerId);
        holder.btnFollow.setVisibility(canFollow ? View.VISIBLE : View.GONE);
        if (!canFollow) return;

        // Trạng thái theo dõi do server trả sẵn trong feed (is_following) — không còn gọi
        // getFollowStatus riêng cho từng card (trước đây mỗi bài = 1 request, nút bị nhấp nháy).
        Boolean following = post.getFollowing();
        renderFollowButton(holder.btnFollow, following != null && following);

        holder.btnFollow.setOnClickListener(v -> {
            boolean isFollowed = v.getTag() != null && (boolean) v.getTag();
            if (isFollowed) {
                com.pompom.group6.network.ApiClient.get().unfollowUser(authorId, viewerId)
                        .enqueue(new retrofit2.Callback<java.util.Map<String, Boolean>>() {
                            @Override
                            public void onResponse(retrofit2.Call<java.util.Map<String, Boolean>> call,
                                                   retrofit2.Response<java.util.Map<String, Boolean>> resp) {
                                if (resp.isSuccessful()) {
                                    renderFollowButton(holder.btnFollow, false);
                                    Toast.makeText(v.getContext(), "Đã hủy theo dõi", Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override public void onFailure(retrofit2.Call<java.util.Map<String, Boolean>> call, Throwable t) {}
                        });
            } else {
                java.util.Map<String, String> body = new java.util.HashMap<>();
                body.put("follower_id", viewerId);
                com.pompom.group6.network.ApiClient.get().followUser(authorId, body)
                        .enqueue(new retrofit2.Callback<java.util.Map<String, Boolean>>() {
                            @Override
                            public void onResponse(retrofit2.Call<java.util.Map<String, Boolean>> call,
                                                   retrofit2.Response<java.util.Map<String, Boolean>> resp) {
                                if (resp.isSuccessful()) {
                                    renderFollowButton(holder.btnFollow, true);
                                    Toast.makeText(v.getContext(), "Đã theo dõi người dùng này", Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override public void onFailure(retrofit2.Call<java.util.Map<String, Boolean>> call, Throwable t) {}
                        });
            }
        });
    }

    private void renderFollowButton(MaterialButton btn, boolean following) {
        btn.setTag(following);
        if (following) {
            btn.setText("Đang theo dõi");
            btn.setBackgroundTintList(ContextCompat.getColorStateList(btn.getContext(), R.color.text_secondary));
            btn.setTextColor(Color.WHITE);
        } else {
            btn.setText("Theo dõi");
            btn.setBackgroundTintList(ContextCompat.getColorStateList(btn.getContext(), R.color.brand_pink_light));
            btn.setTextColor(ContextCompat.getColor(btn.getContext(), R.color.brand_pink));
        }
    }

    /** Menu "..." dạng sheet PomPom (thay cho PopupMenu mặc định của Android để đồng nhất concept). */
    private void showPostMenu(View view, int position) {
        if (position == RecyclerView.NO_POSITION) return;
        android.content.Context ctx = view.getContext();
        String[] options = {"Sao chép nội dung", "Ẩn bài viết", "Báo cáo vi phạm"};
        com.pompom.group6.utils.PomPomDialog.pickList(ctx, "Tuỳ chọn bài viết", options, -1, index -> {
            if (position == RecyclerView.NO_POSITION || position >= posts.size()) return;
            switch (index) {
                case 0: copyPostContent(ctx, position); break;
                case 1: hidePost(ctx, position, "Đã ẩn bài viết"); break;
                case 2: reportPost(ctx, position); break;
            }
        });
    }

    /** Sao chép nội dung bài viết vào clipboard (trước đây menu này không làm gì). */
    private void copyPostContent(android.content.Context context, int position) {
        CommunityPost post = posts.get(position);
        android.content.ClipboardManager clipboard =
                (android.content.ClipboardManager) context.getSystemService(android.content.Context.CLIPBOARD_SERVICE);
        if (clipboard == null) return;
        clipboard.setPrimaryClip(android.content.ClipData.newPlainText("PomPom", post.getContent()));
        Toast.makeText(context, "Đã sao chép nội dung bài viết", Toast.LENGTH_SHORT).show();
    }

    /** Báo cáo vi phạm: xác nhận rồi ẩn bài khỏi bảng tin của người báo cáo (dùng endpoint hide
     * sẵn có) — trước đây menu này không làm gì. Kiểm duyệt thật ở phía admin sẽ bổ sung sau. */
    private void reportPost(android.content.Context context, int position) {
        com.pompom.group6.utils.PomPomDialog.confirm(context, "🚩", "Báo cáo bài viết?",
                "Bài viết sẽ được ẩn khỏi bảng tin của bạn và gửi tới đội ngũ kiểm duyệt xem xét.",
                "Báo cáo", "Huỷ",
                () -> {
                    CommunityPost post = posts.get(position);
                    String viewerId = com.pompom.group6.network.Session.getUserOid(context);
                    // Lưu báo cáo vào hàng đợi kiểm duyệt (best-effort); dù mạng lỗi vẫn ẩn khỏi feed.
                    if (viewerId != null) {
                        java.util.Map<String, String> body = new java.util.HashMap<>();
                        body.put("user_id", viewerId);
                        com.pompom.group6.network.ApiClient.get().reportPost(post.getPostId(), body)
                                .enqueue(new retrofit2.Callback<java.util.Map<String, Boolean>>() {
                                    @Override public void onResponse(retrofit2.Call<java.util.Map<String, Boolean>> call,
                                                                     retrofit2.Response<java.util.Map<String, Boolean>> resp) {}
                                    @Override public void onFailure(retrofit2.Call<java.util.Map<String, Boolean>> call, Throwable t) {}
                                });
                    }
                    int idx = posts.indexOf(post);
                    if (idx != -1) hidePost(context, idx, "Đã tiếp nhận báo cáo, cảm ơn bạn");
                });
    }

    /** Ẩn bài viết khỏi feed của riêng người xem — lưu thật qua API để không hiện lại
     * khi tải lại feed (trước đây chỉ xoá tạm khỏi danh sách trong bộ nhớ nên mở lại là mất tác dụng). */
    private void hidePost(android.content.Context context, int position, String successMessage) {
        String viewerId = com.pompom.group6.network.Session.getUserOid(context);
        if (viewerId == null) {
            Toast.makeText(context, "Bạn cần đăng nhập để ẩn bài viết", Toast.LENGTH_SHORT).show();
            return;
        }
        CommunityPost post = posts.get(position);
        java.util.Map<String, String> body = new java.util.HashMap<>();
        body.put("user_id", viewerId);
        com.pompom.group6.network.ApiClient.get().hidePost(post.getPostId(), body)
                .enqueue(new retrofit2.Callback<java.util.Map<String, Boolean>>() {
                    @Override
                    public void onResponse(retrofit2.Call<java.util.Map<String, Boolean>> call,
                                           retrofit2.Response<java.util.Map<String, Boolean>> resp) {
                        if (!resp.isSuccessful()) {
                            Toast.makeText(context, "Ẩn bài viết thất bại, vui lòng thử lại", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        int idx = posts.indexOf(post);
                        if (idx != -1) {
                            posts.remove(idx);
                            notifyItemRemoved(idx);
                            notifyItemRangeChanged(idx, posts.size());
                        }
                        Toast.makeText(context, successMessage, Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onFailure(retrofit2.Call<java.util.Map<String, Boolean>> call, Throwable t) {
                        Toast.makeText(context, "Không kết nối được máy chủ", Toast.LENGTH_SHORT).show();
                    }
                });
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
        View layoutPostImages, layoutLike, layoutComment, layoutShare, layoutCommentPreview;
        TextView tvPreviewComment1, tvPreviewComment2, tvViewAllComments;
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

            layoutCommentPreview = itemView.findViewById(R.id.layoutCommentPreview);
            tvPreviewComment1 = itemView.findViewById(R.id.tvPreviewComment1);
            tvPreviewComment2 = itemView.findViewById(R.id.tvPreviewComment2);
            tvViewAllComments = itemView.findViewById(R.id.tvViewAllComments);
        }
    }
}
