package com.example.coffeshop2.Domain;

public class CartModel {
    private String id;
    private String itemId;
    private String title;
    private String extra;
    private double price;
    private double originalPrice;
    private int quantity;
    private String imageUrl;

    public CartModel() {}

    public CartModel(String id, String title, String extra, double price, int quantity, String imageUrl) {
        this.id = id;
        this.itemId = id;
        this.title = title;
        this.extra = extra;
        this.price = price;
        this.originalPrice = price;
        this.quantity = quantity;
        this.imageUrl = imageUrl;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getItemId() { return itemId; }
    public void setItemId(String itemId) { this.itemId = itemId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getExtra() { return extra; }
    public void setExtra(String extra) { this.extra = extra; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getOriginalPrice() { return originalPrice; }
    public void setOriginalPrice(double originalPrice) { this.originalPrice = originalPrice; }

    public double getTotalPrice() { return price * quantity; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
}