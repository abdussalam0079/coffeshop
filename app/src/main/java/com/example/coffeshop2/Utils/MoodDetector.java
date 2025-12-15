package com.example.coffeshop2.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.util.Pair;

/**
 * Mood Detector Utility
 * Maps detected emotions to coffee recommendations using TensorFlow Lite
 */
public class MoodDetector {
    
    private static final String TAG = "MoodDetector";
    private static EmotionClassifier emotionClassifier;
    private static boolean isInitialized = false;

    public static class MoodRecommendationResult {
        public final int rawEmotionIndex;   // 0-7 FER+ index, -1 if simulated
        public final int moodBucket;        // 0=happy,1=sad,2=stressed,3=relaxed
        public final String moodLabel;      // happy/sad/stressed/relaxed
        public final Pair<String, String> recommendation;

        public MoodRecommendationResult(int rawEmotionIndex, int moodBucket, String moodLabel, Pair<String, String> recommendation) {
            this.rawEmotionIndex = rawEmotionIndex;
            this.moodBucket = moodBucket;
            this.moodLabel = moodLabel;
            this.recommendation = recommendation;
        }
    }
    
    /**
     * Initialize TensorFlow Lite model
     */
    public static void initialize(Context context) {
        if (!isInitialized) {
            try {
                emotionClassifier = new EmotionClassifier(context);
                isInitialized = emotionClassifier.isModelLoaded();
            } catch (Exception e) {
                e.printStackTrace();
                emotionClassifier = null;
                isInitialized = false;
            }
        }
    }
    
    /**
     * Map raw emotion index (0-6 from your model: anger, contempt, disgust, fear, happy, sadness, surprise)
     * to coffee bucket (0=happy,1=sad,2=stressed,3=relaxed).
     */
    private static int mapIndexToCoffeeBucket(int index) {
        switch (index) {
            case 4: // happy
            case 6: // surprise -> treat as positive
                return 0; // happy
            case 5: // sadness
                return 1; // sad
            case 0: // anger
            case 1: // contempt
            case 2: // disgust
            case 3: // fear
                return 2; // stressed
            default:
                return 3; // relaxed/neutral-ish
        }
    }
    
    /**
     * Get random coffee recommendation based on mood index.
     */
    public static Pair<String, String> getRecommendation(int moodIndex) {
        return CoffeeRecommender.getRandomCoffee(moodIndex);
    }
    
    /**
     * Detect mood index from image bitmap using TensorFlow Lite.
     */
    public static int detectMoodIndex(Bitmap image, Context context) {
        if (!isInitialized) {
            initialize(context);
        }

        if (emotionClassifier != null && isInitialized) {
            try {
                int rawIdx = emotionClassifier.classifyEmotionIndex(image);
                int bucket = mapIndexToCoffeeBucket(rawIdx);
                Log.d(TAG, "Raw emotion idx=" + rawIdx + " bucket=" + bucket);
                return bucket;
            } catch (Exception e) {
                e.printStackTrace();
                return simulateMoodDetectionIndex();
            }
        } else {
            return simulateMoodDetectionIndex();
        }
    }

    /**
     * Detect mood label from image bitmap using TensorFlow Lite.
     */
    public static String detectMood(Bitmap image, Context context) {
        int bucket = detectMoodIndex(image, context);
        // Convert bucket back to label for UI
        switch (bucket) {
            case 0:
                return "happy";
            case 1:
                return "sad";
            case 2:
                return "stressed";
            case 3:
            default:
                return "relaxed";
        }
    }
    
    /**
     * Fallback mood detection (for testing or when model is not available)
     */
    private static int simulateMoodDetectionIndex() {
        int[] moodBuckets = {0, 1, 2, 3};
        java.util.Random random = new java.util.Random();
        int idx = moodBuckets[random.nextInt(moodBuckets.length)];
        Log.w(TAG, "Simulated mood bucket=" + idx);
        return idx;
    }
    
    /**
     * Get recommendation pair (coffee name and description) from image
     */
    public static Pair<String, String> getRecommendationFromImage(Bitmap image, Context context) {
        int moodBucket = detectMoodIndex(image, context);
        return getRecommendation(moodBucket);
    }

    /**
     * Single-call helper: detect mood and get recommendation together to ensure consistency.
     */
    public static MoodRecommendationResult detectAndRecommend(Bitmap image, Context context) {
        int rawIdx = -1;
        int moodBucket;
        if (!isInitialized) {
            initialize(context);
        }

        if (emotionClassifier != null && isInitialized) {
            try {
                rawIdx = emotionClassifier.classifyEmotionIndex(image);
                moodBucket = mapIndexToCoffeeBucket(rawIdx);
                Log.d(TAG, "detectAndRecommend rawIdx=" + rawIdx + " bucket=" + moodBucket);
            } catch (Exception e) {
                e.printStackTrace();
                moodBucket = simulateMoodDetectionIndex();
            }
        } else {
            moodBucket = simulateMoodDetectionIndex();
        }

        String label = moodBucket == 0 ? "happy"
                : moodBucket == 1 ? "sad"
                : moodBucket == 2 ? "stressed"
                : "relaxed";

        Pair<String, String> rec = getRecommendation(moodBucket);
        return new MoodRecommendationResult(rawIdx, moodBucket, label, rec);
    }
    
    /**
     * Cleanup resources
     */
    public static void cleanup() {
        if (emotionClassifier != null) {
            emotionClassifier.close();
            emotionClassifier = null;
            isInitialized = false;
        }
    }
}
