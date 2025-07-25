package com.group4.herbs_and_friends_app.data.model;

import com.google.firebase.firestore.DocumentId;

import java.util.Date;

public class PushNotificationToken {
    @DocumentId
    private String id;
    private String userId;
    private String token;
    private Date lastActive;
    private String deviceName;

    public PushNotificationToken() {
        this.lastActive = new Date();
    }

    public PushNotificationToken(String userId, String token, String deviceName) {
        this.userId = userId;
        this.token = token;
        this.deviceName = deviceName;
        this.lastActive = new Date();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getLastActive() {
        return lastActive;
    }

    public void setLastActive(Date lastActive) {
        this.lastActive = lastActive;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }
}
