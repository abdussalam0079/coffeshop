package com.example.coffeshop2.Repository;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.coffeshop2.Domain.BannerModel;
import com.example.coffeshop2.Domain.CategoryModel;
import com.example.coffeshop2.Domain.PopularModel;
import com.example.coffeshop2.Domain.ItemModel;
import com.example.coffeshop2.Domain.OrderModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

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
                    BannerModel item = child.getValue(BannerModel.class);
                    if (item != null) list.add(item);
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
                    CategoryModel item = child.getValue(CategoryModel.class);
                    if (item != null) list.add(item);
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
                    PopularModel item = child.getValue(PopularModel.class);
                    if (item != null) list.add(item);
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
                    ItemModel item = child.getValue(ItemModel.class);
                    if (item != null) list.add(item);
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
    // 🔥 SAVE ORDER TO FIREBASE
    // =============================
    public void saveOrder(OrderModel order, OnCompleteListener<Void> listener) {
        DatabaseReference ordersRef = firebaseDatabase.getReference("Orders");
        DatabaseReference orderRef = ordersRef.push(); // Generate unique order ID
        
        // Set order ID
        order.setOrderId(orderRef.getKey());
        
        // Save order to Firebase
        orderRef.setValue(order)
                .addOnCompleteListener(listener);
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
