package com.pompom.group6.config;

import android.content.Context;
import com.cloudinary.android.MediaManager;
import com.pompom.group6.utils.Constants;
import java.util.HashMap;
import java.util.Map;

public class CloudinaryConfig {
    public static void init(Context context) {
        Map<String, String> config = new HashMap<>();
        config.put("cloud_name", Constants.CLOUDINARY_CLOUD_NAME);
        config.put("api_key", Constants.CLOUDINARY_API_KEY);
        config.put("api_secret", Constants.CLOUDINARY_API_SECRET);
        
        try {
            MediaManager.init(context, config);
        } catch (IllegalStateException e) {
            // Already initialized
        }
    }
}
