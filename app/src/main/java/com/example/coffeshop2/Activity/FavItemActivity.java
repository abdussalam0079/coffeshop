package com.example.coffeshop2.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.coffeshop2.Domain.ItemModel;
import com.example.coffeshop2.Utils.WishlistManager;
import com.example.coffeshop2.databinding.ActivityFavItemBinding;

import java.util.List;

public class FavItemActivity extends AppCompatActivity {

    private ActivityFavItemBinding binding;
    private ItemAdapter adapter;
    private WishlistManager wishlistManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFavItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        wishlistManager = WishlistManager.getInstance();

        // Setup RecyclerView
        binding.favItemTitle.setLayoutManager(new GridLayoutManager(this, 2));
        
        // Explore coffee button (empty state)
        binding.exploreCoffeeButton.setOnClickListener(v -> {
            // Navigate to main screen
            Intent intent = new Intent(this, MainActivity2.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
        
        loadWishlistItems();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadWishlistItems();
    }

    private void loadWishlistItems() {
        List<ItemModel> wishlistItems = wishlistManager.getWishlistItems();
        
        if (wishlistItems.isEmpty()) {
            binding.favItemTitle.setVisibility(View.GONE);
            binding.emptyWishlistLayout.setVisibility(View.VISIBLE);
        } else {
            binding.favItemTitle.setVisibility(View.VISIBLE);
            binding.emptyWishlistLayout.setVisibility(View.GONE);
            adapter = new ItemAdapter(this, wishlistItems);
            binding.favItemTitle.setAdapter(adapter);
        }
    }
}

