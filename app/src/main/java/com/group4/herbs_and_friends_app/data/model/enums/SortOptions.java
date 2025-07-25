package com.group4.herbs_and_friends_app.data.model.enums;

public enum SortOptions {
    PRICE_DEFAULT("priceDefault", ""),
    PRICE_ASC("priceAsc", "price"),
    PRICE_DESC("priceDesc", "price");

    private final String title;
    private final String value;

    SortOptions(String title, String value) {
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
