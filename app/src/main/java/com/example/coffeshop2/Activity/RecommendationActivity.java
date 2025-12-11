package com.example.coffeshop2.Activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffeshop2.Utils.MoodDetector;
import com.example.coffeshop2.Domain.MoodModel;
import com.example.coffeshop2.Domain.ItemModel;
import com.example.coffeshop2.ViewModel.MainViewModel;
import com.example.coffeshop2.databinding.ActivityRecommendationBinding;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class RecommendationActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST = 100;
    private static final int PERMISSION_REQUEST_CAMERA = 200;
    
    private ActivityRecommendationBinding binding;
    private MainViewModel viewModel;
    private ItemAdapter adapter;
    private Bitmap capturedImage;
    private String detectedMood;
    private String recommendedCoffee;
    private String recommendationDescription;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecommendationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Initialize MoodDetector with TensorFlow Lite
        MoodDetector.initialize(this);

        // Back button
        binding.backButton.setOnClickListener(v -> finish());

        // Take Selfie button
        binding.takeSelfieButton.setOnClickListener(v -> {
            if (checkCameraPermission()) {
                openCamera();
            } else {
                requestCameraPermission();
            }
        });

        // Setup RecyclerView with 2 columns grid
        binding.recyclerViewRecommendations.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ItemAdapter(this, new ArrayList<>());
        binding.recyclerViewRecommendations.setAdapter(adapter);

        // Load recommendations
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
                openCamera();
            } else {
                Toast.makeText(this, "Camera permission is required for mood detection", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        } else {
            Toast.makeText(this, "Camera not available", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            capturedImage = (Bitmap) data.getExtras().get("data");
            processMoodDetection();
        }
    }

    private void processMoodDetection() {
        if (capturedImage == null) {
            Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBarMood.setVisibility(View.VISIBLE);
        binding.takeSelfieButton.setEnabled(false);

        // Detect mood and get recommendation using MoodDetector
        Pair<String, String> recommendation = MoodDetector.getRecommendationFromImage(capturedImage, this);
        recommendedCoffee = recommendation.first;
        recommendationDescription = recommendation.second;
        
        // Get detected mood for display (we need to detect it separately)
        detectedMood = MoodDetector.detectMood(capturedImage, this);

        // Display mood and recommendation
        binding.moodResultText.setVisibility(View.VISIBLE);
        binding.moodResultText.setText("Detected Mood: " + detectedMood.toUpperCase() + 
                "\nRecommended: " + recommendedCoffee + 
                "\n" + recommendationDescription);
        binding.selfieImageView.setVisibility(View.VISIBLE);
        binding.selfieImageView.setImageBitmap(capturedImage);

        // Save to Firebase
        saveMoodRecommendation();

        // Filter and show recommended coffee items
        filterRecommendationsByMood();

        binding.progressBarMood.setVisibility(View.GONE);
        binding.takeSelfieButton.setEnabled(true);
    }

    private void saveMoodRecommendation() {
        DatabaseReference moodRef = FirebaseDatabase.getInstance().getReference("MoodRecommendations").push();
        
        MoodModel moodModel = new MoodModel(
                moodRef.getKey(),
                detectedMood,
                recommendedCoffee + " - " + recommendationDescription,
                "", // Image URL - can be uploaded to Firebase Storage if needed
                System.currentTimeMillis(),
                "user_" + System.currentTimeMillis() // Simple user ID
        );

        moodRef.setValue(moodModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(this, "Mood recommendation saved!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to save recommendation", Toast.LENGTH_SHORT).show();
                    }
                });
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
                
                // If exact match not found, show all items
                if (moodBasedRecommendations.isEmpty()) {
                    moodBasedRecommendations = allItems;
                }

                adapter = new ItemAdapter(this, moodBasedRecommendations);
                binding.recyclerViewRecommendations.setAdapter(adapter);
                binding.emptyText.setVisibility(moodBasedRecommendations.isEmpty() ? View.VISIBLE : View.GONE);
            }
        });
    }

    private void loadRecommendations() {
        binding.progressBarRecommendations.setVisibility(View.VISIBLE);

        viewModel.getItems().observe(this, allItems -> {
            if (allItems != null && !allItems.isEmpty()) {
                // Show all items as recommendations (or filter by rating > 4.5)
                List<ItemModel> recommendations = new ArrayList<>();
                for (ItemModel item : allItems) {
                    if (item.getRating() >= 4.5) {
                        recommendations.add(item);
                    }
                }
                
                // If no high-rated items, show all items
                if (recommendations.isEmpty()) {
                    recommendations = allItems;
                }

                adapter = new ItemAdapter(this, recommendations);
                binding.recyclerViewRecommendations.setAdapter(adapter);

                if (recommendations.isEmpty()) {
                    binding.emptyText.setVisibility(View.VISIBLE);
                } else {
                    binding.emptyText.setVisibility(View.GONE);
                }
            } else {
                binding.emptyText.setVisibility(View.VISIBLE);
            }
            binding.progressBarRecommendations.setVisibility(View.GONE);
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Cleanup TensorFlow Lite resources
        MoodDetector.cleanup();
    }
}
