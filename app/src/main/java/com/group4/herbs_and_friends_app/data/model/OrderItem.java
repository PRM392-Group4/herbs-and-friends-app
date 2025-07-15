package com.group4.herbs_and_friends_app.data.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;
import com.group4.herbs_and_friends_app.utils.DisplayFormat;

public class OrderItem {

    @DocumentId
    private String productId;
    private String name;
    private int quantity;
    private long unitPrice;

    public OrderItem() {}

    public OrderItem(String productId, String name, int quantity, long unitPrice) {
        this.productId = productId;
        this.name = name;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public long getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(long unitPrice) {
        this.unitPrice = unitPrice;
    }

    // Helper methods for display
    @Exclude
    public String getUnitPriceDisplay() {
        return DisplayFormat.toMoneyDisplayString(unitPrice);
    }

    @Override
    public String toString() {
        return "OrderItem{" +
                "productId='" + productId + '\'' +
                ", name='" + name + '\'' +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                '}';
    }

}
