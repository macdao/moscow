package com.github.macdao.moscow.util;

public class ClassUtils {
    public static boolean isPresent(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }
}
