package com.group4.herbs_and_friends_app.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group4.herbs_and_friends_app.data.model.Product;

import java.util.Collections;
import java.util.List;

public class ProductRepository {

    private FirebaseFirestore firestore;
    private CollectionReference products;
    private final String TAG = "product_repo";

    public ProductRepository(FirebaseFirestore firestore) {
        this.firestore = firestore;
        getCollectionReference();
    }

    public void getCollectionReference() {
        products = firestore.collection("products");
    }

    public LiveData<List<Product>> getAllProducts() {
        MutableLiveData<List<Product>> productListLive = new MutableLiveData<>();
        products.get()
                .addOnSuccessListener(query -> {
                    List<Product> productList = query.toObjects(Product.class);
                    productListLive.setValue(productList);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching products", e);
                    productListLive.setValue(Collections.emptyList());
                });
        return productListLive;
    }

    //TODO: Get products with params

    public LiveData<Product> getProductById(String productId) {
        MutableLiveData<Product> productLive = new MutableLiveData<>();
        products.document(productId).get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Product product = doc.toObject(Product.class);
                        productLive.setValue(product);
                    } else productLive.setValue(null);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching product by Id", e);
                    productLive.setValue(null);
                });
        return productLive;
    }
}
