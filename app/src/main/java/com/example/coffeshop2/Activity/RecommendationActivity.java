package com.example.coffeshop2.Activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.coffeshop2.Domain.ItemModel;
import com.example.coffeshop2.Domain.MoodModel;
import com.example.coffeshop2.Utils.MoodDetector;
import com.example.coffeshop2.ViewModel.MainViewModel;
import com.example.coffeshop2.databinding.ActivityRecommendationBinding;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class RecommendationActivity extends AppCompatActivity {

    private static final String TAG = "RecommendationActivity";
    private static final int PERMISSION_REQUEST_CAMERA = 200;
    
    private ActivityRecommendationBinding binding;
    private MainViewModel viewModel;
    private ItemAdapter adapter;
    private ProcessCameraProvider cameraProvider;
    private FaceDetector detector;
    
    // Face detection timing
    private boolean isDetecting = false;
    private boolean faceDetected = false;
    private long faceDetectionStartTime = 0;
    private static final long DETECTION_DURATION = 5000; // 5 seconds
    private String detectedMood = "neutral";
    private String recommendedCoffee = "Espresso";
    private String recommendationDescription = "A classic choice";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecommendationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);
        
        // Initialize ML Kit Face Detector
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .build();
        detector = FaceDetection.getClient(options);

        // Initialize MoodDetector
        MoodDetector.initialize(this);

        // Back button
        binding.backButton.setOnClickListener(v -> finish());

        // Start Camera button
        binding.takeSelfieButton.setText("📹 Start Camera");
        binding.takeSelfieButton.setOnClickListener(v -> {
            if (checkCameraPermission()) {
                startCamera();
            } else {
                requestCameraPermission();
            }
        });

        // Setup RecyclerView
        binding.recyclerViewRecommendations.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ItemAdapter(this, new ArrayList<>());
        binding.recyclerViewRecommendations.setAdapter(adapter);

        loadRecommendations();
    }

    private boolean checkCameraPermission() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) 
                == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCameraPermission() {
        ActivityCompat.requestPermissions(this, 
                new String[]{Manifest.permission.CAMERA}, 
                PERMISSION_REQUEST_CAMERA);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CAMERA) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera();
            } else {
                Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void startCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        
        cameraProviderFuture.addListener(() -> {
            try {
                cameraProvider = cameraProviderFuture.get();
                bindCamera();
            } catch (ExecutionException | InterruptedException e) {
                Log.e(TAG, "Camera start failed", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindCamera() {
        Preview preview = new Preview.Builder().build();
        preview.setSurfaceProvider(binding.previewView.getSurfaceProvider());

        ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build();

        imageAnalysis.setAnalyzer(Executors.newSingleThreadExecutor(), this::detectEmotion);

        CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;

        try {
            cameraProvider.unbindAll();
            cameraProvider.bindToLifecycle(this, cameraSelector, preview, imageAnalysis);

            binding.previewView.setVisibility(View.VISIBLE);
            binding.takeSelfieButton.setText("📹 Detecting...");
            binding.takeSelfieButton.setEnabled(false);
            binding.moodResultText.setVisibility(View.VISIBLE);
            binding.moodResultText.setText("Look at camera for face detection...");
            
            isDetecting = true;
            faceDetected = false;
        } catch (Exception e) {
            Log.e(TAG, "Camera bind failed", e);
        }
    }

    @ExperimentalGetImage
    private void detectEmotion(ImageProxy imageProxy) {
        if (!isDetecting) {
            imageProxy.close();
            return;
        }

        InputImage image = InputImage.fromMediaImage(imageProxy.getImage(), imageProxy.getImageInfo().getRotationDegrees());
        
        detector.process(image)
                .addOnSuccessListener(faces -> {
                    if (!isDetecting) {
                        imageProxy.close();
                        return;
                    }

                    if (!faces.isEmpty()) {
                        if (!faceDetected) {
                            // Face detected for first time, start timer
                            faceDetected = true;
                            faceDetectionStartTime = System.currentTimeMillis();
                            runOnUiThread(() -> {
                                binding.moodResultText.setText("Face detected! Stay still for 5 seconds...");
                            });
                        } else {
                            // Check if 5 seconds have passed
                            long elapsedTime = System.currentTimeMillis() - faceDetectionStartTime;
                            long remainingTime = (DETECTION_DURATION - elapsedTime) / 1000;
                            
                            if (elapsedTime >= DETECTION_DURATION) {
                                // 5 seconds completed, analyze mood using REALISTIC detection
                                Face face = faces.get(0);
                                float smile = face.getSmilingProbability() != null ? face.getSmilingProbability() : -1f;
                                float leftEye = face.getLeftEyeOpenProbability() != null ? face.getLeftEyeOpenProbability() : -1f;
                                float rightEye = face.getRightEyeOpenProbability() != null ? face.getRightEyeOpenProbability() : -1f;
                                float eyeAvg = (leftEye + rightEye) / 2f;

                                Log.d(TAG, "Smile: " + smile + ", EyeAvg: " + eyeAvg);

                                String currentMood;
                                
                                // REALISTIC 5-emotion detection using ML Kit limitations
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
                                
                                final String finalMood = currentMood;
                                
                                // Stop detection and show result
                                isDetecting = false;
                                cameraProvider.unbindAll();
                                runOnUiThread(() -> {
                                    binding.previewView.setVisibility(View.GONE);
                                    handleMood(finalMood);
                                });
                            } else {
                                // Update countdown
                                runOnUiThread(() -> {
                                    binding.moodResultText.setText("Stay still... " + (remainingTime + 1) + " seconds remaining");
                                });
                            }
                        }
                    } else {
                        // No face detected, reset
                        if (faceDetected) {
                            faceDetected = false;
                            runOnUiThread(() -> {
                                binding.moodResultText.setText("Face lost! Please look at camera...");
                            });
                        }
                    }
                    imageProxy.close();
                })
                .addOnFailureListener(e -> imageProxy.close());
    }


    private void handleMood(String mood) {
        // Handle empty mood case
        if (mood == null || mood.trim().isEmpty()) {
            mood = "NEUTRAL";
        }
        
        detectedMood = mood.toLowerCase();
        Toast.makeText(this, "Detected Mood: " + mood, Toast.LENGTH_LONG).show();
        
        // Get coffee recommendation
        MoodDetector.MoodRecommendationResult result = MoodDetector.getRecommendationForMood(detectedMood);
        recommendedCoffee = result.recommendation.first;
        recommendationDescription = result.recommendation.second;
        
        displayResults();
        saveMoodRecommendation();
        filterRecommendationsByMood();
    }
    
    private void displayResults() {
        String moodEmoji = getMoodEmoji(detectedMood);
        binding.moodResultText.setText(moodEmoji + " Detected Mood: " + detectedMood.toUpperCase() + 
                "\n☕ Recommended: " + recommendedCoffee + 
                "\n📝 " + recommendationDescription);
        binding.takeSelfieButton.setText("📹 Try Again");
        binding.takeSelfieButton.setEnabled(true);
    }
    
    private String getMoodEmoji(String mood) {
        switch (mood.toLowerCase()) {
            case "happy": return "😄";
            case "sad": return "😢";
            case "neutral": return "😐";
            case "tired": return "😴";
            case "calm": return "😌";
            default: return "🙂";
        }
    }

    private void saveMoodRecommendation() {
        DatabaseReference moodRef = FirebaseDatabase.getInstance().getReference("MoodRecommendations").push();
        
        MoodModel moodModel = new MoodModel(
                moodRef.getKey(),
                detectedMood,
                recommendedCoffee + " - " + recommendationDescription,
                "",
                System.currentTimeMillis(),
                "user_" + System.currentTimeMillis()
        );

        moodRef.setValue(moodModel);
    }

    private void filterRecommendationsByMood() {
        viewModel.getItems().observe(this, allItems -> {
            if (allItems != null && !allItems.isEmpty()) {
                List<ItemModel> moodBasedRecommendations = new ArrayList<>();
                for (ItemModel item : allItems) {
                    if (item.getTitle().equalsIgnoreCase(recommendedCoffee) || 
                        item.getTitle().toLowerCase().contains(recommendedCoffee.toLowerCase())) {
                        moodBasedRecommendations.add(item);
                    }
                }
                
                if (moodBasedRecommendations.isEmpty()) {
                    moodBasedRecommendations = allItems;
                }

                adapter = new ItemAdapter(this, moodBasedRecommendations);
                binding.recyclerViewRecommendations.setAdapter(adapter);
            }
        });
    }

    private void loadRecommendations() {
        binding.progressBarRecommendations.setVisibility(View.VISIBLE);

        viewModel.getItems().observe(this, allItems -> {
            if (allItems != null && !allItems.isEmpty()) {
                List<ItemModel> recommendations = new ArrayList<>();
                for (ItemModel item : allItems) {
                    if (item.getRating() >= 4.5) {
                        recommendations.add(item);
                    }
                }
                
                if (recommendations.isEmpty()) {
                    recommendations = allItems;
                }

                adapter = new ItemAdapter(this, recommendations);
                binding.recyclerViewRecommendations.setAdapter(adapter);
                binding.emptyText.setVisibility(recommendations.isEmpty() ? View.VISIBLE : View.GONE);
            } else {
                binding.emptyText.setVisibility(View.VISIBLE);
            }
            binding.progressBarRecommendations.setVisibility(View.GONE);
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (cameraProvider != null) {
            cameraProvider.unbindAll();
        }
        MoodDetector.cleanup();
    }
}
