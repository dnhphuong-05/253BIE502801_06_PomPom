package com.pompom.group6.activities;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.media3.common.MediaItem;
import androidx.media3.common.Player;
import androidx.media3.common.util.UnstableApi;
import androidx.media3.exoplayer.ExoPlayer;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.bumptech.glide.Glide;
import com.pompom.group6.R;
import com.pompom.group6.adapters.ReelAdapter;
import com.pompom.group6.databinding.ActivityReelPlayerBinding;
import com.pompom.group6.network.dto.ApiReel;

/** Phát reel toàn màn hình, lặp lại liên tục — giống trải nghiệm Reels của Instagram/TikTok. */
public class ReelPlayerActivity extends AppCompatActivity {

    public static final String EXTRA_REEL = "extra_reel";

    private ActivityReelPlayerBinding binding;
    private ExoPlayer player;

    @OptIn(markerClass = UnstableApi.class)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReelPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ApiReel reel = (ApiReel) getIntent().getSerializableExtra(EXTRA_REEL);
        if (reel == null) {
            finish();
            return;
        }

        binding.btnBack.setOnClickListener(v -> finish());

        binding.tvPlayerCaption.setText(reel.caption);
        if (reel.author != null) {
            binding.tvPlayerAuthorName.setText(reel.author.name);
            binding.ivPlayerVerified.setVisibility(reel.author.verified ? View.VISIBLE : View.GONE);
            Glide.with(this)
                    .load(reel.author.avatarUrl)
                    .placeholder(R.drawable.ic_avatar)
                    .into(binding.ivPlayerAuthorAvatar);
        } else {
            binding.ivPlayerVerified.setVisibility(View.GONE);
        }

        if (reel.productTags != null && !reel.productTags.isEmpty()) {
            binding.rvPlayerProductTags.setVisibility(View.VISIBLE);
            binding.rvPlayerProductTags.setLayoutManager(
                    new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
            binding.rvPlayerProductTags.setAdapter(new ReelAdapter.TaggedProductAdapter(reel.productTags));
        } else {
            binding.rvPlayerProductTags.setVisibility(View.GONE);
        }

        player = new ExoPlayer.Builder(this).build();
        binding.playerView.setPlayer(player);
        player.setMediaItem(MediaItem.fromUri(reel.videoUrl));
        player.setRepeatMode(Player.REPEAT_MODE_ONE);
        player.prepare();
        player.setPlayWhenReady(true);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) player.setPlayWhenReady(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}
