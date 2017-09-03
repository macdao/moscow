package com.github.macdao.moscow.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.macdao.moscow.model.Contract;
import com.github.macdao.moscow.model.GlobalSetting;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class ObjectMapperConverter implements JsonConverter {
    private final ObjectMapper objectMapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    @Override
    public String serialize(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Contract> deserializeContracts(Path file) {
        return deserialize(file, new TypeReference<List<Contract>>() {
        });
    }

    @Override
    public List<GlobalSetting> deserializeGlobalSettings(Path file) {
        return deserialize(file, new TypeReference<List<GlobalSetting>>() {
        });
    }

    private <T> T deserialize(Path file, TypeReference valueTypeRef) {
        final File src = file.toFile();
        try {
            return objectMapper.readValue(src, valueTypeRef);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
