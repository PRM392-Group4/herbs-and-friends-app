package com.group4.herbs_and_friends_app.ui.customer_side.notification;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.group4.herbs_and_friends_app.data.communication.NotificationConsumer;
import com.group4.herbs_and_friends_app.data.communication.dtos.NotificationDto;

import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HNotificationVM extends ViewModel {
    private final NotificationConsumer notificationConsumer;

    @Inject
    public HNotificationVM(NotificationConsumer notificationConsumer) {
        this.notificationConsumer = notificationConsumer;
    }

    public LiveData<List<NotificationDto>> getNotificationsLiveData() {
        return notificationConsumer.getNotificationsLiveData();
    }
}