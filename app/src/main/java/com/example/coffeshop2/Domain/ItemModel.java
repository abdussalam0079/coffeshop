package com.example.coffeshop2.Domain;

import java.util.List;

public class ItemModel {
    private String id;
    private String title;
    private String description;
    private String extra;
    private String categoryId;
    private List<String> picUrl;
    private double price;
    private double rating;
    private String category;

    public ItemModel() {}

    public ItemModel(String id, String title, String description, String extra, List<String> picUrl, double price, double rating, String category) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.extra = extra;
        this.categoryId = category;
        this.picUrl = picUrl;
        this.price = price;
        this.rating = rating;
        this.category = category;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getExtra() { return extra; }
    public void setExtra(String extra) { this.extra = extra; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public List<String> getPicUrl() { return picUrl; }
    public void setPicUrl(List<String> picUrl) { this.picUrl = picUrl; }

    public String getImageUrl() { 
        return (picUrl != null && !picUrl.isEmpty()) ? picUrl.get(0) : null;
    }
    public void setImageUrl(String imageUrl) { 
        if (picUrl != null && !picUrl.isEmpty()) {
            picUrl.set(0, imageUrl);
        }
    }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}