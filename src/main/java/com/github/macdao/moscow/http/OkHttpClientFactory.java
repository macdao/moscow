package com.github.macdao.moscow.http;

import okhttp3.OkHttpClient;

import static java.util.concurrent.TimeUnit.MINUTES;

public class OkHttpClientFactory {

    public static OkHttpClient getOkHttpClient() {
        return new OkHttpClient().newBuilder()
                .writeTimeout(30, MINUTES)
                .connectTimeout(30, MINUTES)
                .readTimeout(30, MINUTES)
                .build();
    }
}