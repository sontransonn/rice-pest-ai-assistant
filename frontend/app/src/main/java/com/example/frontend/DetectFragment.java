package com.example.frontend;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.frontend.model.Prediction;
import com.example.frontend.model.PredictResponse;
import com.example.frontend.network.RetrofitClient;
import com.google.android.material.button.MaterialButton;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DetectFragment extends Fragment {
    private PreviewView viewFinder;
    private ImageView ivGalleryPreview;
    private MaterialButton btnLive, btnGallery;
    private TextView tvResult, tvConfidence;
    private View scanLine;

    private ProcessCameraProvider cameraProvider;
    private boolean isCameraOn = false;

    private static final int REQUEST_CODE_PERMISSIONS = 10;
    private static final String[] REQUIRED_PERMISSIONS = new String[]{Manifest.permission.CAMERA};

    private final ActivityResultLauncher<String> mGetContent = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    processSelectedImage(uri);
                }
            }
    );

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detect, container, false);
        initViews(view);

        btnLive.setOnClickListener(v -> {
            if (allPermissionsGranted()) toggleCamera();
            else requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        });

        btnGallery.setOnClickListener(v -> mGetContent.launch("image/*"));

        return view;
    }

    private void initViews(View view) {
        viewFinder = view.findViewById(R.id.viewFinder);
        ivGalleryPreview = view.findViewById(R.id.ivGalleryPreview);
        btnLive = view.findViewById(R.id.btnLive);
        btnGallery = view.findViewById(R.id.btnGallery);
        tvResult = view.findViewById(R.id.tvResult);
        tvConfidence = view.findViewById(R.id.tvConfidence);
        scanLine = view.findViewById(R.id.scanLine);

        scanLine.setVisibility(View.GONE);
    }

    private void toggleCamera() {
        if (isCameraOn) stopCamera();
        else {
            ivGalleryPreview.setVisibility(View.GONE);
            viewFinder.setVisibility(View.VISIBLE);
            startCamera();
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext());
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(viewFinder.getSurfaceProvider());
                CameraSelector cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle(getViewLifecycleOwner(), cameraSelector, preview);

                isCameraOn = true;
                btnLive.setText("Dừng Quét");
                scanLine.setVisibility(View.VISIBLE);
                tvResult.setText("Đang quét camera...");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, ContextCompat.getMainExecutor(requireContext()));
    }

    private void stopCamera() {
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
            isCameraOn = false;
            btnLive.setText("Live Scan");
            scanLine.setVisibility(View.GONE);
            tvResult.setText("Đã dừng quét");
        }
    }

    private void processSelectedImage(Uri uri) {
        if (isCameraOn){
            stopCamera();
        }

        viewFinder.setVisibility(View.GONE);
        ivGalleryPreview.setVisibility(View.VISIBLE);
        ivGalleryPreview.setImageURI(uri);

        uploadImageToAPI(uri);
    }

    private void uploadImageToAPI(Uri uri) {
        tvResult.setText("Đang phân tích ảnh...");
        tvConfidence.setText("Vui lòng đợi...");

        File file = getFileFromUri(uri);
        if (file == null) return;

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        RetrofitClient.getApiService().uploadImage(body).enqueue(new Callback<PredictResponse>() {
            @Override
            public void onResponse(@NonNull Call<PredictResponse> call, @NonNull Response<PredictResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Prediction> predictions = response.body().data;
                    if (!predictions.isEmpty()) {
                        tvResult.setText("Kết quả: " + predictions.get(0).label);
                        tvConfidence.setText("Độ tin cậy: " + (int)(predictions.get(0).confidence * 100) + "%");

                        drawBboxesOnImage(uri, predictions);
                    } else {
                        tvResult.setText("Không tìm thấy sâu bệnh");
                        tvConfidence.setText("");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<PredictResponse> call, @NonNull Throwable t) {
                tvResult.setText("Lỗi kết nối Server");
            }
        });
    }

    private void drawBboxesOnImage(Uri uri, List<Prediction> predictions) {
        try {
            InputStream is = requireContext().getContentResolver().openInputStream(uri);
            Bitmap sourceBitmap = BitmapFactory.decodeStream(is);
            Bitmap mutableBitmap = sourceBitmap.copy(Bitmap.Config.ARGB_8888, true);
            Canvas canvas = new Canvas(mutableBitmap);

            Paint paintRect = new Paint();
            paintRect.setColor(Color.RED);
            paintRect.setStyle(Paint.Style.STROKE);
            paintRect.setStrokeWidth(8f);

            Paint paintText = new Paint();
            paintText.setColor(Color.WHITE);
            paintText.setTextSize(40f);
            paintText.setTypeface(Typeface.DEFAULT_BOLD);

            Paint paintBg = new Paint();
            paintBg.setColor(Color.RED);
            paintBg.setStyle(Paint.Style.FILL);

            for (Prediction pred : predictions) {
                List<Integer> b = pred.bbox;
                canvas.drawRect(b.get(0), b.get(1), b.get(2), b.get(3), paintRect);
                String labelText = pred.label + " " + (int)(pred.confidence * 100) + "%";
                canvas.drawRect(b.get(0), b.get(1) - 45, b.get(0) + paintText.measureText(labelText) + 10, b.get(1), paintBg);
                canvas.drawText(labelText, b.get(0) + 5, b.get(1) - 10, paintText);
            }

            ivGalleryPreview.setImageBitmap(mutableBitmap);
            if (is != null) is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private File getFileFromUri(Uri uri) {
        try {
            File tempFile = new File(requireContext().getCacheDir(), "temp_image.jpg");
            InputStream inputStream = requireContext().getContentResolver().openInputStream(uri);
            OutputStream outputStream = new FileOutputStream(tempFile);
            byte[] buffer = new byte[4096];
            int read;
            while ((read = inputStream.read(buffer)) != -1) outputStream.write(buffer, 0, read);
            outputStream.flush();
            outputStream.close();
            inputStream.close();
            return tempFile;
        } catch (Exception e) {
            return null;
        }
    }

    private boolean allPermissionsGranted() {
        for (String p : REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(requireContext(), p) != PackageManager.PERMISSION_GRANTED) return false;
        }
        return true;
    }
}