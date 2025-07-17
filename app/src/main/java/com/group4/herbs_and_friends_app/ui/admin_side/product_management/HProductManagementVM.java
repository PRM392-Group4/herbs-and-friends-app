package com.group4.herbs_and_friends_app.ui.admin_side.product_management;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import com.group4.herbs_and_friends_app.data.model.Product;
import com.group4.herbs_and_friends_app.data.repository.CategoryRepository;
import com.group4.herbs_and_friends_app.data.repository.ProductRepository;
import com.group4.herbs_and_friends_app.ui.base.product.BaseProductViewModel;

import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HProductManagementVM extends BaseProductViewModel {
    @Inject
    public HProductManagementVM(CategoryRepository cateRepo,
                                ProductRepository productRepo) {
        super(cateRepo, productRepo);
    }

    // =========================================================
    //              Product Management Operations
    // =========================================================

    public LiveData<Boolean> addProduct(Product product) {
        return productRepo.addProduct(product);
    }

    // Update whole object
    public LiveData<Boolean> updateProduct(String productId, Product product) {
        return productRepo.updateProduct(productId, product);
    }

    // Update partially
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