package com.example.coffeshop2.Domain;

import java.util.List;

public class CartModel {
    private String itemId;
    private String title;
    private String extra;
    private double price;
    private double originalPrice;
    private int quantity;
    private String imageUrl;

    public CartModel() {
        // Required empty constructor
    }

    public CartModel(String itemId, String title, String extra, double price, int quantity, String imageUrl) {
        this.itemId = itemId;
        this.title = title;
        this.extra = extra;
        this.price = price;
        this.originalPrice = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    public String getItemId() { return itemId; }
    public String getTitle() { return title; }
    public String getExtra() { return extra; }
    public double getPrice() { return price; }
    public double getOriginalPrice() { return originalPrice; }
    public int getQuantity() { return quantity; }
    public String getImageUrl() { return imageUrl; }
    public double getTotalPrice() { return price * quantity; }

    public void setItemId(String itemId) { this.itemId = itemId; }
    public void setTitle(String title) { this.title = title; }
    public void setExtra(String extra) { this.extra = extra; }
    public void setPrice(double price) { this.price = price; }
    public void setOriginalPrice(double originalPrice) { this.originalPrice = originalPrice; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}

