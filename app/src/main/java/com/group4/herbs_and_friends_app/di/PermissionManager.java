package com.group4.herbs_and_friends_app.di;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;

public class PermissionManager {
    private final Context context;

    public PermissionManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public boolean isPushNotificationAllowed() {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED;
    }
}
