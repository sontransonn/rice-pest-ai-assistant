package com.example.frontend;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.adapter.PestAdapter;

import java.util.ArrayList;
import java.util.List;

public class LibraryFragment extends Fragment {

    private RecyclerView rvLibrary;
    private PestAdapter adapter;
    private List<Pest> pestList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_library, container, false);

        rvLibrary = view.findViewById(R.id.rvLibrary);
        rvLibrary.setLayoutManager(new LinearLayoutManager(getContext()));

        // 1. Tạo dữ liệu 6 loài sâu
        pestList = new ArrayList<>();
        pestList.add(new Pest("Rầy nâu", "Brown-planthopper", R.drawable.ray_nau));
        pestList.add(new Pest("Rầy xanh đuôi đen", "Green-leafhopper", R.drawable.ray_xanh_duoi_den));
        pestList.add(new Pest("Sâu cuốn lá nhỏ", "Leaf-folder", R.drawable.sau_cuon_la_nho));
        pestList.add(new Pest("Bọ xít dài", "Rice-bug", R.drawable.bo_xit_dai));
        pestList.add(new Pest("Sâu đục thân", "Stem-borer", R.drawable.sau_duc_than));
        pestList.add(new Pest("Sâu đục nõn", "Whorl-maggot", R.drawable.ruoi_duc_la));

        adapter = new PestAdapter(pestList, pest -> {
            // Xử lý khi ấn vào item
            Intent intent = new Intent(getActivity(), PestDetailActivity.class);
            intent.putExtra("pest_name", pest.getNameVN());
            startActivity(intent);
        });

        rvLibrary.setAdapter(adapter);

        return view;
    }
}