package com.group4.herbs_and_friends_app.ui.customer_side.manage;

import android.net.Uri;

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
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HProductManageVM extends ViewModel {
    private CategoryRepository cateRepo;
    private ProductRepository productRepo;
    private MutableLiveData<Params> paramsLive;
    private LiveData<List<Product>> productsWithParamsLive;

    @Inject
    public HProductManageVM(CategoryRepository cateRepo, ProductRepository productRepo) {
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

    // =========================================================
    //                  Product List Operations
    // =========================================================

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

    // =========================================================
    //              Product Management Operations
    // =========================================================

    public LiveData<Boolean> addProduct(Product product) {
        return productRepo.addProduct(product);
    }

    public LiveData<Boolean> updateProduct(String productId, Product product) {
        return productRepo.updateProduct(productId, product);
    }

    // This method is useful if you only want to update specific fields without sending the whole object
    public LiveData<Boolean> updateProductFields(String productId, Map<String, Object> updates) {
        return productRepo.updateProductFields(productId, updates);
    }

    public LiveData<Boolean> deleteProduct(String productId) {
        return productRepo.deleteProduct(productId);
    }

    public LiveData<List<String>> uploadImages(String prodId, List<Uri> uris) {
        return productRepo.uploadImages(prodId, uris);
    }
}
