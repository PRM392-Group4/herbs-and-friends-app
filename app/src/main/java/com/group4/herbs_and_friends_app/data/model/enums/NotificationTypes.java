package com.group4.herbs_and_friends_app.data.model.enums;

public enum NotificationTypes {
    ORDER_STATUS_UPDATED("Cập nhật trạng thái đơn hàng"),
    NEW_COUPON_ADDED("Có thêm một giảm giá mới");

    private final String message;

    NotificationTypes(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
