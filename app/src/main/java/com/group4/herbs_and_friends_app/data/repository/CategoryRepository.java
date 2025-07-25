package com.group4.herbs_and_friends_app.data.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.group4.herbs_and_friends_app.data.model.Category;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class CategoryRepository {
    private FirebaseFirestore firestore;
    private CollectionReference categories;

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
                    // Get parent categories
                    List<Category> parentCategories = new ArrayList<>();
                    for (DocumentSnapshot doc : query) {
                        Category category = doc.toObject(Category.class);
                        if (!doc.contains("categoryParentId")) {
                            category.setChildCategory(false);
                            parentCategories.add(category);
                        } else {
                            // Add as child category to parent category
                            for (Category parent : parentCategories) {
                                if (Objects.equals(parent.getId(), category.getCategoryParentId())) {
                                    category.setChildCategory(true);
                                    parent.addChildCategory(category);
                                    break;
                                }
                            }
                        }
                    }

                    // Flat list containing all parent-child categories in arrangement
                    List<Category> arrangedCategories = new ArrayList<>();
                    for (Category parent : parentCategories) {
                        arrangedCategories.add(parent);
                        if (parent.getChildCategories() != null)
                            arrangedCategories.addAll(parent.getChildCategories());
                    }

                    categoryListLive.setValue(arrangedCategories);
                })
                .addOnFailureListener(e -> {
                    categoryListLive.setValue(Collections.emptyList());
                });
        return categoryListLive;
    }

    // Get parent categories (categories without field 'categoryParentId')
    public LiveData<List<Category>> getParentCategories() {
        MutableLiveData<List<Category>> parentCategoriesLive = new MutableLiveData<>();
        categories.get()
                .addOnSuccessListener(query -> {
                    // Get parent categories
                    List<Category> parentCategories = new ArrayList<>();
                    for (DocumentSnapshot doc : query) {
                        Category category = doc.toObject(Category.class);
                        if (!doc.contains("categoryParentId")) {
                            category.setChildCategory(false);
                            parentCategories.add(category);
                        } else {
                            // Add as child category to parent category
                            for (Category parent : parentCategories) {
                                if (Objects.equals(parent.getId(), category.getCategoryParentId())) {
                                    category.setChildCategory(true);
                                    parent.addChildCategory(category);
                                    break;
                                }
                            }
                        }
                    }
                    parentCategoriesLive.setValue(parentCategories);
                })
                .addOnFailureListener(e -> {
                    parentCategoriesLive.setValue(Collections.emptyList());
                });
        return parentCategoriesLive;
    }
}
