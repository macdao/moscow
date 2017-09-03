package com.github.macdao.moscow;

import com.github.macdao.moscow.model.Contract;
import org.junit.Test;

import java.nio.file.Paths;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class GlobalSettingsContainerTest {
    private static final GlobalSettingsContainer contractContainer = new GlobalSettingsContainer(
            Paths.get("src/test/resources/contracts"),
            Paths.get("global-settings.json")
    );

    @Test
    public void should_find_correct_contracts() throws Exception {
        final String contractName = "should_response_text_foo";

        final List<Contract> contracts = contractContainer.findContracts(contractName);
        assertThat(contracts).hasSize(1);
        final Contract contract = contracts.get(0);
        assertThat(contract.getDescription()).isEqualTo(contractName);
        assertThat(contract.getResponse().getText()).isEqualTo("foo");
    }
}
