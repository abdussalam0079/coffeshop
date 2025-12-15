package com.example.coffeshop2.Activity;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.coffeshop2.Domain.CartModel;
import com.example.coffeshop2.R;
import com.example.coffeshop2.Utils.CartManager;

import java.util.List;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartViewHolder> {

    private final Context context;
    private List<CartModel> list;
    private OnCartUpdateListener listener;

    public interface OnCartUpdateListener {
        void onCartUpdated();
    }

    public CartItemAdapter(Context context, List<CartModel> list, OnCartUpdateListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartModel model = list.get(position);
        
        if (model == null) {
            return;
        }

        // Set title with null check
        holder.title.setText(model.getTitle() != null ? model.getTitle() : "");
        
        // Set prices with proper formatting
        holder.originalPrice.setText(String.format("$%.2f", model.getOriginalPrice()));
        // Apply strikethrough to original price
        holder.originalPrice.setPaintFlags(holder.originalPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        holder.currentPrice.setText(String.format("$%.2f", model.getTotalPrice()));
        holder.quantityText.setText(String.valueOf(model.getQuantity()));

        // Load image with placeholder and error handling
        if (model.getImageUrl() != null && !model.getImageUrl().isEmpty()) {
            Glide.with(context)
                    .load(model.getImageUrl())
                    .placeholder(R.drawable.ic_launcher_foreground)
                    .error(R.drawable.ic_launcher_foreground)
                    .centerCrop()
                    .into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.ic_launcher_foreground);
        }

        holder.quantityMinus.setOnClickListener(v -> {
            int newQuantity = model.getQuantity() - 1;
            if (newQuantity > 0) {
                CartManager.getInstance().updateQuantity(model.getItemId(), newQuantity);
                notifyDataSetChanged();
                if (listener != null) listener.onCartUpdated();
            }
        });

        holder.quantityPlus.setOnClickListener(v -> {
            int newQuantity = model.getQuantity() + 1;
            CartManager.getInstance().updateQuantity(model.getItemId(), newQuantity);
            notifyDataSetChanged();
            if (listener != null) listener.onCartUpdated();
        });

        holder.removeButton.setOnClickListener(v -> {
            CartManager.getInstance().removeFromCart(model.getItemId());
            list.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, list.size());
            if (listener != null) listener.onCartUpdated();
            Toast.makeText(context, "Item removed from cart", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void updateList(List<CartModel> newList) {
        this.list = newList;
        notifyDataSetChanged();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView title;
        TextView originalPrice;
        TextView currentPrice;
        TextView quantityMinus;
        TextView quantityText;
        TextView quantityPlus;
        ImageView removeButton;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.cartItemImage);
            title = itemView.findViewById(R.id.cartItemTitle);
            originalPrice = itemView.findViewById(R.id.cartItemOriginalPrice);
            currentPrice = itemView.findViewById(R.id.cartItemCurrentPrice);
            quantityMinus = itemView.findViewById(R.id.cartQuantityMinus);
            quantityText = itemView.findViewById(R.id.cartQuantityText);
            quantityPlus = itemView.findViewById(R.id.cartQuantityPlus);
            removeButton = itemView.findViewById(R.id.cartRemoveButton);
        }
    }
}

