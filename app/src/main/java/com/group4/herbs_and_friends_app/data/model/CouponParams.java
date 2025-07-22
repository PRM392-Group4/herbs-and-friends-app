package com.group4.herbs_and_friends_app.data.model;

import java.util.Date;

public class CouponParams {
    private String search;
    private Date effectiveDate;
    private Date expiryDate;

    public CouponParams() {
    }

    public String getSearch() {
        return search;
    }

    public void setSearch(String search) {
        this.search = search;
    }

    public void clear() {
        this.search = null;
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
}
