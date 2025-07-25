package com.group4.herbs_and_friends_app.data.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;

import java.util.ArrayList;
import java.util.List;

public class Category {
    @DocumentId
    private String id;
    private String name;
    private String categoryParentId;
    @Exclude
    private Category categoryParent;
    @Exclude
    private List<Category> childCategories;
    @Exclude
    private boolean isChildCategory;
    @Exclude
    private boolean isChecked;

    public Category() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategoryParentId() {
        return categoryParentId;
    }

    public void setCategoryParentId(String categoryParentId) {
        this.categoryParentId = categoryParentId;
    }

    public List<Category> getChildCategories() {
        return childCategories;
    }

    public void setChildCategories(List<Category> childCategories) {
        this.childCategories = childCategories;
    }

    public void addChildCategory(Category child) {
        if (childCategories == null) childCategories = new ArrayList<>();
        childCategories.add(child);
    }

    public boolean isChildCategory() {
        return isChildCategory;
    }

    public void setChildCategory(boolean childCategory) {
        isChildCategory = childCategory;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }
}
