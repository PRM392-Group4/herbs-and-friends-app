package com.group4.herbs_and_friends_app.data.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;
import com.group4.herbs_and_friends_app.utils.DisplayFormat;

import java.util.ArrayList;
import java.util.Date;

public class Product {
    @DocumentId
    private String id;
    private String name;
    private long price;
    private String description;
    private int inStock;
    private String categoryId;
    private ArrayList<String> imageUrls;
    private ArrayList<String> tags;
    private Date createdAt;
    private Date updatedAt;
    @Exclude
    private Category category;

    public Product() {}

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

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getInStock() {
        return inStock;
    }

    public void setInStock(int inStock) {
        this.inStock = inStock;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public ArrayList<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(ArrayList<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Return price in formatted String for display
    public String getPriceDisplay() {
        return DisplayFormat.toMoneyDisplayString(price);
    }

    // Return the first URL string in imageUrls to use as product thumbnail
    public String getThumbnail() {
        if (imageUrls == null || imageUrls.isEmpty()) return null;
        return imageUrls.get(0);
    }


}
