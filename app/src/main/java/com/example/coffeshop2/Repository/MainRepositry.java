package com.example.coffeshop2.Repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.coffeshop2.Domain.BannerModel;
import com.example.coffeshop2.Domain.CategoryModel;
import com.example.coffeshop2.Domain.ItemModel;
import com.example.coffeshop2.Domain.OrderModel;
import com.example.coffeshop2.Domain.PopularModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainRepositry {

    private static final String TAG = "MainRepository";

    private static MainRepositry instance;
    private final FirebaseDatabase firebaseDatabase;

    private ValueEventListener bannerListener;
    private ValueEventListener categoryListener;
    private ValueEventListener popularListener;
    private ValueEventListener itemsListener;

    private MainRepositry() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        // Enable offline persistence (optional, helps with connectivity)
        firebaseDatabase.setPersistenceEnabled(false);
        // Set logging to debug mode
        // FirebaseDatabase.getInstance().setLogLevel(Logger.Level.DEBUG);
    }

    public static synchronized MainRepositry getInstance() {
        if (instance == null) {
            instance = new MainRepositry();
        }
        return instance;
    }

    // =============================
    // 🔥 LOAD BANNERS (REALTIME)
    // =============================
    public LiveData<List<BannerModel>> loadBanner() {

        MutableLiveData<List<BannerModel>> listData = new MutableLiveData<>();
        DatabaseReference ref = firebaseDatabase.getReference("Banner");

        if (bannerListener != null) ref.removeEventListener(bannerListener);

        bannerListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<BannerModel> list = new ArrayList<>();

                for (DataSnapshot child : snapshot.getChildren()) {
                    try {
                        BannerModel item = child.getValue(BannerModel.class);
                        if (item != null) {
                            list.add(item);
                        }
                    } catch (Exception e) {
                        Log.w(TAG, "Failed to parse banner item: " + child.getKey() + ", error: " + e.getMessage());
                        // Skip this item and continue
                    }
                }

                listData.postValue(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Banner Cancelled: " + error.getMessage());
                listData.postValue(Collections.emptyList());
            }
        };

        ref.addValueEventListener(bannerListener);
        return listData;
    }


    // =============================
    // 🔥 LOAD CATEGORY (REALTIME)
    // =============================
    public LiveData<List<CategoryModel>> loadCategory() {

        MutableLiveData<List<CategoryModel>> listData = new MutableLiveData<>();
        DatabaseReference ref = firebaseDatabase.getReference("Category");

        if (categoryListener != null) ref.removeEventListener(categoryListener);

        categoryListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<CategoryModel> list = new ArrayList<>();

                for (DataSnapshot child : snapshot.getChildren()) {
                    try {
                        CategoryModel item = child.getValue(CategoryModel.class);
                        if (item != null) {
                            list.add(item);
                        }
                    } catch (Exception e) {
                        Log.w(TAG, "Failed to parse category item: " + child.getKey() + ", error: " + e.getMessage());
                        // Skip this item and continue
                    }
                }

                listData.postValue(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Category Cancelled: " + error.getMessage());
                listData.postValue(Collections.emptyList());
            }
        };

        ref.addValueEventListener(categoryListener);
        return listData;
    }

    // =============================
    // 🔥 LOAD POPULAR (REALTIME)
    // =============================
    public LiveData<List<PopularModel>> loadPopular() {

        MutableLiveData<List<PopularModel>> listData = new MutableLiveData<>();
        DatabaseReference ref = firebaseDatabase.getReference("Popular");

        if (popularListener != null) ref.removeEventListener(popularListener);

        popularListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<PopularModel> list = new ArrayList<>();

                for (DataSnapshot child : snapshot.getChildren()) {
                    try {
                        PopularModel item = child.getValue(PopularModel.class);
                        if (item != null) {
                            list.add(item);
                        }
                    } catch (Exception e) {
                        Log.w(TAG, "Failed to parse popular item: " + child.getKey() + ", error: " + e.getMessage());
                        // Skip this item and continue
                    }
                }

                listData.postValue(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Popular Cancelled: " + error.getMessage());
                listData.postValue(Collections.emptyList());
            }
        };

        ref.addValueEventListener(popularListener);
        return listData;
    }

    // =============================
    // 🔥 LOAD ITEMS (REALTIME)
    // =============================
    public LiveData<List<ItemModel>> loadItems() {

        MutableLiveData<List<ItemModel>> listData = new MutableLiveData<>();
        DatabaseReference ref = firebaseDatabase.getReference("Items");

        if (itemsListener != null) ref.removeEventListener(itemsListener);

        itemsListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<ItemModel> list = new ArrayList<>();

                for (DataSnapshot child : snapshot.getChildren()) {
                    try {
                        ItemModel item = child.getValue(ItemModel.class);
                        if (item != null) {
                            list.add(item);
                        }
                    } catch (Exception e) {
                        Log.w(TAG, "Failed to parse item: " + child.getKey() + ", error: " + e.getMessage());
                        // Skip this item and continue
                    }
                }

                listData.postValue(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Items Cancelled: " + error.getMessage());
                listData.postValue(Collections.emptyList());
            }
        };

        ref.addValueEventListener(itemsListener);
        return listData;
    }

    // =============================
    // 🔥 SAVE ORDER TO FIREBASE (User-based structure)
    // =============================
    public void saveOrder(OrderModel order, String userId, OnCompleteListener<Void> listener) {
        try {
            // Structure: orders/{userId}/{orderId}
            DatabaseReference ordersRef = firebaseDatabase.getReference("orders").child(userId);
            DatabaseReference orderRef = ordersRef.push(); // Generate unique order ID
            
            // Set order ID and user ID
            String orderId = orderRef.getKey();
            if (orderId != null) {
                order.setOrderId(orderId);
                order.setUserId(userId);
            }
            
            Log.d(TAG, "Attempting to save order with ID: " + orderId + " for user: " + userId);
            Log.d(TAG, "Database URL: " + firebaseDatabase.getReference().toString());
            Log.d(TAG, "Orders path: " + orderRef.toString());
            
            // Validate order data before saving
            if (!validateOrder(order)) {
                Log.e(TAG, "Order validation failed");
                TaskCompletionSource<Void> taskSource = new TaskCompletionSource<>();
                taskSource.setException(new Exception("Order validation failed: Invalid order data"));
                listener.onComplete(taskSource.getTask());
                return;
            }
            
            // Save order to Firebase
            orderRef.setValue(order)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Order saved successfully! Order ID: " + orderId + " for user: " + userId);
                        } else {
                            Exception exception = task.getException();
                            if (exception != null) {
                                Log.e(TAG, "Failed to save order: " + exception.getMessage(), exception);
                                Log.e(TAG, "Exception class: " + exception.getClass().getName());
                            } else {
                                Log.e(TAG, "Failed to save order: Unknown error");
                            }
                        }
                        listener.onComplete(task);
                    });
        } catch (Exception e) {
            Log.e(TAG, "Exception while saving order", e);
            // Create a failed task
            TaskCompletionSource<Void> taskSource = new TaskCompletionSource<>();
            taskSource.setException(e);
            listener.onComplete(taskSource.getTask());
        }
    }

    /**
     * Validate order data before saving
     */
    private boolean validateOrder(OrderModel order) {
        // Validate total is a number and positive
        if (order.getTotal() <= 0) {
            Log.e(TAG, "Invalid total: " + order.getTotal());
            return false;
        }
        
        // Validate status is from predefined list
        String[] validStatuses = {"pending", "confirmed", "preparing", "ready", "delivered", "cancelled"};
        boolean validStatus = false;
        if (order.getStatus() != null) {
            for (String status : validStatuses) {
                if (status.equals(order.getStatus().toLowerCase())) {
                    validStatus = true;
                    break;
                }
            }
        }
        if (!validStatus) {
            Log.e(TAG, "Invalid status: " + order.getStatus());
            return false;
        }
        
        // Validate items list is not empty
        if (order.getItems() == null || order.getItems().isEmpty()) {
            Log.e(TAG, "Order items list is empty");
            return false;
        }
        
        // Validate timestamp is positive
        if (order.getTimestamp() <= 0) {
            Log.e(TAG, "Invalid timestamp: " + order.getTimestamp());
            return false;
        }
        
        return true;
    }

    // =============================
    // 🔥 LOAD ORDERS (REALTIME) - User-specific
    // =============================
    public LiveData<List<OrderModel>> loadOrders(String userId) {
        MutableLiveData<List<OrderModel>> listData = new MutableLiveData<>();
        // Structure: orders/{userId}
        DatabaseReference ref = firebaseDatabase.getReference("orders").child(userId);

        ref.orderByChild("timestamp").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<OrderModel> list = new ArrayList<>();

                for (DataSnapshot child : snapshot.getChildren()) {
                    try {
                        OrderModel item = child.getValue(OrderModel.class);
                        if (item != null) {
                            // Ensure orderId and userId are set
                            if (item.getOrderId() == null || item.getOrderId().isEmpty()) {
                                item.setOrderId(child.getKey());
                            }
                            if (item.getUserId() == null || item.getUserId().isEmpty()) {
                                item.setUserId(userId);
                            }
                            list.add(item);
                        }
                    } catch (Exception e) {
                        Log.w(TAG, "Failed to parse order: " + child.getKey() + ", error: " + e.getMessage());
                        // Skip this item and continue
                    }
                }

                // Sort by timestamp (newest first)
                Collections.sort(list, (o1, o2) -> Long.compare(o2.getTimestamp(), o1.getTimestamp()));
                listData.postValue(list);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Orders Cancelled: " + error.getMessage());
                listData.postValue(Collections.emptyList());
            }
        });

        return listData;
    }


    // ======================
    // ❌ REMOVE ALL LISTENERS
    // ======================
    public void removeAllListeners() {

        DatabaseReference bannerRef = firebaseDatabase.getReference("Banner");
        DatabaseReference categoryRef = firebaseDatabase.getReference("Category");

        if (bannerListener != null) {
            bannerRef.removeEventListener(bannerListener);
            bannerListener = null;
        }

        if (categoryListener != null) {
            categoryRef.removeEventListener(categoryListener);
            categoryListener = null;
        }

        DatabaseReference popularRef = firebaseDatabase.getReference("Popular");
        if (popularListener != null) {
            popularRef.removeEventListener(popularListener);
            popularListener = null;
        }

        DatabaseReference itemsRef = firebaseDatabase.getReference("Items");
        if (itemsListener != null) {
            itemsRef.removeEventListener(itemsListener);
            itemsListener = null;
        }
    }
}
