package com.group4.herbs_and_friends_app.data.model;

import com.group4.herbs_and_friends_app.data.model.enums.SortOptions;

import java.util.List;

public class Params {
    private String search;
    private List<String> categoryIds;
    private SortOptions sort;

    public Params() {
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public List<String> getCategoryIds() {
        return categoryIds;
    }

    public void setCategoryIds(List<String> categoryIds) {
        this.categoryIds = categoryIds;
    }

    public SortOptions getSort() {
        return sort;
    }

    public void setSort(SortOptions sort) {
        this.sort = sort;
    }

    public void clear() {
        this.search = null;
        this.categoryIds = null;
        this.sort = SortOptions.PRICE_DEFAULT;
    }
}
