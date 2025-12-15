package com.example.coffeshop2.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.coffeshop2.Utils.UserManager;
import com.example.coffeshop2.ViewModel.MainViewModel;
import com.example.coffeshop2.databinding.ActivityOrdersBinding;

import java.util.ArrayList;
import java.util.List;

public class OrdersActivity extends AppCompatActivity {

    private ActivityOrdersBinding binding;
    private MainViewModel viewModel;
    private OrderAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(MainViewModel.class);

        // Setup RecyclerView
        binding.recyclerViewOrders.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OrderAdapter(this, new ArrayList<>());
        binding.recyclerViewOrders.setAdapter(adapter);

        // Start shopping button (empty state)
        binding.startShoppingButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity2.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

        // Initialize user and load orders
        UserManager userManager = UserManager.getInstance(this);
        loadOrders(userManager.getUserId());
    }

    private void loadOrders(String userId) {
        viewModel.getOrders(userId).observe(this, orders -> {
            if (orders != null && !orders.isEmpty()) {
                adapter = new OrderAdapter(this, orders);
                binding.recyclerViewOrders.setAdapter(adapter);
                binding.recyclerViewOrders.setVisibility(View.VISIBLE);
                binding.emptyOrdersLayout.setVisibility(View.GONE);
            } else {
                binding.recyclerViewOrders.setVisibility(View.GONE);
                binding.emptyOrdersLayout.setVisibility(View.VISIBLE);
            }
        });
    }
}

