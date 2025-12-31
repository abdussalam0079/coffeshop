package com.example.coffeshop2.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coffeshop2.databinding.ActivityNoInternetBinding;

public class NoInternetActivity extends AppCompatActivity {

    private ActivityNoInternetBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityNoInternetBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.retryButton.setOnClickListener(v -> checkInternetAndNavigate());
    }

    private void checkInternetAndNavigate() {
        if (isInternetAvailable()) {
            // Internet is available, go to MainActivity2
            Intent intent = new Intent(NoInternetActivity.this, MainActivity2.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }
        // If no internet, stay on this screen
    }

    private boolean isInternetAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnected();
        }
        return false;
    }
}

