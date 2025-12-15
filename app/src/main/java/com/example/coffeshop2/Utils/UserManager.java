package com.example.coffeshop2.Utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.UUID;

/**
 * UserManager - Handles user authentication and user ID management
 * Uses Firebase Anonymous Authentication for secure user identification
 */
public class UserManager {
    private static final String TAG = "UserManager";
    private static final String PREFS_NAME = "CoffeeShopPrefs";
    private static final String KEY_USER_ID = "user_id";
    
    private static UserManager instance;
    private FirebaseAuth firebaseAuth;
    private Context context;
    private String userId;

    private UserManager(Context context) {
        this.context = context.getApplicationContext();
        this.firebaseAuth = FirebaseAuth.getInstance();
        initializeUser();
    }

    public static synchronized UserManager getInstance(Context context) {
        if (instance == null) {
            instance = new UserManager(context);
        }
        return instance;
    }

    /**
     * Initialize user - use anonymous auth or fallback to device ID
     */
    private void initializeUser() {
        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        
        if (currentUser != null) {
            // User is already authenticated
            userId = currentUser.getUid();
            Log.d(TAG, "User already authenticated: " + userId);
        } else {
            // Try to get saved user ID from preferences
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            userId = prefs.getString(KEY_USER_ID, null);
            
            if (userId == null) {
                // Create new anonymous user
                signInAnonymously();
            } else {
                Log.d(TAG, "Using saved user ID: " + userId);
            }
        }
    }

    /**
     * Sign in anonymously with Firebase Auth
     */
    private void signInAnonymously() {
        firebaseAuth.signInAnonymously()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (user != null) {
                            userId = user.getUid();
                            // Save user ID to preferences
                            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                            prefs.edit().putString(KEY_USER_ID, userId).apply();
                            Log.d(TAG, "Anonymous sign-in successful: " + userId);
                        }
                    } else {
                        // Fallback to device-based ID if auth fails
                        Log.w(TAG, "Anonymous sign-in failed, using device ID", task.getException());
                        userId = getDeviceId();
                        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                        prefs.edit().putString(KEY_USER_ID, userId).apply();
                    }
                });
    }

    /**
     * Get device-based unique ID as fallback
     */
    private String getDeviceId() {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String deviceId = prefs.getString("device_id", null);
        
        if (deviceId == null) {
            deviceId = "device_" + UUID.randomUUID().toString();
            prefs.edit().putString("device_id", deviceId).apply();
        }
        
        return deviceId;
    }

    /**
     * Get current user ID
     */
    public String getUserId() {
        if (userId == null) {
            // Fallback if not initialized
            SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            userId = prefs.getString(KEY_USER_ID, getDeviceId());
        }
        return userId;
    }

    /**
     * Get Firebase User (for authentication checks)
     */
    public FirebaseUser getFirebaseUser() {
        return firebaseAuth.getCurrentUser();
    }

    /**
     * Check if user is authenticated
     */
    public boolean isAuthenticated() {
        return firebaseAuth.getCurrentUser() != null;
    }

    /**
     * Sign out (for testing/admin purposes)
     */
    public void signOut() {
        firebaseAuth.signOut();
        userId = null;
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().remove(KEY_USER_ID).apply();
    }
}

