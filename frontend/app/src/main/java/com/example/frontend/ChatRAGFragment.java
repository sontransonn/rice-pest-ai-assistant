package com.example.frontend;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.adapter.ChatAdapter;
import com.example.frontend.model.ChatMessage;
import com.example.frontend.model.ChatRequest;
import com.example.frontend.model.ChatResponse;
import com.example.frontend.network.RetrofitClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatRAGFragment extends Fragment {
    private RecyclerView rvChat;
    private EditText etMessage;
    private FloatingActionButton btnSend;
    private ImageButton btnBack, btnGallery, btnCamera;

    private List<ChatMessage> messageList = new ArrayList<>();
    private ChatAdapter chatAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        initViews(view);
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        rvChat = view.findViewById(R.id.rvChat);
        etMessage = view.findViewById(R.id.etMessage);
        btnSend = view.findViewById(R.id.btnSend);
        btnBack = view.findViewById(R.id.btnBack);
        btnGallery = view.findViewById(R.id.btnGalleryChat);
        btnCamera = view.findViewById(R.id.btnCameraChat);

        chatAdapter = new ChatAdapter(messageList);
        rvChat.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChat.setAdapter(chatAdapter);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(v -> {
            if (getActivity() != null) getActivity().onBackPressed();
        });

        btnSend.setOnClickListener(v -> {
            String text = etMessage.getText().toString().trim();
            if (!text.isEmpty()) {
                sendUserMessage(text);
            }
        });

        btnGallery.setOnClickListener(v -> Toast.makeText(getContext(), "Tính năng gửi ảnh tư vấn đang phát triển", Toast.LENGTH_SHORT).show());
        btnCamera.setOnClickListener(v -> Toast.makeText(getContext(), "Tính năng chụp ảnh tư vấn đang phát triển", Toast.LENGTH_SHORT).show());
    }

    private void sendUserMessage(String text) {
        messageList.add(new ChatMessage("user", text));
        updateChatUI();
        etMessage.setText("");

        ChatRequest request = new ChatRequest(text);
        RetrofitClient.getApiService().chatWithAI(request).enqueue(new Callback<ChatResponse>() {
            @Override
            public void onResponse(@NonNull Call<ChatResponse> call, @NonNull Response<ChatResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    addBotMessage(response.body().answer);
                } else {
                    addBotMessage("Server đang bận, bạn thử lại sau nhé!");
                }
            }

            @Override
            public void onFailure(@NonNull Call<ChatResponse> call, @NonNull Throwable t) {
                addBotMessage("Lỗi kết nối: " + t.getMessage());
            }
        });
    }

    private void addBotMessage(String text) {
        messageList.add(new ChatMessage("model", text));
        updateChatUI();
    }

    private void updateChatUI() {
        if (chatAdapter != null) {
            chatAdapter.notifyItemInserted(messageList.size() - 1);
            rvChat.scrollToPosition(messageList.size() - 1);
        }
    }
}