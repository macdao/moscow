package com.github.macdao.moscow.json;

import com.github.macdao.moscow.util.ClassUtils;

public class JsonConverterFactory {
    public static JsonConverter getJsonConverter() {
        if (ClassUtils.isPresent("com.fasterxml.jackson.databind.ObjectMapper")) {
            return new ObjectMapperConverter();
        }
        return null;
    }
}
