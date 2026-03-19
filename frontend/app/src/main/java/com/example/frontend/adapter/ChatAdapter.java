package com.example.frontend.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.R;
import com.example.frontend.model.ChatMessage;

import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<ChatMessage> messageList;

    // Định nghĩa 2 loại View để hiển thị trái/phải
    private static final int TYPE_USER = 1;
    private static final int TYPE_BOT = 2;

    public ChatAdapter(List<ChatMessage> messageList) {
        this.messageList = messageList;
    }

    @Override
    public int getItemViewType(int position) {
        // Nếu role là "user" thì trả về TYPE_USER, ngược lại là TYPE_BOT
        if (messageList.get(position).getRole().equals("user")) {
            return TYPE_USER;
        } else {
            return TYPE_BOT;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_USER) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_user, parent, false);
            return new UserViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_bot, parent, false);
            return new BotViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messageList.get(position);
        if (holder instanceof UserViewHolder) {
            ((UserViewHolder) holder).tvMessage.setText(message.getContent());
        } else {
            ((BotViewHolder) holder).tvMessage.setText(message.getContent());
        }
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // ViewHolder cho tin nhắn của Người dùng
    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessageUser);
        }
    }

    // ViewHolder cho tin nhắn của AI (Bot)
    static class BotViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        BotViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessageBot);
        }
    }
}