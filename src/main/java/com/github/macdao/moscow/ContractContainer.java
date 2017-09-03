package com.github.macdao.moscow;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;


public class ContractContainer extends AbstractContractContainer {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public ContractContainer(Path... paths) {
        for (Path path : paths) {
            try {
                loadContracts(path);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        logger.info("Contracts loaded: {}", getContractMap());
    }

    private void loadContracts(final Path path) throws IOException {
        Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.getFileName().toString().endsWith(".json")) {
                    loadContractsFromFile(path, file);
                }
                return super.visitFile(file, attrs);
            }
        });
    }
}
