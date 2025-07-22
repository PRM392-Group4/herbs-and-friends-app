package com.group4.herbs_and_friends_app.data.model.enums;

public enum ShippingMethod {
    PICKUP("Pick-up", "Đến lấy", 0),
    STANDARD("Standard", "Tiêu chuẩn (3-7 ngày)", 10000),
    EXPRESS("Express", "Nhanh (1-3 ngày)", 20000),
    ;

    private final String value;
    private final long price;
    private final String displayName;

    ShippingMethod(String value, String displayName, long price) {
        this.value = value;
        this.displayName = displayName;
        this.price = price;
    }

    public static ShippingMethod fromValue(String value) {
        for (ShippingMethod method : ShippingMethod.values()) {
            if (method.value.equals(value)) {
                return method;
            }
        }
        return STANDARD; // Default value
    }

    public long getPrice() {
        return price;
    }

    public String getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }
}
