package com.github.macdao.moscow;

import java.nio.file.Path;

public class Contract {
    private String description;
    private ContractRequest request = new ContractRequest();
    private ContractResponse response;
    private Path base;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ContractRequest getRequest() {
        return request;
    }

    public void setRequest(ContractRequest request) {
        this.request = request;
    }

    public ContractResponse getResponse() {
        return response;
    }

    public void setResponse(ContractResponse response) {
        this.response = response;
    }

    public void setBase(Path base) {
        this.base = base;
    }

    public Path getBase() {
        return base;
    }
}
