package com.github.macdao.moscow;

import org.junit.Test;

import java.nio.file.Paths;

public class ContractAssertionTest {
    private final ContractContainer contractContainer = new ContractContainer(Paths.get("src/test/resources/contracts"));

    @Test
    public void should_response_foo() throws Exception {
        new ContractAssertion(contractContainer.findContracts("should_response_foo"))
                .setPort(12306)
                .assertContract();
    }
}