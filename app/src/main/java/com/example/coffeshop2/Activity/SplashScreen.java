package com.example.coffeshop2.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import androidx.appcompat.app.AppCompatActivity;

import com.example.coffeshop2.databinding.ActivitySplashScreenBinding;

public class SplashScreen extends AppCompatActivity {

    private ActivitySplashScreenBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySplashScreenBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Animate title text
        Animation textAnim = AnimationUtils.loadAnimation(this, com.example.coffeshop2.R.anim.splash_text_in);
        binding.textView.startAnimation(textAnim);

        binding.button.setOnClickListener(v -> {
            Intent intent = new Intent(SplashScreen.this, MainActivity2.class);
            startActivity(intent);
        });
    }
}
