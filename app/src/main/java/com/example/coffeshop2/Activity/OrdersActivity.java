package com.example.coffeshop2.Activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.coffeshop2.databinding.ActivityOrdersBinding;

public class OrdersActivity extends AppCompatActivity {

    private ActivityOrdersBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOrdersBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Start shopping button (empty state)
        binding.startShoppingButton.setOnClickListener(v -> {
            // Navigate to main screen
            Intent intent = new Intent(this, MainActivity2.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });
    }
}

