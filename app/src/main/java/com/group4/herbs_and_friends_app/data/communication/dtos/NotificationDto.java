package com.group4.herbs_and_friends_app.data.communication.dtos;

import com.group4.herbs_and_friends_app.data.model.enums.NotificationTypes;

import java.util.Date;

public class NotificationDto {
    private String title;
    private String message;
    private NotificationTypes type;
    private Date sendDate;

    public NotificationDto() {}

    public NotificationDto(String title, String message, NotificationTypes type, Date sendDate) {
        this.title = title;
        this.message = message;
        this.sendDate = sendDate;
        this.type = type;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setType(NotificationTypes type) {
        this.type = type;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public NotificationTypes getType() {
        return type;
    }
}
