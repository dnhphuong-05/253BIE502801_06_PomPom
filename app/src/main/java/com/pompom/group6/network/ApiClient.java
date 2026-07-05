package com.pompom.group6.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/** Singleton Retrofit + ApiService dùng chung toàn app. */
public final class ApiClient {
    private ApiClient() {}

    private static volatile ApiService service;

    public static ApiService get() {
        if (service == null) {
            synchronized (ApiClient.class) {
                if (service == null) {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(ApiConfig.BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    service = retrofit.create(ApiService.class);
                }
            }
        }
        return service;
    }
}
