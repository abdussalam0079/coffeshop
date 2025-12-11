package com.example.coffeshop2.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.coffeshop2.Domain.BannerModel;
import com.example.coffeshop2.Domain.CategoryModel;
import com.example.coffeshop2.Domain.PopularModel;
import com.example.coffeshop2.Domain.ItemModel;
import com.example.coffeshop2.Repository.MainRepositry;

import java.util.List;

public class MainViewModel extends ViewModel {

    private final MainRepositry repo = MainRepositry.getInstance();

    public LiveData<List<BannerModel>> getBanners() {
        return repo.loadBanner();
    }

    public LiveData<List<CategoryModel>> getCategories() {
        return repo.loadCategory();
    }

    public LiveData<List<PopularModel>> getPopular() {
        return repo.loadPopular();
    }

    public LiveData<List<ItemModel>> getItems() {
        return repo.loadItems();
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repo.removeAllListeners();
    }

    // Remove duplicate methods - these are handled by the repo

}
