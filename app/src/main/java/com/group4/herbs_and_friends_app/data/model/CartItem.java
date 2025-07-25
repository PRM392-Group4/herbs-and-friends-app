package com.group4.herbs_and_friends_app.data.model;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.Objects;

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
    // === Methods
    // =====================================

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return price == cartItem.price && quantity == cartItem.quantity && Objects.equals(productId, cartItem.productId) && Objects.equals(name, cartItem.name) && Objects.equals(imageUrl, cartItem.imageUrl) && Objects.equals(addedAt, cartItem.addedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId, name, price, imageUrl, quantity, addedAt);
    }


    // =====================================
    // === Getters & Setters
    // =====================================

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Date getAddedAt() {
        return addedAt;
    }
}
