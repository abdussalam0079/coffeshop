package com.example.coffeshop2.Activity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.coffeshop2.Domain.PopularModel;
import com.example.coffeshop2.Domain.ItemModel;
import com.example.coffeshop2.Utils.WishlistManager;
import com.example.coffeshop2.R;

import java.util.List;

public class PopularAdapter extends RecyclerView.Adapter<PopularAdapter.PopularViewHolder> {

    private final Context context;
    private final List<PopularModel> list;

    public PopularAdapter(Context context, List<PopularModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public PopularViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_popular, parent, false);
        return new PopularViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PopularViewHolder holder, int position) {
        PopularModel model = list.get(position);
        
        if (model == null) {
            return;
        }

        // Set title with null check
        holder.title.setText(model.getTitle() != null ? model.getTitle() : "");
        
        // Set extra with null check
        if (model.getExtra() != null && !model.getExtra().isEmpty()) {
            holder.extra.setText(model.getExtra());
            holder.extra.setVisibility(View.VISIBLE);
        } else {
            holder.extra.setVisibility(View.GONE);
        }
        
        // Set price with proper formatting
        holder.price.setText(String.format("$%.2f", model.getPrice()));
        
        // Set rating
        holder.ratingBar.setRating((float) model.getRating());

        String imageUrl = (model.getPicUrl() != null && !model.getPicUrl().isEmpty())
                ? model.getPicUrl().get(0)
                : null;

        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(context)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .centerCrop()
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.ic_launcher_foreground);
        }

        // Update wishlist button state
        WishlistManager wishlistManager = WishlistManager.getInstance();
        boolean isInWishlist = wishlistManager.isInWishlist(model.getTitle());
        updateWishlistButton(holder.wishlistButton, isInWishlist);

        // Wishlist button click
        holder.wishlistButton.setOnClickListener(v -> {
            ItemModel itemModel = new ItemModel();
            itemModel.setTitle(model.getTitle());
            itemModel.setDescription(model.getDescription());
            itemModel.setExtra(model.getExtra());
            itemModel.setPrice(model.getPrice());
            itemModel.setRating(model.getRating());
            itemModel.setPicUrl(model.getPicUrl());

            if (wishlistManager.isInWishlist(model.getTitle())) {
                wishlistManager.removeFromWishlist(model.getTitle());
                updateWishlistButton(holder.wishlistButton, false);
                Toast.makeText(context, "Removed from wishlist", Toast.LENGTH_SHORT).show();
            } else {
                wishlistManager.addToWishlist(itemModel);
                updateWishlistButton(holder.wishlistButton, true);
                Toast.makeText(context, "Added to wishlist", Toast.LENGTH_SHORT).show();
            }
        });

        // Navigate to detail screen on item click
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ItemDetailActivity.class);
            intent.putExtra("title", model.getTitle());
            intent.putExtra("description", model.getDescription());
            intent.putExtra("extra", model.getExtra());
            intent.putExtra("price", model.getPrice());
            intent.putExtra("rating", model.getRating());
            if (model.getPicUrl() != null && !model.getPicUrl().isEmpty()) {
                intent.putExtra("imageUrl", model.getPicUrl().get(0));
            }
            context.startActivity(intent);
        });
    }

    private void updateWishlistButton(ImageView button, boolean isInWishlist) {
        if (isInWishlist) {
            button.setColorFilter(context.getResources().getColor(R.color.orange));
            button.setAlpha(1.0f);
        } else {
            button.setColorFilter(context.getResources().getColor(R.color.grey));
            button.setAlpha(0.6f);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class PopularViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView extra;
        TextView price;
        RatingBar ratingBar;
        ImageView wishlistButton;

        public PopularViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.popularImage);
            title = itemView.findViewById(R.id.popularTitle);
            extra = itemView.findViewById(R.id.popularExtra);
            price = itemView.findViewById(R.id.popularPrice);
            ratingBar = itemView.findViewById(R.id.popularRating);
            wishlistButton = itemView.findViewById(R.id.popularWishlistButton);
        }
    }
}

