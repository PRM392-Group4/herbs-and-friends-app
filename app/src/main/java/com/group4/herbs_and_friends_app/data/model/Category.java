package com.group4.herbs_and_friends_app.data.model;

import com.google.firebase.firestore.DocumentId;

public class Category {
    @DocumentId
    private String id;
    private String name;
    private String categoryParentId;

    public Category() {}

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
}
