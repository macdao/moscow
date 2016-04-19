package com.github.macdao.moscow;

import com.github.macdao.moscow.http.RestTemplateExecutor;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class DefaultContractAssertionAdvancedTest {
    private static final ContractContainer contractContainer =
            new ContractContainer(Paths.get("src/test/resources/contracts"));

    @Rule
    public final TestName name = new TestName();


    @Test
    @Ignore("Since we are testing against the moco server, payload wont be correct")
    public void should_return_replaced_contract() throws Exception {
        new DefaultContractAssertion(contractContainer.findContracts(name.getMethodName()))
                .setPort(12306)
                .variable("name", "juntao")
                .variable("email", "juntao.qiu@gmail.com")
                .setRestExecutor(new RestTemplateExecutor())
                .assertContract();
    }

    @Test
    @Ignore("Since we are testing against the moco server, payload wont be correct")
    public void should_return_replaced_contract2() throws Exception {
        Map<String, String> context = new HashMap<>();

        context.put("name", "juntao");
        context.put("host", "localhost");
        context.put("port", "12306");
        context.put("email", "juntao.qiu@gmail.com");

        new DefaultContractAssertion(contractContainer.findContracts(name.getMethodName()))
                .variables(context)
                .setRestExecutor(new RestTemplateExecutor())
                .assertContract();
    }
}
