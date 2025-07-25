package com.group4.herbs_and_friends_app.di;

import android.content.Context;

public class ResourceManager {
    private final Context context;

    public ResourceManager(Context context) {
        this.context = context.getApplicationContext();
    }

    public String getString(int resId) {
        return context.getString(resId);
    }
}