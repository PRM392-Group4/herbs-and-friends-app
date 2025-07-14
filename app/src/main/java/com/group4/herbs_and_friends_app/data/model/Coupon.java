package com.group4.herbs_and_friends_app.data.model;

import com.google.firebase.firestore.DocumentId;
import com.group4.herbs_and_friends_app.utils.DisplayFormat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Coupon {

    @DocumentId
    private String id;
    private String code;
    private double discount;        
    private Date effectiveDate;
    private Date expiryDate;
    private String name;

    public Coupon() {}

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

    // Helper methods for display
    public String getDiscountDisplay() {
        return String.format(Locale.getDefault(), "%.0f%%", discount * 100);
    }

    public String getEffectiveDateDisplay() {
        if (effectiveDate == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(effectiveDate);
    }

    public String getExpiryDateDisplay() {
        if (expiryDate == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        return sdf.format(expiryDate);
    }

    // Business logic methods
    public boolean isValid() {
        Date now = new Date();
        return now.after(effectiveDate) && now.before(expiryDate);
    }

    public boolean isExpired() {
        Date now = new Date();
        return now.after(expiryDate);
    }

    public boolean isNotYetActive() {
        Date now = new Date();
        return now.before(effectiveDate);
    }

    public String getStatus() {
        if (isNotYetActive()) {
            return "Chưa hiệu lực";
        } else if (isExpired()) {
            return "Đã hết hạn";
        } else if (isValid()) {
            return "Đang hiệu lực";
        } else {
            return "Không xác định";
        }
    }

    // Calculate discount amount for a given total
    public long calculateDiscountAmount(long totalAmount) {
        if (!isValid()) {
            return 0;
        }
        return (long) (totalAmount * discount);
    }

    @Override
    public String toString() {
        return "Coupon{" +
                "id='" + id + '\'' +
                ", code='" + code + '\'' +
                ", discount=" + discount +
                ", effectiveDate=" + effectiveDate +
                ", expiryDate=" + expiryDate +
                ", name='" + name + '\'' +
                '}';
    }
}
