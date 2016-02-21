package com.github.macdao.moscow.json;

import com.github.macdao.moscow.model.Contract;

import java.nio.file.Path;
import java.util.List;

public interface JsonConverter {

    String serialize(Object object);

    List<Contract> deserializeContracts(Path file);
}
