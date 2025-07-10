package com.group4.herbs_and_friends_app.data.repository;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.group4.herbs_and_friends_app.data.model.Params;
import com.group4.herbs_and_friends_app.data.model.Product;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class ProductRepository {

    private final FirebaseFirestore firestore;
    private final CollectionReference products;
    private final FirebaseStorage storage;
    private final StorageReference storageRef;

    public ProductRepository(FirebaseFirestore firestore,
                             FirebaseStorage storage) {
        this.firestore = firestore;
        this.products  = firestore.collection("products");
        this.storage = storage;
        this.storageRef = storage.getReference().child("products")
                .child("images");
    }

    public LiveData<List<Product>> getAllProducts() {
        MutableLiveData<List<Product>> productListLive = new MutableLiveData<>();
        products.get()
                .addOnSuccessListener(query -> {
                    List<Product> productList = query.toObjects(Product.class);
                    productListLive.setValue(productList);
                })
                .addOnFailureListener(e -> {
                    productListLive.setValue(Collections.emptyList());
                });
        return productListLive;
    }

    public LiveData<List<Product>> getProductsWithParams(Params params) {
        MutableLiveData<List<Product>> productListLive = new MutableLiveData<>();
        Query query = products;

        // Apply categories
        if(params.getCategoryIds() != null && !params.getCategoryIds().isEmpty())
            query = query.whereIn("categoryId", params.getCategoryIds());

        // Apply sort
        if(params.getSort() != null) {
            switch (params.getSort()) {
                case PRICE_DEFAULT: break;
                case PRICE_ASC:
                    query = query.orderBy("price", Query.Direction.ASCENDING);
                    break;
                case PRICE_DESC:
                    query = query.orderBy("price", Query.Direction.DESCENDING);
            }
        }

        query.get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Product> productList = querySnapshot.toObjects(Product.class);

                    // Apply search
                    if(params.getSearch() != null && !params.getSearch().isEmpty()) {
                        List<Product> filteredList = filterBySearch(productList, params.getSearch());
                        productListLive.setValue(filteredList);
                    } else productListLive.setValue(productList);
                })
                .addOnFailureListener(e -> {
                    productListLive.setValue(Collections.emptyList());
                });

        return productListLive;
    }

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
                    productLive.setValue(null);
                });
        return productLive;
    }

    private List<Product> filterBySearch(List<Product> productList, String search) {
        List<Product> filteredList = new ArrayList<>();

        for (Product product : productList) {
            boolean nameMatch = product.getName().toLowerCase().contains(search.toLowerCase());
            boolean tagMatch = product.getTags() != null &&
                    product.getTags()
                            .stream()
                            .anyMatch(tag -> tag.toLowerCase().contains(search.toLowerCase()));
            if(nameMatch || tagMatch) filteredList.add(product);
        }

        return filteredList;
    }

    // Add a new product
    public LiveData<Boolean> addProduct(Product product) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        // Set timestamps
        Date now = new Date();
        product.setCreatedAt(now);
        product.setUpdatedAt(now);

        // Set the product to preserve the generated id
        products
            .document(product.getId())
            .set(product)
            .addOnSuccessListener(aVoid -> {
                result.setValue(true);
            })
            .addOnFailureListener(e -> {
                result.setValue(false);
            });

        return result;
    }

    // Update an existing product
    public LiveData<Boolean> updateProduct(String productId, Product product) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        // Update timestamp
        product.setUpdatedAt(new Date());

        products.document(productId).set(product)
                .addOnSuccessListener(aVoid -> {
                    result.setValue(true);
                })
                .addOnFailureListener(e -> {
                    result.setValue(false);
                });

        return result;
    }

    // Update specific fields of a product
    public LiveData<Boolean> updateProductFields(String productId, java.util.Map<String, Object> updates) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        // Add updated timestamp to the updates
        updates.put("updatedAt", new Date());

        products.document(productId).update(updates)
                .addOnSuccessListener(aVoid -> {
                    result.setValue(true);
                })
                .addOnFailureListener(e -> {
                    result.setValue(false);
                });

        return result;
    }

    // Delete a product
    public LiveData<Boolean> deleteProduct(String productId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        products.document(productId).delete()
                .addOnSuccessListener(aVoid -> {
                    result.setValue(true);
                })
                .addOnFailureListener(e -> {
                    result.setValue(false);
                });

        return result;
    }

    // Check if product exists
    public LiveData<Boolean> productExists(String productId) {
        MutableLiveData<Boolean> result = new MutableLiveData<>();

        products.document(productId).get()
                .addOnSuccessListener(doc -> {
                    result.setValue(doc.exists());
                })
                .addOnFailureListener(e -> {
                    result.setValue(false);
                });

        return result;
    }


    // Upload images to firebase storage then returns a
    // LiveData which emits the list of download-URLs when done.
    public LiveData<List<String>> uploadImages(String prodId, List<Uri> uris) {
        MutableLiveData<List<String>> live = new MutableLiveData<>();
        List<String> urls = new ArrayList<>();
        if (uris.isEmpty()) {
            live.setValue(urls);
            return live;
        }

        for (Uri uri : uris) {
            String fileName = UUID.randomUUID().toString();
            StorageReference ref = storageRef.child(prodId).child(fileName);
            ref.putFile(uri)
                .continueWithTask(task -> {
                    if (!task.isSuccessful()) throw task.getException();
                    return ref.getDownloadUrl();
                })
                .addOnSuccessListener(downloadUri -> {
                    urls.add(downloadUri.toString());
                    if (urls.size() == uris.size()) {
                        live.setValue(urls);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d("Upload Images", "Failed to upload images");
                });
        }
        return live;
    }
}
