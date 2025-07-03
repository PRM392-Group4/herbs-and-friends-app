package com.group4.herbs_and_friends_app.data.model;

import com.google.firebase.firestore.DocumentId;

import java.util.Date;

import com.google.firebase.firestore.DocumentId;
import com.google.type.DateTime;

import java.util.Date;

public class User {
    @DocumentId
    private String uid;
    private String name;
    private String email;
    private String phone;
    private String password;
    private String address;
    private Date createdAt;
    private Date updateAt;

    public User() {
    }

    public String getUid() {
        return uid;
    }
    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Date updateAt) {
        this.updateAt = updateAt;
    }
}
