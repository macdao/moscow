package com.github.macdao.moscow;

import com.github.macdao.moscow.json.JsonConverter;
import com.github.macdao.moscow.json.JsonConverterFactory;
import com.github.macdao.moscow.model.Contract;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import java.nio.file.Path;
import java.util.List;

abstract class AbstractContractContainer {
    private final JsonConverter jsonConverter = JsonConverterFactory.getJsonConverter();
    private final ListMultimap<String, Contract> contractMap = ArrayListMultimap.create();

    void loadContractsFromFile(Path base, Path file) {
        final List<Contract> contracts = fromFile(base, file);
        for (Contract contract : contracts) {
            contractMap.put(contract.getDescription(), contract);
        }
    }

    private List<Contract> fromFile(Path base, Path file) {
        final List<Contract> contracts = jsonConverter.deserializeContracts(file);
        for (Contract contract : contracts) {
            contract.setBase(base);
        }
        return contracts;
    }

    public List<Contract> findContracts(String description) {
        return contractMap.get(description);
    }

    ListMultimap<String, Contract> getContractMap() {
        return contractMap;
    }
}
