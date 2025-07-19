package com.group4.herbs_and_friends_app.data.model.enums;

public enum Role {
    ADMIN("admin", "Admin"),
    USER("customer", "Khách Hàng");

    private final String value;
    private final String displayName;

    Role(String value, String displayName) {
        this.value = value;
        this.displayName = displayName;
    }

    public String getValue() {
        return value;
    }

    public String getDisplayName() {
        return displayName;
    }
}
