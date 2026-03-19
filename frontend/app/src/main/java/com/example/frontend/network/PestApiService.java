package com.example.frontend.network;

import com.example.frontend.model.ChatRequest;
import com.example.frontend.model.ChatResponse;
import com.example.frontend.model.PredictResponse;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface PestApiService {
    @Multipart
    @POST("predict")
    Call<PredictResponse> uploadImage(@Part MultipartBody.Part file);

    @POST("chat")
    Call<ChatResponse> chatWithAI(@Body ChatRequest request);
}