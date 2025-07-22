package com.group4.herbs_and_friends_app.data.model;

import android.util.Log;

import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Exclude;
import com.group4.herbs_and_friends_app.data.model.enums.OrderStatus;
import com.group4.herbs_and_friends_app.data.model.enums.PaymentMethod;
import com.group4.herbs_and_friends_app.data.model.enums.ShippingMethod;
import com.group4.herbs_and_friends_app.utils.DisplayFormat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Order {
    @DocumentId
    private String id;
    private String userId;        
    private String status;         
    private long total;
    private String paymentMethod;
    private String shippingMethod;
    private DocumentReference coupon;
    private Date placedAt;
    private String couponId;        
    private String note;            
    private String address;
    private String recipientName;   
    private String recipientPhone;
    private DocumentReference user;

    @Exclude
    private List<OrderItem> items;  

    @Exclude
    private Coupon appliedCoupon;  // Cached coupon for display purposes
    @Exclude
    private User orderUser;    // Cached user for display purposes
    public Order() {}

    public Order(String userId, OrderStatus status, long total, PaymentMethod paymentMethod,
                 ShippingMethod shippingMethod, String address) {
        this.userId = userId;
        this.status = status.getValue();
        this.total = total;
        this.paymentMethod = paymentMethod.getValue();
        this.shippingMethod = shippingMethod.getValue();
        this.placedAt = new Date();
        this.address = address;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Exclude
    public DocumentReference getCoupon() {
        return coupon;
    }

    public void setCoupon(DocumentReference coupon) {
        this.coupon = coupon;
    }

    public void setAppliedCoupon(Coupon appliedCoupon) {
        this.appliedCoupon = appliedCoupon;
    }

    @Exclude
    public Coupon getAppliedCoupon() {
        return appliedCoupon;
    }
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getShippingMethod() {
        return shippingMethod;
    }

    public void setShippingMethod(String shippingMethod) {
        this.shippingMethod = shippingMethod;
    }

    public Date getPlacedAt() {
        return placedAt;
    }

    public void setPlacedAt(Date placedAt) {
        this.placedAt = placedAt;
    }

    public String getCouponId() {
        return couponId;
    }

    public void setCouponId(String couponId) {
        this.couponId = couponId;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getRecipientName() {
        return recipientName;
    }

    public void setRecipientName(String recipientName) {
        this.recipientName = recipientName;
    }

    public String getRecipientPhone() {
        return recipientPhone;
    }

    public void setRecipientPhone(String recipientPhone) {
        this.recipientPhone = recipientPhone;
    }

    @Exclude
    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
    }

    @Exclude
    public OrderStatus getStatusEnum() {
        return OrderStatus.fromValue(status);
    }

    public void setStatusEnum(OrderStatus status) {
        this.status = status.getValue();
    }

    @Exclude
    public PaymentMethod getPaymentMethodEnum() {
        return PaymentMethod.fromValue(paymentMethod);
    }

    public void setPaymentMethodEnum(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod.getValue();
    }

    @Exclude
    public ShippingMethod getShippingMethodEnum() {
        return ShippingMethod.fromValue(shippingMethod);
    }

    public void setShippingMethodEnum(ShippingMethod shippingMethod) {
        this.shippingMethod = shippingMethod.getValue();
    }

    @Exclude
    public String getTotalDisplay() {
        return DisplayFormat.toMoneyDisplayString(total);
    }

    @Exclude
    public String getStatusDisplay() {
        return getStatusEnum().getDisplayName();
    }

    @Exclude
    public String getPaymentMethodDisplay() {
        return getPaymentMethodEnum().getDisplayName();
    }

    @Exclude
    public String getShippingMethodDisplay() {
        return getShippingMethodEnum().getDisplayName();
    }

    @Exclude
    public String getPlacedAtDisplay() {
        if (placedAt == null) return "";
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm, dd/MM/yyyy", Locale.getDefault());
        return sdf.format(placedAt);
    }

    @Exclude
    public String getOrderNumber() {
        return "#" + (id != null ? id.substring(0, Math.min(id.length(), 8)) : "unknown");
    }

    @Exclude
    public int getTotalItemCount() {
        if (items == null || items.isEmpty()) return 0;
        int total = 0;
        for (OrderItem item : items) {
            total += item.getQuantity();
        }
        return total;
    }

    @Exclude
    public long getShippingFee() {
        return (long)getShippingMethodEnum().getPrice();
    }

    @Exclude
    public String getShippingFeeDisplay() {
        return DisplayFormat.toMoneyDisplayString(getShippingFee());
    }

    @Exclude
    public long getDiscount() {
        if (appliedCoupon != null && appliedCoupon.isValid()) {
            return appliedCoupon.calculateDiscountAmount(getSubtotal());
        }
        return 0;
    }

    @Exclude
    public String getDiscountDisplay() {
        return DisplayFormat.toMoneyDisplayString(getDiscount());
    }

    @Exclude
    public long getSubtotal() {
        if (appliedCoupon != null && appliedCoupon.isValid()) {
            double discountPercentage = appliedCoupon.getDiscount();
            double discountFactor = 1.0 - discountPercentage;
            Log.d("Order", "Discount factor: " + discountFactor);
            long subtotal = Math.round((total - getShippingMethodEnum().getPrice()) / discountFactor);
            Log.d("Order", "Calculated subtotal: " + subtotal);
            return subtotal;
        } else {
            return Math.round(total - getShippingMethodEnum().getPrice());
        }
    }

    @Exclude
    public String getSubtotalDisplay() {
        return DisplayFormat.toMoneyDisplayString(getSubtotal());
    }

    // Helper method to check if order can be cancelled
    public boolean canBeCancelled() {
        OrderStatus currentStatus = getStatusEnum();
        return currentStatus == OrderStatus.PENDING || currentStatus == OrderStatus.CONFIRMED;
    }

    // Helper method to check if order is completed
    @Exclude
    public boolean isCompleted() {
        return getStatusEnum() == OrderStatus.COMPLETED;
    }

    @Override
    public String toString() {
        return "Order{" +
                "couponId='" + couponId + '\'' +
                ", placedAt=" + placedAt +
                ", coupon=" + coupon +
                ", shippingMethod='" + shippingMethod + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", total=" + total +
                ", status='" + status + '\'' +
                ", userId='" + userId + '\'' +
                ", id='" + id + '\'' +
                ", items=" + items +
                ", appliedCoupon=" + appliedCoupon +
                '}';
    }
}
