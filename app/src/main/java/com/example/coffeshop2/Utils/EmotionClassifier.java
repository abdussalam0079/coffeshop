package com.example.coffeshop2.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.util.List;

/**
 * ML Kit Face Detection based Emotion Classifier
 * Uses facial cues to derive simple emotions
 */
public class EmotionClassifier {

    private static final String TAG = "EmotionClassifier";
    private FaceDetector detector;
    
    public EmotionClassifier(Context context) {
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_ACCURATE)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build();
        detector = FaceDetection.getClient(options);
    }
    
    public boolean isModelLoaded() {
        return detector != null;
    }
    
    /**
     * Process bitmap and derive emotion from facial cues
     */
    public String processBitmap(Bitmap bitmap, ProcessCallback callback) {
        InputImage image = InputImage.fromBitmap(bitmap, 0);
        detector.process(image)
                .addOnSuccessListener(faces -> {
                    if (!faces.isEmpty()) {
                        Face face = faces.get(0); // Use largest face
                        String emotion = deriveEmotion(face);
                        callback.onResult(emotion);
                    } else {
                        callback.onResult("neutral");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Face detection failed", e);
                    callback.onResult("neutral");
                });
        return "processing"; // Async operation
    }
    
    /**
     * Derive emotion from ML Kit face detection results
     * Limited to 5 reliable emotions based on smile and eye probabilities
     */
    private String deriveEmotion(Face face) {
        float smile = face.getSmilingProbability() != null ? face.getSmilingProbability() : -1f;
        float leftEye = face.getLeftEyeOpenProbability() != null ? face.getLeftEyeOpenProbability() : -1f;
        float rightEye = face.getRightEyeOpenProbability() != null ? face.getRightEyeOpenProbability() : -1f;
        float eyeAvg = (leftEye + rightEye) / 2f;
        
        Log.d(TAG, "Smile: " + smile + ", EyeAvg: " + eyeAvg);
        
        String currentMood;
        
        if (smile >= 0.7f) {
            currentMood = "HAPPY";
        }
        else if (smile >= 0.3f && smile < 0.7f) {
            currentMood = "NEUTRAL";
        }
        else if (eyeAvg >= 0 && eyeAvg < 0.35f) {
            currentMood = "TIRED";
        }
        else if (smile >= 0 && smile < 0.2f) {
            currentMood = "SAD";
        }
        else {
            currentMood = "CALM";
        }
        
        return currentMood.toLowerCase();
    }
    
    /**
     * Synchronous version for compatibility
     */
    public String classifyEmotion(Bitmap bitmap) {
        // For compatibility, return a default emotion
        // In practice, use processBitmap with callback
        return "neutral";
    }

    public int classifyEmotionIndex(Bitmap bitmap) {
        String emotion = classifyEmotion(bitmap);
        switch (emotion) {
            case "angry": return 0;
            case "sad": return 5;
            case "happy": return 4;
            case "depressed": return 5;
            default: return 4; // Default to happy
        }
    }
    
    public void close() {
        // ML Kit handles cleanup automatically
    }
    
    public interface ProcessCallback {
        void onResult(String emotion);
    }
}