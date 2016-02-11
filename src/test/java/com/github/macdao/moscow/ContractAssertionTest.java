package com.github.macdao.moscow;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.nio.file.Paths;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ContractAssertionTest {
    private static final ContractContainer contractContainer = new ContractContainer(Paths.get("src/test/resources/contracts"));

    @Rule
    public final TestName name = new TestName();

    @Test
    public void should_response_text_foo() throws Exception {
        new ContractAssertion(contractContainer.findContracts(name.getMethodName()))
                .setPort(12306)
                .assertContract();
    }

    @Test
    public void request_text_foo_should_response_text_bar() throws Exception {
        new ContractAssertion(contractContainer.findContracts(name.getMethodName()))
                .setPort(12306)
                .assertContract();
    }

    @Test(expected = RuntimeException.class)
    public void bad_contract_name() throws Exception {
        new ContractAssertion(contractContainer.findContracts(name.getMethodName()))
                .setPort(12306)
                .assertContract();
    }

    @Test
    public void request_file_should_response_text_bar2() throws Exception {
        new ContractAssertion(contractContainer.findContracts(name.getMethodName()))
                .setPort(12306)
                .assertContract();
    }

    @Test
    public void request_uri_foo_should_response_text_bar3() throws Exception {
        new ContractAssertion(contractContainer.findContracts(name.getMethodName()))
                .setPort(12306)
                .assertContract();
    }

    @Test
    public void request_param_should_response_text_bar4() throws Exception {
        new ContractAssertion(contractContainer.findContracts(name.getMethodName()))
                .setPort(12306)
                .assertContract();
    }

    @Test
    public void request_put_foo2_should_response_text_bar3() throws Exception {
        new ContractAssertion(contractContainer.findContracts(name.getMethodName()))
                .setPort(12306)
                .assertContract();
    }

    @Test
    public void request_json_should_response_text_bar4() throws Exception {
        new ContractAssertion(contractContainer.findContracts(name.getMethodName()))
                .setPort(12306)
                .assertContract();
    }

    @Test
    public void request_json_should_response_text_bar() throws Exception {
        new ContractAssertion(contractContainer.findContracts(name.getMethodName()))
                .setPort(12306)
                .assertContract();
    }

    @Test
    public void request_text_bar_should_response_201() throws Exception {
        final String result = new ContractAssertion(contractContainer.findContracts(name.getMethodName()))
                .setPort(12306)
                .assertContract()
                .get("bar-id");

        assertThat(result, is("bar-id-1"));
    }

    @Test
    public void request_text_bar2_should_response_headers() throws Exception {
        new ContractAssertion(contractContainer.findContracts(name.getMethodName()))
                .setPort(12306)
                .assertContract();
    }

    @Test
    public void request_text_bar3_should_response_json() throws Exception {
        new ContractAssertion(contractContainer.findContracts(name.getMethodName()))
                .setHost("127.0.0.1")
                .setPort(12306)
                .assertContract();
    }

    @Test
    public void request_text_bar4_should_response_foo() throws Exception {
        new ContractAssertion(contractContainer.findContracts(name.getMethodName()))
                .setPort(12306)
                .setNecessity(true)
                .assertContract();
    }

    @Test(expected = RuntimeException.class)
    public void request_text_bar5_should_response_timeout() throws Exception {
        new ContractAssertion(contractContainer.findContracts(name.getMethodName()))
                .setPort(12306)
                .setExecutionTimeout(100)
                .assertContract();
    }
}