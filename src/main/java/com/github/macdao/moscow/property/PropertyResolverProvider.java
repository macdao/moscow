package com.github.macdao.moscow.property;

import org.springframework.core.env.PropertyResolver;

public class PropertyResolverProvider implements PropertyProvider {
    private final PropertyResolver propertyResolver;

    public PropertyResolverProvider(PropertyResolver propertyResolver) {
        this.propertyResolver = propertyResolver;
    }

    @Override
    public String getProperty(String key) {
        return propertyResolver.getProperty(key);
    }
}

