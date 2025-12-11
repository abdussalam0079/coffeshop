package com.example.coffeshop2.Activity;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffeshop2.Domain.CategoryModel;
import com.example.coffeshop2.R;

import java.util.List;
public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.ViewHolder> {

    Context context;
    List<CategoryModel> list;
    int selectedPosition = 0;
    OnCategoryClickListener listener;

    public interface OnCategoryClickListener {
        void onCategoryClicked(CategoryModel model);
    }

    public CategoryAdapter(Context ctx, List<CategoryModel> list) {
        this(ctx, list, null);
    }

    public CategoryAdapter(Context ctx, List<CategoryModel> list, OnCategoryClickListener listener) {
        this.context = ctx;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_category, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryAdapter.ViewHolder holder, int position) {
        CategoryModel model = list.get(position);
        holder.categoryTitle.setText(model.getTitle());

        if (selectedPosition == position) {
            holder.categoryTitle.setBackgroundResource(R.drawable.bg_category_selected);
            holder.categoryTitle.setTextColor(context.getResources().getColor(android.R.color.white));
        } else {
            holder.categoryTitle.setBackgroundResource(R.drawable.unselected_pill);
            holder.categoryTitle.setTextColor(context.getResources().getColor(android.R.color.black));
        }
    }

    @Override
    public int getItemCount() { return list.size(); }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView categoryTitle;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTitle = itemView.findViewById(R.id.categoryTitle);
            itemView.setOnClickListener(v -> {
                int pos = getAdapterPosition();
                if (pos == RecyclerView.NO_POSITION) return;
                selectedPosition = pos;
                notifyDataSetChanged();
                if (listener != null) {
                listener.onCategoryClicked(list.get(pos));
                }
            });
        }
    }
}

