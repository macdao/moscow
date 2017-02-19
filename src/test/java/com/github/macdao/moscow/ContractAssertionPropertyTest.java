package com.github.macdao.moscow;

import com.github.macdao.moscow.http.OkHttpClientExecutor;
import com.github.macdao.moscow.http.RestExecutor;
import com.github.macdao.moscow.http.RestTemplateExecutor;
import com.github.macdao.moscow.property.MapPropertyProvider;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;

import static com.google.common.collect.ImmutableMap.of;

@RunWith(Parameterized.class)
public class ContractAssertionPropertyTest {
    private static final ContractContainer contractContainer = new ContractContainer(Paths.get("src/test/resources/contracts"));

    @Parameterized.Parameters
    public static Collection<RestExecutor> data() {
        return Arrays.asList(new RestTemplateExecutor(), new OkHttpClientExecutor());
    }

    @Parameterized.Parameter
    public RestExecutor restExecutor;

    @Rule
    public final TestName name = new TestName();

    @Test
    public void get_property_should_response_property() throws Exception {
        new ContractAssertion(contractContainer.findContracts(methodName()))
                .setPort(12306)
                .setRestExecutor(restExecutor)
                .withPropertyProvider(new MapPropertyProvider(of("server.port", "0")))
                .assertContract();
    }


    private String methodName() {
        return name.getMethodName().substring(0, name.getMethodName().length() - 3);
    }
}