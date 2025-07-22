package com.group4.herbs_and_friends_app.di;

import android.app.Application;

import javax.inject.Inject;

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
