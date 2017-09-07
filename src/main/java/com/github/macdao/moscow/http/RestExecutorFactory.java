package com.github.macdao.moscow.http;

import com.github.macdao.moscow.util.ClassUtils;

public class RestExecutorFactory {

    public static RestExecutor getRestExecutor() {
        if (ClassUtils.isPresent("com.github.macdao.moscow.http.RestTemplateExecutor")) {
            return new RestTemplateExecutor();
        }

        if (ClassUtils.isPresent("okhttp3.OkHttpClient")) {
            return new OkHttpClientExecutor();
        }

        throw new IllegalStateException("Require org.springframework.boot.test.TestRestTemplate or okhttp3.OkHttpClient");
    }
}
