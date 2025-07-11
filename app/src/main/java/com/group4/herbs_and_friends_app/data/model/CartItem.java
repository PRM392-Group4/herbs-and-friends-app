package com.group4.herbs_and_friends_app.data.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class CartItem {

    // =====================================
    // === Fields
    // =====================================

    private String productId;
    private String name;
    private long price;
    private String imageUrl;
    private int quantity;

    @ServerTimestamp
    private Date addedAt;

    // =====================================
    // === Constructors
    // =====================================

    // Required no-arg constructor for Firestore
    public CartItem() {
    }

    public CartItem(String productId, String name, long price, String imageUrl, int quantity) {
        this.productId = productId;
        this.name = name;
        this.price = price;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
    }

    // =====================================
    // === Getters & Setters
    // =====================================

    public String getProductId() {
        return productId;
    }

    public String getName() {
        return name;
    }

    public long getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getQuantity() {
        return quantity;
    }

    public Date getAddedAt() {
        return addedAt;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
