package com.github.macdao.moscow;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ArrayListMultimap;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;


public class ContractContainer {
    private static final ObjectMapper objectMapper = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    private final ArrayListMultimap<String, Contract> contractMap = ArrayListMultimap.create();

    public ContractContainer(Path... paths) {
        for (Path path : paths) {
            try {
                loadContracts(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public List<Contract> findContracts(String description) {
        return contractMap.get(description);
    }

    private void loadContracts(Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                loadContractsFromFile(file);
                return super.visitFile(file, attrs);
            }
        });
    }

    private void loadContractsFromFile(Path file) throws IOException {
        if (file.getFileName().toString().endsWith(".json")) {
            final List<Contract> contracts = objectMapper.readValue(file.toFile(), new TypeReference<List<Contract>>() {
            });
            for (Contract contract : contracts) {
                final String description = contract.getDescription();
                if (description != null) {
                    contractMap.put(description, contract);
                }
            }
        }
    }
}
