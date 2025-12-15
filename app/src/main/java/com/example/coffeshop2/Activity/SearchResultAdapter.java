package com.example.coffeshop2.Activity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.coffeshop2.Domain.ItemModel;
import com.example.coffeshop2.R;

import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchViewHolder> {

    private final Context context;
    private final List<ItemModel> list;

    public SearchResultAdapter(Context context, List<ItemModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_search_result, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        ItemModel model = list.get(position);
        
        if (model == null) {
            return;
        }

        // Set title
        if (model.getTitle() != null) {
            holder.title.setText(model.getTitle());
        } else {
            holder.title.setText("");
        }

        // Set description/extra
        if (model.getExtra() != null && !model.getExtra().isEmpty()) {
            holder.description.setText(model.getExtra());
            holder.description.setVisibility(View.VISIBLE);
        } else if (model.getDescription() != null && !model.getDescription().isEmpty()) {
            holder.description.setText(model.getDescription());
            holder.description.setVisibility(View.VISIBLE);
        } else {
            holder.description.setVisibility(View.GONE);
        }

        // Set price
        holder.price.setText(String.format("$%.1f", model.getPrice()));

        // Set rating
        holder.ratingBar.setRating((float) model.getRating());

        // Load image
        String imageUrl = (model.getPicUrl() != null && !model.getPicUrl().isEmpty())
                ? model.getPicUrl().get(0)
                : null;

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.ic_launcher_foreground);
        }

        // Navigate to detail screen on item click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ItemDetailActivity.class);
            intent.putExtra("title", model.getTitle() != null ? model.getTitle() : "");
            intent.putExtra("description", model.getDescription() != null ? model.getDescription() : "");
            intent.putExtra("extra", model.getExtra() != null ? model.getExtra() : "");
            intent.putExtra("price", model.getPrice());
            intent.putExtra("rating", model.getRating());
            if (imageUrl != null) {
                intent.putExtra("imageUrl", imageUrl);
            }
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class SearchViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView description;
        TextView price;
        RatingBar ratingBar;

        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.searchImage);
            title = itemView.findViewById(R.id.searchTitle);
            description = itemView.findViewById(R.id.searchDescription);
            price = itemView.findViewById(R.id.searchPrice);
            ratingBar = itemView.findViewById(R.id.searchRating);
        }
    }
}
