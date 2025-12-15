package com.example.coffeshop2.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffeshop2.ViewModel.MainViewModel;
import com.example.coffeshop2.databinding.ActivityListBinding;
import com.example.coffeshop2.Domain.ItemModel;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {

    private ActivityListBinding binding;
    private MainViewModel viewModel;
    private ItemAdapter adapter;
    private String selectedCategoryId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get category ID from intent
        selectedCategoryId = getIntent().getStringExtra("categoryId");
        if (selectedCategoryId == null) {
            selectedCategoryId = "0"; // Default to first category
        }

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Setup RecyclerView with 2 columns
        binding.recyclerViewItems.setLayoutManager(new GridLayoutManager(this, 2));
        adapter = new ItemAdapter(this, new ArrayList<>());
        binding.recyclerViewItems.setAdapter(adapter);

        // Back button
        binding.backButton.setOnClickListener(v -> finish());

        // Load items
        loadItems();
    }

    private void loadItems() {
        binding.progressBarItems.setVisibility(View.VISIBLE);
        binding.recyclerViewItems.setVisibility(View.GONE);

        viewModel.getItems().observe(this, allItems -> {
            if (allItems != null) {
                // Filter items by selected category
                List<ItemModel> filteredItems = new ArrayList<>();
                for (ItemModel item : allItems) {
                    if (item != null && selectedCategoryId != null && selectedCategoryId.equals(item.getCategoryId())) {
                        filteredItems.add(item);
                    }
                }

                adapter = new ItemAdapter(this, filteredItems);
                binding.recyclerViewItems.setAdapter(adapter);

                if (filteredItems.isEmpty()) {
                    // Show empty state message
                    Toast.makeText(this, "No items found for this category", Toast.LENGTH_SHORT).show();
                } else {
                    binding.recyclerViewItems.setVisibility(View.VISIBLE);
                    binding.recyclerViewItems.animate().alpha(1f).setDuration(300).start();
                }
            } else {
                Toast.makeText(this, "Failed to load items. Please try again.", Toast.LENGTH_SHORT).show();
            }
            binding.progressBarItems.setVisibility(View.GONE);
        });
    }
}

