package com.group4.herbs_and_friends_app.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.group4.herbs_and_friends_app.data.model.Category;
import com.group4.herbs_and_friends_app.data.model.Product;
import com.group4.herbs_and_friends_app.data.repository.CategoryRepository;
import com.group4.herbs_and_friends_app.data.repository.ProductRepository;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HHomeVM extends ViewModel {
    private CategoryRepository cateRepo;
    private ProductRepository productRepo;

    @Inject
    public HHomeVM(CategoryRepository cateRepo, ProductRepository productRepo) {
        this.cateRepo = cateRepo;
        this.productRepo = productRepo;
    }

    // TODO: Set params/filters to be shared

    //TODO: Get products with params

    public LiveData<List<Product>> getAllProductsLive() {
        return productRepo.getAllProducts();
    }

    public LiveData<Product> getSelectedProductLive(String productId) {
        return productRepo.getProductById(productId);
    }

    public LiveData<List<Category>> getParentCategoriesLive() {
        return cateRepo.getParentCategories();
    }
}