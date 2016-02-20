package com.github.macdao.moscow.http;

public class RestExecutorFactory {
    private static boolean restTemplatePresent = isPresent("com.github.macdao.moscow.http.RestTemplateExecutor");

    public static RestExecutor getRestExecutor() {
        if (restTemplatePresent) {
            return new RestTemplateExecutor();
        }
        return new OkHttpClientExecutor();
    }

    private static boolean isPresent(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }
}
