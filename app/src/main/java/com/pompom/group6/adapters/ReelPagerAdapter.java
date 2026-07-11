package com.pompom.group6.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.pompom.group6.R;
import com.pompom.group6.databinding.DialogReelCommentsBinding;
import com.pompom.group6.databinding.ItemReelPageBinding;
import com.pompom.group6.models.Comment;
import com.pompom.group6.network.ApiClient;
import com.pompom.group6.network.Session;
import com.pompom.group6.network.dto.ApiComment;
import com.pompom.group6.network.dto.ApiReel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Mỗi trang ViewPager2 là 1 reel; chỉ dùng CHUNG 1 ExoPlayer, gắn vào đúng trang đang
 * hiển thị — giống cách Reels của Facebook/Instagram phát liên tục khi lướt dọc.
 */
public class ReelPagerAdapter extends RecyclerView.Adapter<ReelPagerAdapter.PageViewHolder> {
    private final List<ApiReel> reels;
    private final ExoPlayer player;
    private int activePosition = -1;
    private int bottomInsetPx = 0;
    private int topInsetPx = 0;
    private RecyclerView recyclerView;

    private final Handler progressHandler = new Handler(Looper.getMainLooper());
    private final Runnable progressTick = this::tickProgress;

    public ReelPagerAdapter(List<ApiReel> reels, ExoPlayer player) {
        this.reels = reels;
        this.player = player;
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView rv) {
        super.onAttachedToRecyclerView(rv);
        this.recyclerView = rv;
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView rv) {
        super.onDetachedFromRecyclerView(rv);
        progressHandler.removeCallbacks(progressTick);
        this.recyclerView = null;
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemReelPageBinding binding = ItemReelPageBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new PageViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        ApiReel reel = reels.get(position);

        if (reel.author != null) {
            holder.binding.tvPlayerAuthorName.setText(reel.author.name);
            holder.binding.ivPlayerVerified.setVisibility(reel.author.verified ? View.VISIBLE : View.GONE);
            Glide.with(holder.itemView.getContext())
                    .load(reel.author.avatarUrl)
                    .placeholder(R.drawable.ic_avatar)
                    .into(holder.binding.ivPlayerAuthorAvatar);
        } else {
            holder.binding.tvPlayerAuthorName.setText("");
            holder.binding.ivPlayerVerified.setVisibility(View.GONE);
        }
        holder.binding.tvPlayerCaption.setText(reel.caption);
        bindActionColumn(holder, reel);

        // Chừa khoảng cho status bar (thanh tiến trình) và thanh điều hướng hệ thống (overlay/cột
        // hành động) — video tràn full-screen phía sau cả hai, nên tự tính padding/margin thủ công.
        // Trên một số máy (vd MIUI dùng nav bar 3 nút) cửa sổ app KHÔNG thực sự vẽ xuyên qua được
        // nav bar dù đã bật edge-to-edge — hệ thống vẫn báo bottomInsetPx lớn dù nav bar đã nằm
        // ngoài cửa sổ, cộng dồn khoảng đệm dư thừa 2 lần. Giới hạn mức bù tối đa (~32dp, đủ cho
        // gesture-nav thật) để tránh khoảng trắng/đen dư ở đáy màn hình.
        float d = holder.itemView.getResources().getDisplayMetrics().density;
        int cappedBottomInset = Math.min(bottomInsetPx, (int) (32 * d));
        View overlay = holder.binding.layoutReelOverlay;
        overlay.setPadding(overlay.getPaddingLeft(), (int) (24 * d),
                overlay.getPaddingRight(), (int) (16 * d) + cappedBottomInset);

        ViewGroup.MarginLayoutParams actionsParams =
                (ViewGroup.MarginLayoutParams) holder.binding.layoutReelActions.getLayoutParams();
        actionsParams.bottomMargin = (int) (16 * d) + cappedBottomInset;
        holder.binding.layoutReelActions.setLayoutParams(actionsParams);

        ViewGroup.MarginLayoutParams progressParams =
                (ViewGroup.MarginLayoutParams) holder.binding.reelProgressBar.getLayoutParams();
        progressParams.topMargin = (int) (6 * d) + topInsetPx;
        holder.binding.reelProgressBar.setLayoutParams(progressParams);

        if (reel.productTags != null && !reel.productTags.isEmpty()) {
            holder.binding.rvPlayerProductTags.setVisibility(View.VISIBLE);
            holder.binding.rvPlayerProductTags.setLayoutManager(
                    new LinearLayoutManager(holder.itemView.getContext(), LinearLayoutManager.HORIZONTAL, false));
            holder.binding.rvPlayerProductTags.setAdapter(new ReelAdapter.TaggedProductAdapter(reel.productTags));
        } else {
            holder.binding.rvPlayerProductTags.setVisibility(View.GONE);
        }

        // Không còn thanh điều khiển mặc định của ExoPlayer (đè lên caption) — chạm vào video để
        // tạm dừng/phát tiếp, giống Reels/TikTok.
        holder.binding.playerView.setOnClickListener(v -> {
            if (player.isPlaying()) player.pause(); else player.play();
        });

        if (position == activePosition) {
            attachPlayer(holder, reel);
        } else {
            holder.binding.playerView.setPlayer(null);
        }
    }

    private void bindActionColumn(PageViewHolder holder, ApiReel reel) {
        holder.binding.tvReelLikeCount.setText(formatCount(reel.likeCount));
        holder.binding.tvReelCommentCount.setText(formatCount(reel.commentCount));
        renderLikeIcon(holder, reel);

        holder.binding.btnReelLike.setOnClickListener(v -> {
            reel.liked = !reel.liked;
            reel.likeCount += reel.liked ? 1 : -1;
            holder.binding.tvReelLikeCount.setText(formatCount(reel.likeCount));
            renderLikeIcon(holder, reel);
        });

        holder.binding.btnReelComment.setOnClickListener(v ->
                openComments(holder.itemView.getContext(), reel, holder.binding.tvReelCommentCount));

        holder.binding.btnReelShare.setOnClickListener(v -> {
            Context ctx = v.getContext();
            String link = reel.sourceUrl != null ? reel.sourceUrl : reel.caption;
            Intent shareIntent = new Intent(Intent.ACTION_SEND);
            shareIntent.setType("text/plain");
            shareIntent.putExtra(Intent.EXTRA_TEXT, link != null ? link : "PomPom Beauty");
            ctx.startActivity(Intent.createChooser(shareIntent, "Chia sẻ thước phim"));
        });
    }

    private void renderLikeIcon(PageViewHolder holder, ApiReel reel) {
        if (reel.liked) {
            holder.binding.ivReelLikeIcon.setImageResource(R.drawable.ic_heart_solid);
            holder.binding.ivReelLikeIcon.setImageTintList(null);
        } else {
            holder.binding.ivReelLikeIcon.setImageResource(R.drawable.ic_heart_outline);
            holder.binding.ivReelLikeIcon.setImageTintList(
                    android.content.res.ColorStateList.valueOf(0xFFFFFFFF));
        }
    }

    /** Bình luận thật, dùng chung endpoint bình luận chung (post_id chỉ là id tham chiếu — xem
     * cách ArticleDetailActivity tái dùng endpoint này cho bài Blog/Tips). */
    private void openComments(Context context, ApiReel reel, android.widget.TextView tvCommentCountLabel) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        DialogReelCommentsBinding db = DialogReelCommentsBinding.inflate(LayoutInflater.from(context));
        dialog.setContentView(db.getRoot());
        db.rvReelComments.setLayoutManager(new LinearLayoutManager(context));

        Runnable[] reload = new Runnable[1];
        reload[0] = () -> ApiClient.get().getPostComments(reel.id).enqueue(new Callback<List<ApiComment>>() {
            @Override
            public void onResponse(Call<List<ApiComment>> call, Response<List<ApiComment>> resp) {
                if (!resp.isSuccessful() || resp.body() == null) return;
                List<Comment> comments = new ArrayList<>();
                for (ApiComment c : resp.body()) {
                    comments.add(new Comment(0, 0, c.authorName, c.authorAvatar, c.content, c.createdAt));
                }
                db.rvReelComments.setAdapter(new CommentAdapter(comments));
                db.tvReelCommentTitle.setText("Bình luận (" + comments.size() + ")");
                db.tvReelCommentEmpty.setVisibility(comments.isEmpty() ? View.VISIBLE : View.GONE);
                reel.commentCount = comments.size();
                tvCommentCountLabel.setText(formatCount(reel.commentCount));
            }
            @Override public void onFailure(Call<List<ApiComment>> call, Throwable t) {}
        });
        reload[0].run();

        View.OnClickListener send = v -> {
            String userOid = Session.getUserOid(context);
            if (userOid == null) {
                Toast.makeText(context, "Vui lòng đăng nhập để bình luận", Toast.LENGTH_SHORT).show();
                return;
            }
            String content = db.etReelComment.getText().toString().trim();
            if (content.isEmpty()) return;
            Map<String, String> body = new HashMap<>();
            body.put("user_id", userOid);
            body.put("content", content);
            ApiClient.get().addComment(reel.id, body).enqueue(new Callback<ApiComment>() {
                @Override
                public void onResponse(Call<ApiComment> call, Response<ApiComment> resp) {
                    if (!resp.isSuccessful()) return;
                    db.etReelComment.setText("");
                    reload[0].run();
                }
                @Override public void onFailure(Call<ApiComment> call, Throwable t) {
                    Toast.makeText(context, "Không gửi được bình luận", Toast.LENGTH_SHORT).show();
                }
            });
        };
        db.btnSendReelComment.setOnClickListener(send);
        db.etReelComment.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEND) {
                send.onClick(v);
                return true;
            }
            return false;
        });

        dialog.show();
    }

    private String formatCount(long count) {
        if (count >= 1_000_000) return String.format(Locale.US, "%.1fM", count / 1_000_000.0);
        if (count >= 1_000) return String.format(Locale.US, "%.1fK", count / 1_000.0);
        return String.valueOf(count);
    }

    @Override
    public void onViewRecycled(@NonNull PageViewHolder holder) {
        super.onViewRecycled(holder);
        holder.binding.playerView.setPlayer(null);
    }

    @Override
    public int getItemCount() {
        return reels != null ? reels.size() : 0;
    }

    /** Gọi khi biết chiều cao status bar hệ thống — áp cho các trang đã bind lại. */
    public void setTopInset(int px) {
        if (topInsetPx == px) return;
        topInsetPx = px;
        notifyDataSetChanged();
    }

    /** Gọi khi biết chiều cao thanh điều hướng hệ thống — áp cho các trang đã bind lại. */
    public void setBottomInset(int px) {
        if (bottomInsetPx == px) return;
        bottomInsetPx = px;
        notifyDataSetChanged();
    }

    /** Gọi khi ViewPager2 đổi trang: phát reel đang hiển thị, gỡ player khỏi trang cũ. */
    public void setActivePosition(int position) {
        if (activePosition == position) return;
        activePosition = position;
        if (recyclerView == null) return;
        for (int i = 0; i < getItemCount(); i++) {
            RecyclerView.ViewHolder vh = recyclerView.findViewHolderForAdapterPosition(i);
            if (vh instanceof PageViewHolder) {
                PageViewHolder page = (PageViewHolder) vh;
                if (i == position) {
                    attachPlayer(page, reels.get(i));
                } else {
                    page.binding.playerView.setPlayer(null);
                }
            }
        }
    }

    private void attachPlayer(PageViewHolder holder, ApiReel reel) {
        holder.binding.playerView.setPlayer(player);
        player.setMediaItem(MediaItem.fromUri(reel.videoUrl));
        player.setRepeatMode(Player.REPEAT_MODE_ONE);
        player.prepare();
        player.setPlayWhenReady(true);
        startProgressUpdates(holder);
    }

    /** Cập nhật thanh tiến trình mảnh trên cùng mỗi 200ms trong lúc reel đang phát — thay cho
     * thanh điều khiển mặc định của ExoPlayer (trước đây đè lên caption/tên tác giả bên dưới). */
    private void startProgressUpdates(PageViewHolder holder) {
        progressHandler.removeCallbacks(progressTick);
        activeHolder = holder;
        progressHandler.post(progressTick);
    }

    private PageViewHolder activeHolder;

    private void tickProgress() {
        if (activeHolder == null || player == null) return;
        long duration = player.getDuration();
        if (duration > 0) {
            int progress = (int) (1000L * player.getCurrentPosition() / duration);
            activeHolder.binding.reelProgressBar.setProgress(Math.min(progress, 1000));
        }
        progressHandler.postDelayed(progressTick, 200);
    }

    static class PageViewHolder extends RecyclerView.ViewHolder {
        final ItemReelPageBinding binding;

        PageViewHolder(ItemReelPageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
