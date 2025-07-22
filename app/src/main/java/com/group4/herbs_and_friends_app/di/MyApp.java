package com.group4.herbs_and_friends_app.di;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.ProcessLifecycleOwner;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.group4.herbs_and_friends_app.data.communication.NotificationConsumer;
import com.group4.herbs_and_friends_app.data.communication.NotificationPublisher;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import dagger.hilt.android.HiltAndroidApp;

@HiltAndroidApp
public class MyApp extends Application {
    @Inject
    EagerInitializer eagerInitializer;

    @Override
    public void onCreate() {
        super.onCreate();

        // Reference eagerInitializer to ensure eager initialization
        if (eagerInitializer == null) {
            throw new IllegalStateException("EagerInitializer not injected!");
        }
    }
}
