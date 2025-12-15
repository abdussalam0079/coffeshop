package com.example.coffeshop2.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.coffeshop2.Domain.ItemModel;
import com.example.coffeshop2.ViewModel.MainViewModel;
import com.example.coffeshop2.databinding.ActivityMain2Binding;

import java.util.ArrayList;
import java.util.List;

public class MainActivity2 extends AppCompatActivity {

    private ActivityMain2Binding binding;
    private MainViewModel viewModel;
    private Context ctx;
    private List<ItemModel> allItems = new ArrayList<>();
    private RecyclerView.Adapter<?> searchAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ctx = this;
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Initialize user authentication (anonymous auth)
        com.example.coffeshop2.Utils.UserManager.getInstance(this);

        initBanner();
        initCategory();
        initPopular();
        initBottomNav();
        initSearch();
    }

    // =======================
    //  BANNER OBSERVER
    // =======================
    private void initBanner() {
        binding.progressBarbanner.setVisibility(View.VISIBLE);
        viewModel.getBanners().observe(this, items -> {
            if (items != null && !items.isEmpty()) {
                Glide.with(MainActivity2.this)
                        .load(items.get(0).getUrl())
                        .into(binding.banner);
            }
            binding.progressBarbanner.setVisibility(View.GONE);
        });
    }

    // =======================
    //  CATEGORY OBSERVER
    // =======================
    private void initCategory() {
        // Use the correct ProgressBar ID from your XML
        binding.progressBarCategory.setVisibility(View.VISIBLE);

        viewModel.getCategories().observe(this, categories -> {
            if (categories != null && !categories.isEmpty()) {
                // Set up the RecyclerView with click listener
                CategoryAdapter adapter = new CategoryAdapter(ctx, categories, category -> {
                    // Navigate to ListActivity with category ID
                    Intent intent = new Intent(MainActivity2.this, ListActivity.class);
                    intent.putExtra("categoryId", String.valueOf(category.getId()));
                    startActivity(intent);
                });
                binding.categoryTitle.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
                binding.categoryTitle.setAdapter(adapter);
            }
            // Hide the progress bar
            binding.progressBarCategory.setVisibility(View.GONE);
        });
    }

    // =======================
    //  POPULAR OBSERVER
    // =======================
    private void initPopular() {
        binding.progressBarPopular.setVisibility(View.VISIBLE);

        viewModel.getPopular().observe(this, popularItems -> {
            if (popularItems != null && !popularItems.isEmpty()) {
                binding.recyclerViewPopular.setLayoutManager(
                        new LinearLayoutManager(this, RecyclerView.HORIZONTAL, false));
                binding.recyclerViewPopular.setAdapter(new PopularAdapter(ctx, popularItems));
            }
            binding.progressBarPopular.setVisibility(View.GONE);
        });
    }

    // =======================
    //  BOTTOM NAVIGATION
    // =======================
    private void initBottomNav() {
        binding.navExplore.setOnClickListener(v ->
                startActivity(new Intent(this, MainActivity2.class)));

        binding.navCart.setOnClickListener(v ->
                startActivity(new Intent(this, CartActivity.class)));

        binding.navWishlist.setOnClickListener(v ->
                startActivity(new Intent(this, FavItemActivity.class)));

        binding.navOrders.setOnClickListener(v ->
                startActivity(new Intent(this, OrdersActivity.class)));

        binding.navProfile.setOnClickListener(v ->
                startActivity(new Intent(this, RecommendationActivity.class)));
    }

    // =======================
    //  SEARCH FUNCTIONALITY
    // =======================
    private void initSearch() {
        // Observe all items from ViewModel
        viewModel.getItems().observe(this, items -> {
            if (items != null && !items.isEmpty()) {
                allItems = new ArrayList<>(items);
            } else {
                allItems = new ArrayList<>();
            }
        });

        // Set up search results RecyclerView
        binding.searchResultsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Set up TextWatcher for search input
        binding.editTextText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String query = s.toString().trim();
                if (query.isEmpty()) {
                    showOriginalContent();
                } else {
                    List<ItemModel> filtered = performSearch(query);
                    updateSearchResults(filtered);
                    showSearchResults();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private List<ItemModel> performSearch(String query) {
        List<ItemModel> filtered = new ArrayList<>();
        if (query.isEmpty() || allItems.isEmpty()) {
            return filtered;
        }
        
        String lowerQuery = query.toLowerCase();
        for (ItemModel item : allItems) {
            if (item == null) continue;
            
            // Search in title
            boolean matches = false;
            if (item.getTitle() != null && item.getTitle().toLowerCase().contains(lowerQuery)) {
                matches = true;
            }
            // Search in description
            else if (item.getDescription() != null && item.getDescription().toLowerCase().contains(lowerQuery)) {
                matches = true;
            }
            // Search in extra
            else if (item.getExtra() != null && item.getExtra().toLowerCase().contains(lowerQuery)) {
                matches = true;
            }
            
            if (matches) {
                filtered.add(item);
            }
        }
        return filtered;
    }

    private void updateSearchResults(List<ItemModel> filtered) {
        // Create new adapter with filtered results
        searchAdapter = new SearchResultAdapter(ctx, filtered);
        binding.searchResultsRecyclerView.setAdapter(searchAdapter);
    }

    private void showOriginalContent() {
        binding.searchResultsRecyclerView.setVisibility(View.GONE);
        binding.searchResultsRecyclerView.animate().alpha(0f).setDuration(200).start();
        
        binding.banner.setVisibility(View.VISIBLE);
        binding.banner.animate().alpha(1f).setDuration(300).start();
        binding.tvCategories.setVisibility(View.VISIBLE);
        binding.tvCategories.animate().alpha(1f).setDuration(300).start();
        binding.categoryTitle.setVisibility(View.VISIBLE);
        binding.categoryTitle.animate().alpha(1f).setDuration(300).start();
        binding.popularHeader.setVisibility(View.VISIBLE);
        binding.popularHeader.animate().alpha(1f).setDuration(300).start();
        binding.recyclerViewPopular.setVisibility(View.VISIBLE);
        binding.recyclerViewPopular.animate().alpha(1f).setDuration(300).start();
    }

    private void showSearchResults() {
        binding.banner.setVisibility(View.GONE);
        binding.banner.animate().alpha(0f).setDuration(200).start();
        binding.tvCategories.setVisibility(View.GONE);
        binding.tvCategories.animate().alpha(0f).setDuration(200).start();
        binding.categoryTitle.setVisibility(View.GONE);
        binding.categoryTitle.animate().alpha(0f).setDuration(200).start();
        binding.popularHeader.setVisibility(View.GONE);
        binding.popularHeader.animate().alpha(0f).setDuration(200).start();
        binding.recyclerViewPopular.setVisibility(View.GONE);
        binding.recyclerViewPopular.animate().alpha(0f).setDuration(200).start();
        
        binding.searchResultsRecyclerView.setVisibility(View.VISIBLE);
        binding.searchResultsRecyclerView.setAlpha(0f);
        binding.searchResultsRecyclerView.animate().alpha(1f).setDuration(300).start();
    }
}
