package com.group4.herbs_and_friends_app.data.model.enums;

public enum PaymentMethod {
    MOMO("Momo", "Momo"),
    ZALOPAY("ZaloPay", "ZaloPay"),
    CASH("Cash", "Tiền mặt");

    private final String value;
    private final String displayName;

    PaymentMethod(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public static PaymentMethod fromValue(String value) {
        for (PaymentMethod method : PaymentMethod.values()) {
            if (method.value.equals(value)) {
                return method;
            }
        }
        return CASH; // Default value
    }

    public String getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }
}
