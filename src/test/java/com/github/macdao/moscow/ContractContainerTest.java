package com.github.macdao.moscow;

import org.junit.Test;

import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ContractContainerTest {
    @Test
    public void should_find_correct_contracts() throws Exception {
        final ContractContainer contractContainer = new ContractContainer(Paths.get("src/test/resources/contracts"));

        final List<Contract> contracts = contractContainer.findContracts("should_response_foo");
        assertThat(contracts.size(), is(1));
        final Contract contract = contracts.get(0);
        assertThat(contract.getDescription(), is("should_response_foo"));
        assertThat(contract.getResponse().getText(), is("foo"));
    }
}