package com.group4.herbs_and_friends_app.di;

import android.util.Log;

import com.group4.herbs_and_friends_app.data.communication.NotificationConsumer;
import com.group4.herbs_and_friends_app.data.communication.NotificationPublisher;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class EagerInitializer {
    @Inject
    public EagerInitializer(NotificationConsumer notificationConsumer, NotificationPublisher notificationPublisher) {
        Log.d("EagerInitializer", "EagerInitializer initialized: " + notificationConsumer + ", " + notificationPublisher);
    }
}