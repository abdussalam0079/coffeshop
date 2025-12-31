package com.example.coffeshop2.Utils;

import com.example.coffeshop2.Domain.ItemModel;

import java.util.ArrayList;
import java.util.List;

public class WishlistManager {
    private static WishlistManager instance;
    private List<ItemModel> wishlistItems;

    private WishlistManager() {
        wishlistItems = new ArrayList<>();
    }

    public static synchronized WishlistManager getInstance() {
        if (instance == null) {
            instance = new WishlistManager();
        }
        return instance;
    }

    public void addToWishlist(ItemModel item) {
        // Check if item already exists in wishlist
        for (ItemModel wishlistItem : wishlistItems) {
            if (wishlistItem.getTitle().equals(item.getTitle())) {
                return; // Already in wishlist
            }
        }
        wishlistItems.add(item);
    }

    public void removeFromWishlist(String itemTitle) {
        wishlistItems.removeIf(item -> item.getTitle().equals(itemTitle));
    }

    public boolean isInWishlist(String itemTitle) {
        for (ItemModel item : wishlistItems) {
            if (item.getTitle().equals(itemTitle)) {
                return true;
            }
        }
        return false;
    }

    public List<ItemModel> getWishlistItems() {
        return wishlistItems;
    }

    public void clearWishlist() {
        wishlistItems.clear();
    }

    public int getWishlistCount() {
        return wishlistItems.size();
    }
}

