package com.example.frontend;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class PestDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pest_detail);

        // Khai báo các view
        ImageView ivBack = findViewById(R.id.ivBackDetail);
        TextView tvTitle = findViewById(R.id.tvToolbarTitle);
        TextView tvNameVN = findViewById(R.id.tvPestNameVN);

        // 1. Lấy tên loài sâu từ Intent gửi sang
        String pestName = getIntent().getStringExtra("pest_name");

        // 2. Hiển thị lên giao diện
        if (pestName != null) {
            tvNameVN.setText(pestName);
            tvTitle.setText(pestName); // Đổi tiêu đề Toolbar theo tên loài
        }

        // 3. Nút quay lại
        ivBack.setOnClickListener(v -> finish());

        // TODO: Dựa vào pestName, bạn có thể dùng một hàm switch/case
        // để đổ tiếp dữ liệu mô tả và ảnh tương ứng vào các TextView khác.
    }
}