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
        pestList.add(new Pest("Rầy nâu", "Brown-planthopper", android.R.drawable.ic_menu_gallery));
        pestList.add(new Pest("Rầy xanh đuôi đen", "Green-leafhopper", android.R.drawable.ic_menu_gallery));
        pestList.add(new Pest("Sâu cuốn lá nhỏ", "Leaf-folder", android.R.drawable.ic_menu_gallery));
        pestList.add(new Pest("Bọ xít dài", "Rice-bug", android.R.drawable.ic_menu_gallery));
        pestList.add(new Pest("Sâu đục thân", "Stem-borer", android.R.drawable.ic_menu_gallery));
        pestList.add(new Pest("Ruồi đục lá", "Whorl-maggot", android.R.drawable.ic_menu_gallery));

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