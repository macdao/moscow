package com.github.macdao.moscow;

import com.google.common.collect.ImmutableList;

import java.nio.file.Path;
import java.util.List;


public class ContractContainer {
    public ContractContainer(Path... paths) {
    }

    public List<Contract> findContracts(String description) {
        final Contract contract = new Contract();
        final ContractResponse response = new ContractResponse();
        response.setText("foo");
        contract.setResponse(response);
        return ImmutableList.of(contract);
    }
}
