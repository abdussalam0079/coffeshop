package com.example.coffeshop2.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.coffeshop2.Domain.CartModel;
import com.example.coffeshop2.Domain.ItemModel;
import com.example.coffeshop2.R;
import com.example.coffeshop2.Utils.CartManager;
import com.example.coffeshop2.Utils.WishlistManager;
import com.example.coffeshop2.databinding.ActivityItemDetailBinding;

public class ItemDetailActivity extends AppCompatActivity {

    private ActivityItemDetailBinding binding;
    private ItemModel item;
    private int quantity = 1;
    private String selectedSize = "Small";
    private double basePrice;
    private String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityItemDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get item from intent
        String title = getIntent().getStringExtra("title");
        String description = getIntent().getStringExtra("description");
        String extra = getIntent().getStringExtra("extra");
        basePrice = getIntent().getDoubleExtra("price", 0.0);
        double rating = getIntent().getDoubleExtra("rating", 0.0);
        imageUrl = getIntent().getStringExtra("imageUrl");

        // Create temporary item model
        item = new ItemModel();
        item.setTitle(title);
        item.setDescription(description);
        item.setExtra(extra);
        item.setPrice(basePrice);
        item.setRating(rating);

        setupViews();
        setupSizeSelection();
        setupQuantityControls();
        updatePrice();
    }

    private void setupViews() {
        // Back button
        binding.backButton.setOnClickListener(v -> finish());

        // Heart button (wishlist)
        WishlistManager wishlistManager = WishlistManager.getInstance();
        boolean isInWishlist = wishlistManager.isInWishlist(item.getTitle());
        updateWishlistButton(isInWishlist);

        binding.heartButton.setOnClickListener(v -> {
            if (wishlistManager.isInWishlist(item.getTitle())) {
                wishlistManager.removeFromWishlist(item.getTitle());
                updateWishlistButton(false);
                Toast.makeText(this, "Removed from wishlist", Toast.LENGTH_SHORT).show();
            } else {
                wishlistManager.addToWishlist(item);
                updateWishlistButton(true);
                Toast.makeText(this, "Added to wishlist", Toast.LENGTH_SHORT).show();
            }
        });

        // Set item details
        binding.itemTitle.setText(item.getTitle());
        binding.itemDescription.setText(item.getDescription());
        binding.itemRating.setText(String.format("%.1f", item.getRating()));

        // Load image
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Glide.with(this)
                    .load(imageUrl)
                    .into(binding.itemImage);
        }

        // Add to cart button
        binding.addToCartButton.setOnClickListener(v -> {
            String cartImageUrl = (imageUrl != null) ? imageUrl : "";
            
            CartModel cartItem = new CartModel(
                    item.getTitle() + "_" + System.currentTimeMillis(), // Unique ID
                    item.getTitle(),
                    item.getExtra(),
                    calculateFinalPrice(),
                    quantity,
                    cartImageUrl
            );
            
            CartManager.getInstance().addToCart(cartItem);
            Toast.makeText(this, item.getTitle() + " added to cart", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupSizeSelection() {
        binding.sizeSmall.setOnClickListener(v -> selectSize("Small", binding.sizeSmall));
        binding.sizeMedium.setOnClickListener(v -> selectSize("Medium", binding.sizeMedium));
        binding.sizeLarge.setOnClickListener(v -> selectSize("Large", binding.sizeLarge));

        // Set initial selection
        selectSize("Small", binding.sizeSmall);
    }

    private void selectSize(String size, View selectedView) {
        selectedSize = size;
        
        // Reset all sizes
        binding.sizeSmall.setBackgroundResource(R.drawable.unselected_pill);
        binding.sizeMedium.setBackgroundResource(R.drawable.unselected_pill);
        binding.sizeLarge.setBackgroundResource(R.drawable.unselected_pill);
        binding.sizeSmall.setTextColor(getResources().getColor(android.R.color.black));
        binding.sizeMedium.setTextColor(getResources().getColor(android.R.color.black));
        binding.sizeLarge.setTextColor(getResources().getColor(android.R.color.black));

        // Highlight selected size
        selectedView.setBackgroundResource(R.drawable.bg_category_selected);
        ((android.widget.TextView) selectedView).setTextColor(getResources().getColor(android.R.color.white));

        // Update price based on size
        updatePrice();
    }

    private void setupQuantityControls() {
        // Set initial quantity display
        binding.quantityText.setText(String.valueOf(quantity));

        binding.quantityMinus.setOnClickListener(v -> {
            if (quantity > 1) {
                quantity--;
                binding.quantityText.setText(String.valueOf(quantity));
                updatePrice();
            }
        });

        binding.quantityPlus.setOnClickListener(v -> {
            quantity++;
            binding.quantityText.setText(String.valueOf(quantity));
            updatePrice();
        });
    }

    private void updatePrice() {
        double totalPrice = calculateFinalPrice();
        binding.addToCartButton.setText(String.format("Add to cart - $%.2f", totalPrice));
    }

    private double calculateFinalPrice() {
        double sizeMultiplier = 1.0;
        switch (selectedSize) {
            case "Small":
                sizeMultiplier = 1.0;
                break;
            case "Medium":
                sizeMultiplier = 1.2;
                break;
            case "Large":
                sizeMultiplier = 1.5;
                break;
        }
        return basePrice * sizeMultiplier * quantity;
    }

    private void updateWishlistButton(boolean isInWishlist) {
        if (isInWishlist) {
            binding.heartButton.setColorFilter(getResources().getColor(R.color.orange));
            binding.heartButton.setAlpha(1.0f);
        } else {
            binding.heartButton.setColorFilter(getResources().getColor(R.color.grey));
            binding.heartButton.setAlpha(0.6f);
        }
    }
}

