package com.example.coffeshop2.Domain;

public class MoodModel {
    private String id;
    private String mood;
    private String recommendation;
    private String imageUrl;
    private long timestamp;
    private String userId;

    public MoodModel() {}

    public MoodModel(String id, String mood, String recommendation, String imageUrl, long timestamp, String userId) {
        this.id = id;
        this.mood = mood;
        this.recommendation = recommendation;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getMood() { return mood; }
    public void setMood(String mood) { this.mood = mood; }

    public String getRecommendation() { return recommendation; }
    public void setRecommendation(String recommendation) { this.recommendation = recommendation; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
}