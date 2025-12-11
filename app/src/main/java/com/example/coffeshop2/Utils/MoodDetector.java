package com.example.coffeshop2.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Pair;

/**
 * Mood Detector Utility
 * Maps detected emotions to coffee recommendations using TensorFlow Lite
 */
public class MoodDetector {
    
    private static EmotionClassifier emotionClassifier;
    private static boolean isInitialized = false;
    
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
     * Map emotion label to mood
     */
    private static String mapLabelToMood(String label) {
        switch (label.toLowerCase()) {
            case "happy":
                return "happy";
            case "sad":
                return "sad";
            case "angry":
                return "angry";
            case "neutral":
                return "neutral";
            case "surprise": // map surprise to neutral or happy
                return "neutral";
            case "fear":
            case "disgust":
                return "tired"; // or map as needed
            default:
                return "neutral";
        }
    }
    
    /**
     * Get coffee recommendation based on mood
     * Returns Pair<CoffeeName, Description>
     */
    public static Pair<String, String> getRecommendation(String mood) {
        switch (mood.toLowerCase()) {
            case "happy":
                return new Pair<>("Iced Mocha", "Something sweet to celebrate!");
            case "sad":
                return new Pair<>("Caramel Latte", "Comforting warm flavors.");
            case "angry":
                return new Pair<>("Cold Brew", "Cooling, smooth and calming.");
            case "tired":
                return new Pair<>("Double Espresso", "Strong and energizing.");
            default:
                return new Pair<>("Cappuccino", "A balanced choice.");
        }
    }
    
    /**
     * Detect mood from image bitmap using TensorFlow Lite
     */
    public static String detectMood(Bitmap image, Context context) {
        // Initialize if not already done
        if (!isInitialized) {
            initialize(context);
        }
        
        // If model is available, use it
        if (emotionClassifier != null && isInitialized) {
            try {
                String detectedLabel = emotionClassifier.classifyEmotion(image);
                return mapLabelToMood(detectedLabel);
            } catch (Exception e) {
                e.printStackTrace();
                // Fallback to simulated detection
                return simulateMoodDetection();
            }
        } else {
            // Fallback: simulate mood detection if model not available
            return simulateMoodDetection();
        }
    }
    
    /**
     * Fallback mood detection (for testing or when model is not available)
     */
    private static String simulateMoodDetection() {
        // Simulate random mood detection
        String[] moods = {"happy", "sad", "angry", "neutral", "tired"};
        java.util.Random random = new java.util.Random();
        return moods[random.nextInt(moods.length)];
    }
    
    /**
     * Get recommendation pair (coffee name and description) from image
     */
    public static Pair<String, String> getRecommendationFromImage(Bitmap image, Context context) {
        String mood = detectMood(image, context);
        return getRecommendation(mood);
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
