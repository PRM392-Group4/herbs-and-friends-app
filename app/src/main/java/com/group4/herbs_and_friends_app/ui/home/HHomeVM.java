package com.group4.herbs_and_friends_app.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;

import com.group4.herbs_and_friends_app.data.model.Category;
import com.group4.herbs_and_friends_app.data.model.Params;
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
    private MutableLiveData<Params> paramsLive;
    private LiveData<List<Product>> productsWithParamsLive;

    @Inject
    public HHomeVM(CategoryRepository cateRepo, ProductRepository productRepo) {
        this.cateRepo = cateRepo;
        this.productRepo = productRepo;
        this.paramsLive = new MutableLiveData<>();

        // Get latest with params
        this.productsWithParamsLive = Transformations.switchMap(paramsLive, params -> {
            if (params == null) return getAllProductsLive();

            return productRepo.getProductsWithParams(params);
        });
    }

    public LiveData<Params> getParamsLive() {
        return paramsLive;
    }

    public void setParamsLive(Params params) {
        this.paramsLive.setValue(params);
    }

    public LiveData<List<Product>> getAllProductsLive() {
        return productRepo.getAllProducts();
    }

    public LiveData<List<Product>> getProductsWithParamsLive() {
        return productsWithParamsLive;
    }

    public LiveData<Product> getSelectedProductLive(String productId) {
        return productRepo.getProductById(productId);
    }

    public LiveData<List<Category>> getAllCategoriesLive() {
        return cateRepo.getCategories();
    }

    public LiveData<List<Category>> getParentCategoriesLive() {
        return cateRepo.getParentCategories();
    }
}