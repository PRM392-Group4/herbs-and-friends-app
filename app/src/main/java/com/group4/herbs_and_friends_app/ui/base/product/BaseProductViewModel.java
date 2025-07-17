package com.group4.herbs_and_friends_app.ui.base.product;

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

public abstract class BaseProductViewModel extends ViewModel {
    protected final CategoryRepository cateRepo;
    protected final ProductRepository productRepo;
    protected final MutableLiveData<Params> paramsLive;
    protected final LiveData<List<Product>> productsWithParamsLive;

    /**
     * Inject repos and pass them to super.
     */
    protected BaseProductViewModel(CategoryRepository cateRepo, ProductRepository productRepo) {
        this.cateRepo = cateRepo;
        this.productRepo = productRepo;
        this.paramsLive = new MutableLiveData<>();
        this.productsWithParamsLive = Transformations.switchMap(paramsLive, params -> {
            if (params == null) {
                return productRepo.getAllProducts();
            }
            return productRepo.getProductsWithParams(params);
        });
    }

    // =========================================================
    //                  Product List Operations
    // =========================================================

    /**
     * Params LiveData: input for filtering/sorting.
     */
    public LiveData<Params> getParamsLive() {
        return paramsLive;
    }
    public void setParamsLive(Params params) {
        this.paramsLive.setValue(params);
    }

    /**
     * Product listing operations
     */
    public LiveData<List<Product>> getAllProductsLive() {
        return productRepo.getAllProducts();
    }
    public LiveData<List<Product>> getProductsWithParamsLive() {
        return productsWithParamsLive;
    }
    public LiveData<Product> getSelectedProductLive(String productId) {
        return productRepo.getProductById(productId);
    }

    /**
     * Category listing operations
     */
    public LiveData<List<Category>> getAllCategoriesLive() {
        return cateRepo.getCategories();
    }
    public LiveData<List<Category>> getParentCategoriesLive() {
        return cateRepo.getParentCategories();
    }
}
