package com.group4.herbs_and_friends_app.data.model.enums;

public enum CouponSortOptions {

    EXPIRY_DATE_ASC("expiryDateAsc", "expiryDate"),
    EXPIRY_DATE_DESC("expiryDateDesc", "expiryDate"),
    EFFECTIVE_DATE_ASC("effectiveDateAsc", "effectiveDate"),
    EFFECTIVE_DATE_DESC("effectiveDateDesc", "effectiveDate");

    private final String title;
    private final String value;

    CouponSortOptions(String title, String value) {
        this.title = title;
        this.value = value;
    }

    public String getTitle() {
        return title;
    }

    public String getValue() {
        return value;
    }
}
