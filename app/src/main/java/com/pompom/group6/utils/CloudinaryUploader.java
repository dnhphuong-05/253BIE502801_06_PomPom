package com.pompom.group6.utils;

import android.net.Uri;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

import java.util.Map;

/** Upload ảnh/video story lên Cloudinary (đã cấu hình signed ở CloudinaryConfig/PomPomApp). */
public final class CloudinaryUploader {
    private CloudinaryUploader() {}

    public interface Callback {
        void onSuccess(String secureUrl);
        void onError(String message);
    }

    /** @param resourceType "image" hoặc "video" */
    public static void upload(Uri fileUri, String resourceType, Callback callback) {
        MediaManager.get().upload(fileUri)
                .option("resource_type", resourceType)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {}

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        Object url = resultData.get("secure_url");
                        if (url != null) {
                            callback.onSuccess(url.toString());
                        } else {
                            callback.onError("Không nhận được URL từ Cloudinary");
                        }
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        callback.onError(error.getDescription());
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                        callback.onError(error.getDescription());
                    }
                })
                .dispatch();
    }
}
