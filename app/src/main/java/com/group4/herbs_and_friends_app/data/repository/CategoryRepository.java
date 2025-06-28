package com.group4.herbs_and_friends_app.data.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group4.herbs_and_friends_app.data.model.Category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CategoryRepository {
    private FirebaseFirestore firestore;
    private CollectionReference categories;
    private final String TAG = "cate_repo";

    public CategoryRepository(FirebaseFirestore firestore) {
        this.firestore = firestore;
        getCollectionReference();
    }

    public void getCollectionReference() {
        categories = firestore.collection("categories");
    }

    // Get all categories
    public LiveData<List<Category>> getCategories() {
        MutableLiveData<List<Category>> categoryListLive = new MutableLiveData<>();
        categories.get()
                .addOnSuccessListener(query -> {
                    List<Category> categoryList = query.toObjects(Category.class);
                    categoryListLive.setValue(categoryList);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching categories", e);
                    categoryListLive.setValue(Collections.emptyList());
                });
        return categoryListLive;
    }

    // Get parent categories (categories without field 'categoryParentId')
    public LiveData<List<Category>> getParentCategories() {
        MutableLiveData<List<Category>> parentCategoriesLive = new MutableLiveData<>();
        categories.get()
                .addOnSuccessListener(query -> {
                    List<Category> parentCategories = new ArrayList<>();
                    for (DocumentSnapshot doc : query) {
                        if (!doc.contains("categoryParentId"))
                            parentCategories.add(doc.toObject(Category.class));
                    }
                    parentCategoriesLive.setValue(parentCategories);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error fetching parent categories", e);
                    parentCategoriesLive.setValue(Collections.emptyList());
                });
        return parentCategoriesLive;
    }
}
