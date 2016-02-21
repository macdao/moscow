package com.github.macdao.moscow;

import com.github.macdao.moscow.json.JsonConverter;
import com.github.macdao.moscow.json.JsonConverterFactory;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;


public class ContractContainer {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final JsonConverter jsonConverter = JsonConverterFactory.getJsonConverter();
    private final ListMultimap<String, Contract> contractMap = ArrayListMultimap.create();

    public ContractContainer(Path... paths) {
        for (Path path : paths) {
            try {
                loadContracts(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        logger.info("Contracts loaded: {}", contractMap);
    }

    public List<Contract> findContracts(String description) {
        return contractMap.get(description);
    }

    private void loadContracts(final Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                loadContractsFromFile(file, path);
                return super.visitFile(file, attrs);
            }
        });
    }

    private void loadContractsFromFile(Path file, Path base) throws IOException {
        if (file.getFileName().toString().endsWith(".json")) {
            final List<Contract> contracts = jsonConverter.deserializeContracts(file);
            for (Contract contract : contracts) {
                final String description = contract.getDescription();
                if (description != null) {
                    contract.setBase(base);
                    contractMap.put(description, contract);
                }
            }
        }
    }
}
