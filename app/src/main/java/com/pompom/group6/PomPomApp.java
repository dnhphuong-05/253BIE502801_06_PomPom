package com.pompom.group6;

import android.app.Application;
import com.pompom.group6.config.CloudinaryConfig;
import com.pompom.group6.database.DatabaseHelper;

public class PomPomApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        
        // Initialize Cloudinary
        CloudinaryConfig.init(this);
        
        // Initialize Database (Copy from assets)
        new DatabaseHelper(this);
    }
}
