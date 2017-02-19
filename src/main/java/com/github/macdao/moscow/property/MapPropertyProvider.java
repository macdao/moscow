package com.github.macdao.moscow.property;

import java.util.Map;

public class MapPropertyProvider implements PropertyProvider {
    private final Map<String, String> map;

    public MapPropertyProvider(Map<String, String> map) {
        this.map = map;
    }

    @Override
    public String getProperty(String key) {
        return map.get(key);
    }
}
