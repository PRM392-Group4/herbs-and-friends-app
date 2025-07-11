package com.group4.herbs_and_friends_app.data.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Cart {

    // ==================================
    // === Fields
    // ==================================

    @DocumentId
    private String cartId;         // userId under the hood

    private long totalPrice;       // (item.price * item.quantity)

    @ServerTimestamp
    private Date createdAt;

    @ServerTimestamp
    private Date updatedAt;

    // ==================================
    // === Constructors
    // ==================================

    public Cart() {
    }

    // ==================================
    // === Methods
    // ==================================

    public String getCartId() {
        return cartId;
    }

    public long getTotalPrice() {
        return totalPrice;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

}
