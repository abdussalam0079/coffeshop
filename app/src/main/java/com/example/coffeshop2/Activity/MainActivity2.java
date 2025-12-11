package com.example.coffeshop2.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.coffeshop2.ViewModel.MainViewModel;
import com.example.coffeshop2.databinding.ActivityMain2Binding;

public class MainActivity2 extends AppCompatActivity {

    private ActivityMain2Binding binding;
    private MainViewModel viewModel;
    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMain2Binding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ctx = this;
        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        initBanner();
        initCategory();
        initPopular();
        initBottomNav();
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
}
