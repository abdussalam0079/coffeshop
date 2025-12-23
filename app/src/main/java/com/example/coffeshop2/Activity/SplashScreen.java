package com.example.coffeshop2.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.example.coffeshop2.R;
import com.example.coffeshop2.databinding.ActivitySplashScreenBinding;

public class SplashScreen extends AppCompatActivity {

    private ActivitySplashScreenBinding binding;
    private static final int SPLASH_DURATION = 4000; // Total time on splash screen
    private boolean hasNavigated = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        startAnimations();

        binding.getStartedButton.setOnClickListener(v -> checkInternetAndNavigate());

        // Automatically navigate after the splash duration, only if not already navigated
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (!hasNavigated) {
                checkInternetAndNavigate();
            }
        }, SPLASH_DURATION);
    }

    private void startAnimations() {
        // Load animations
        Animation logoAnim = AnimationUtils.loadAnimation(this, R.anim.splash_logo_animation);
        Animation textAnim = AnimationUtils.loadAnimation(this, R.anim.splash_fade_in);
        Animation buttonAnim = AnimationUtils.loadAnimation(this, R.anim.splash_button_animation);

        // Handler for staggered animations
        Handler handler = new Handler(Looper.getMainLooper());

        // 1. Animate Logo
        binding.appLogo.setVisibility(View.VISIBLE);
        binding.appLogo.startAnimation(logoAnim);

        // 2. Animate Text after 500ms
        handler.postDelayed(() -> {
            binding.welcomeText.setVisibility(View.VISIBLE);
            binding.welcomeText.startAnimation(textAnim);
        }, 500);

        // 3. Animate Button after 1000ms
        handler.postDelayed(() -> {
            binding.getStartedButton.setVisibility(View.VISIBLE);
            binding.getStartedButton.startAnimation(buttonAnim);
        }, 1000);
    }

    private void checkInternetAndNavigate() {
        if (hasNavigated) {
            return;
        }
        hasNavigated = true;

        Intent intent;
        if (isInternetAvailable()) {
            intent = new Intent(SplashScreen.this, MainActivity2.class);
        } else {
            intent = new Intent(SplashScreen.this, NoInternetActivity.class);
        }
        startActivity(intent);
        finish(); // Prevent user from going back to the splash screen
    }

    private boolean isInternetAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) return false;

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }
}
