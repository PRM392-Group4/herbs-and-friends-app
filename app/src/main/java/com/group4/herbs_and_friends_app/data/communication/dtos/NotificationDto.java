package com.group4.herbs_and_friends_app.data.communication.dtos;

import com.group4.herbs_and_friends_app.data.model.enums.NotificationTypes;

import java.util.Date;

public class NotificationDto {
    private String title;
    private NotificationTypes type;
    private Date sendDate;

    public NotificationDto() {
    }

    public NotificationDto(String title, NotificationTypes type, Date sendDate) {
        this.title = title;
        this.type = type;
        this.sendDate = sendDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getSendDate() {
        return sendDate;
    }

    public void setSendDate(Date sendDate) {
        this.sendDate = sendDate;
    }

    public NotificationTypes getType() {
        return type;
    }

    public void setType(NotificationTypes type) {
        this.type = type;
    }

    public String getMessage() {
        return type.getMessage();
    }
}
