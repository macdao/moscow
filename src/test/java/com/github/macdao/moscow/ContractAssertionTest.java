package com.github.macdao.moscow;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.nio.file.Paths;

public class ContractAssertionTest {
    private final ContractContainer contractContainer = new ContractContainer(Paths.get("src/test/resources/contracts"));

    @Rule
    public final TestName name = new TestName();

    @Test
    public void should_response_foo() throws Exception {
        new ContractAssertion(contractContainer.findContracts(name.getMethodName()))
                .setPort(12306)
                .assertContract();
    }

    @Test
    public void request_foo_should_response_bar() throws Exception {
        new ContractAssertion(contractContainer.findContracts(name.getMethodName()))
                .setPort(12306)
                .assertContract();
    }
}