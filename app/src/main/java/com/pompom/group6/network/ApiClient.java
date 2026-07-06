package com.pompom.group6.network;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/** Singleton Retrofit + ApiService dùng chung toàn app. */
public final class ApiClient {
    private ApiClient() {}

    // Backend chạy trên Render free tier: server "ngủ" sau 15 phút không dùng và
    // cần 20-50s để khởi động lại khi có request đầu tiên. Timeout mặc định của
    // OkHttp (10s) không đủ, khiến request đầu tiên báo lỗi dù server đang tỉnh
    // dậy bình thường — nên phải nới rộng timeout ở đây.
    private static final int TIMEOUT_SECONDS = 60;

    private static volatile ApiService service;

    public static ApiService get() {
        if (service == null) {
            synchronized (ApiClient.class) {
                if (service == null) {
                    OkHttpClient httpClient = new OkHttpClient.Builder()
                            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                            .writeTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
                            .build();
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(ApiConfig.BASE_URL)
                            .client(httpClient)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    service = retrofit.create(ApiService.class);
                }
            }
        }
        return service;
    }
}
