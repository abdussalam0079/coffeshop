package com.example.coffeshop2.Domain;

import java.util.List;

public class ItemModel {
    private String categoryId;
    private String title;
    private String description;
    private String extra;
    private double price;
    private double rating;
    private List<String> picUrl;

    public ItemModel() {
        // Required empty constructor for Firebase
    }

    public ItemModel(String categoryId, String title, String description, String extra, double price, double rating, List<String> picUrl) {
        this.categoryId = categoryId;
        this.title = title;
        this.description = description;
        this.extra = extra;
        this.price = price;
        this.rating = rating;
        this.picUrl = picUrl;
    }

    public String getCategoryId() { return categoryId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getExtra() { return extra; }
    public double getPrice() { return price; }
    public double getRating() { return rating; }
    public List<String> getPicUrl() { return picUrl; }

    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setExtra(String extra) { this.extra = extra; }
    public void setPrice(double price) { this.price = price; }
    public void setRating(double rating) { this.rating = rating; }
    public void setPicUrl(List<String> picUrl) { this.picUrl = picUrl; }
}

