package com.group4.herbs_and_friends_app.data.model;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.Exclude;

import java.util.Date;

public class Coupon {

    @DocumentId
    private String id;
    private String code;
    private double discount;
    private Date effectiveDate;
    private Date expiryDate;
    private String name;

    public Coupon() {
    }

    public Coupon(String code, double discount, Date effectiveDate, Date expiryDate, String name) {
        this.code = code;
        this.discount = discount;
        this.effectiveDate = effectiveDate;
        this.expiryDate = expiryDate;
        this.name = name;
    }

    // Getters and setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public Date getEffectiveDate() {
        return effectiveDate;
    }

    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Exclude
    public boolean isValid() {
        Date now = new Date();
        return now.after(effectiveDate) && now.before(expiryDate);
    }

    @Exclude
    public long calculateDiscountAmount(long totalAmount) {
        if (!isValid()) {
            return 0;
        }
        return (long) (totalAmount * discount);
    }
}
