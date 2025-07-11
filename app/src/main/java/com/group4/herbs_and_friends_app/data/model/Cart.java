package com.group4.herbs_and_friends_app.data.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
import java.util.List;

public class Cart {

    // ==============================
    // === Fields
    // ==============================

    @DocumentId
    private String id; // user id

    private List<CartItem> items;

    @ServerTimestamp
    private Date createdAt;

    @ServerTimestamp
    private Date updatedAt;

    public Cart() {
    }

    // ==============================
    // === Getters / Setters
    // ==============================

    public String getId() {
        return id;
    }

    public List<CartItem> getItems() {
        return items;
    }

    public void setItems(List<CartItem> items) {
        this.items = items;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Returns the total quantity within the cart
     *
     * @return
     */
    @Exclude
    public int getTotalQuantity() {
        if (items == null) return 0;
        int total = 0;
        for (CartItem item : items) {
            total += item.getQuantity();
        }
        return total;
    }


    /**
     * Returns the total price of the cart
     *
     * @return
     */
    @Exclude
    public long getTotalPrice() {
        if (items == null) return 0;
        long sum = 0;
        for (CartItem item : items) {
            sum += item.getPrice() * item.getQuantity();
        }
        return sum;
    }
}
