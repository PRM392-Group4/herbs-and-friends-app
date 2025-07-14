package com.group4.herbs_and_friends_app.data.model.enums;

public enum OrderStatus {
    UNPAID("Unpaid", "Chờ thanh toán"),
    PENDING("Pending", "Chờ xác nhận"),
    CONFIRMED("Confirmed", "Đã xác nhận"),
    SHIPPING("Shipping", "Đang giao"),
    COMPLETED("Completed", "Hoàn thành"),
    CANCELLED("Cancelled", "Đã hủy");

    private final String value;
    private final String displayName;

    OrderStatus(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public String getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public static OrderStatus fromValue(String value) {
        for (OrderStatus status : OrderStatus.values()) {
            if (status.value.equals(value)) {
                return status;
            }
        }
        return PENDING; // Default value
    }
}
