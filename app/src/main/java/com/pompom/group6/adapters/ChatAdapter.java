package com.pompom.group6.adapters;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pompom.group6.databinding.ItemChatBotBinding;
import com.pompom.group6.databinding.ItemChatUserBinding;
import com.pompom.group6.models.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ChatMessage> messages;

    public ChatAdapter(List<ChatMessage> messages) {
        this.messages = messages;
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == ChatMessage.TYPE_USER) {
            ItemChatUserBinding binding = ItemChatUserBinding.inflate(inflater, parent, false);
            return new UserViewHolder(binding);
        } else {
            ItemChatBotBinding binding = ItemChatBotBinding.inflate(inflater, parent, false);
            return new BotViewHolder(binding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage msg = messages.get(position);
        if (holder instanceof UserViewHolder) {
            ((UserViewHolder) holder).binding.tvChatUserMessage.setText(msg.getText());
        } else {
            ((BotViewHolder) holder).binding.tvChatBotMessage.setText(msg.getText());
        }
    }

    @Override
    public int getItemCount() {
        return messages != null ? messages.size() : 0;
    }

    // ── ViewHolders ───────────────────────────────────────────────────────────────

    static class UserViewHolder extends RecyclerView.ViewHolder {
        final ItemChatUserBinding binding;
        UserViewHolder(ItemChatUserBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    static class BotViewHolder extends RecyclerView.ViewHolder {
        final ItemChatBotBinding binding;
        BotViewHolder(ItemChatBotBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
