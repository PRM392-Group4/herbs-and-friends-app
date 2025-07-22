package com.group4.herbs_and_friends_app.di;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.group4.herbs_and_friends_app.data.communication.FCMClient;
import com.group4.herbs_and_friends_app.data.communication.NotificationConsumer;
import com.group4.herbs_and_friends_app.data.communication.NotificationPublisher;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class EagerInitializer {
    @Inject
    public EagerInitializer(NotificationConsumer notificationConsumer, NotificationPublisher notificationPublisher) {
        Log.d("EagerInitializer",
                "EagerInitializer initialized: "
                        + notificationConsumer + ", "
                        + notificationPublisher);

        // add consumer to monitor when app goes foreground/background
        ProcessLifecycleOwner.get().getLifecycle().addObserver(notificationConsumer);
    }
}