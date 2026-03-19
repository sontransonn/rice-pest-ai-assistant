package com.example.frontend.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.frontend.Pest;
import com.example.frontend.R;

import java.util.List;

public class PestAdapter extends RecyclerView.Adapter<PestAdapter.PestViewHolder> {

    private List<Pest> pestList;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Pest pest);
    }

    public PestAdapter(List<Pest> pestList, OnItemClickListener listener) {
        this.pestList = pestList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PestViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pest, parent, false);
        return new PestViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PestViewHolder holder, int position) {
        Pest pest = pestList.get(position);
        holder.tvNameVN.setText(pest.getNameVN());
        holder.tvNameEN.setText(pest.getNameEN());
        holder.ivThumb.setImageResource(pest.getImageResId());

        holder.itemView.setOnClickListener(v -> listener.onItemClick(pest));
    }

    @Override
    public int getItemCount() {
        return pestList.size();
    }

    static class PestViewHolder extends RecyclerView.ViewHolder {
        TextView tvNameVN, tvNameEN;
        ImageView ivThumb;

        public PestViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNameVN = itemView.findViewById(R.id.tvPestNameVN);
            tvNameEN = itemView.findViewById(R.id.tvPestNameEN);
            ivThumb = itemView.findViewById(R.id.ivPestThumb);
        }
    }
}