package com.example.coffeshop2.Domain;

public class MoodModel {
    private String moodId;
    private String detectedMood; // "happy", "sad", "energetic", "calm", "stressed", "neutral"
    private String recommendedCoffee;
    private String imageUrl; // Selfie URL stored in Firebase Storage
    private long timestamp;
    private String userId; // Optional: for tracking user

    public MoodModel() {
        // Required empty constructor for Firebase
    }

    public MoodModel(String moodId, String detectedMood, String recommendedCoffee, String imageUrl, long timestamp, String userId) {
        this.moodId = moodId;
        this.detectedMood = detectedMood;
        this.recommendedCoffee = recommendedCoffee;
        this.imageUrl = imageUrl;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    public String getMoodId() { return moodId; }
    public String getDetectedMood() { return detectedMood; }
    public String getRecommendedCoffee() { return recommendedCoffee; }
    public String getImageUrl() { return imageUrl; }
    public long getTimestamp() { return timestamp; }
    public String getUserId() { return userId; }

    public void setMoodId(String moodId) { this.moodId = moodId; }
    public void setDetectedMood(String detectedMood) { this.detectedMood = detectedMood; }
    public void setRecommendedCoffee(String recommendedCoffee) { this.recommendedCoffee = recommendedCoffee; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
    public void setUserId(String userId) { this.userId = userId; }
}

