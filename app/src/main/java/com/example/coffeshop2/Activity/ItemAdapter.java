package com.example.coffeshop2.Activity;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.coffeshop2.Domain.ItemModel;
import com.example.coffeshop2.Domain.CartModel;
import com.example.coffeshop2.R;
import com.example.coffeshop2.Utils.CartManager;
import com.example.coffeshop2.Utils.WishlistManager;
import android.widget.Toast;

import java.util.List;

public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ItemViewHolder> {

    private final Context context;
    private final List<ItemModel> list;

    public ItemAdapter(Context context, List<ItemModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        ItemModel model = list.get(position);

        holder.title.setText(model.getTitle());
        holder.extra.setText(model.getExtra());
        holder.price.setText(String.format("$%.1f", model.getPrice()));

        String imageUrl = (model.getPicUrl() != null && !model.getPicUrl().isEmpty())
                ? model.getPicUrl().get(0)
                : null;

        if (imageUrl != null) {
            Glide.with(context)
                    .load(imageUrl)
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
            if (wishlistManager.isInWishlist(model.getTitle())) {
                wishlistManager.removeFromWishlist(model.getTitle());
                updateWishlistButton(holder.wishlistButton, false);
                Toast.makeText(context, "Removed from wishlist", Toast.LENGTH_SHORT).show();
            } else {
                wishlistManager.addToWishlist(model);
                updateWishlistButton(holder.wishlistButton, true);
                Toast.makeText(context, "Added to wishlist", Toast.LENGTH_SHORT).show();
            }
        });

        holder.addButton.setOnClickListener(v -> {
            // Add to cart
            String cartImageUrl = (model.getPicUrl() != null && !model.getPicUrl().isEmpty())
                    ? model.getPicUrl().get(0) : "";
            
            CartModel cartItem = new CartModel(
                    model.getTitle() + "_" + System.currentTimeMillis(), // Unique ID
                    model.getTitle(),
                    model.getExtra(),
                    model.getPrice(),
                    1,
                    cartImageUrl
            );
            
            CartManager.getInstance().addToCart(cartItem);
            Toast.makeText(context, model.getTitle() + " added to cart", Toast.LENGTH_SHORT).show();
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

    @Override
    public int getItemCount() {
        return list.size();
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

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView extra;
        TextView price;
        ImageView addButton;
        ImageView wishlistButton;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.itemImage);
            title = itemView.findViewById(R.id.itemTitle);
            extra = itemView.findViewById(R.id.itemExtra);
            price = itemView.findViewById(R.id.itemPrice);
            addButton = itemView.findViewById(R.id.itemAddButton);
            wishlistButton = itemView.findViewById(R.id.itemWishlistButton);
        }
    }
}

