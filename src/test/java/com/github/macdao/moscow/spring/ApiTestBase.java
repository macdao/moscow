package com.github.macdao.moscow.spring;

import com.github.macdao.moscow.ContractAssertion;
import com.github.macdao.moscow.ContractContainer;
import com.github.macdao.moscow.property.PropertyResolverProvider;
import org.junit.Rule;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.nio.file.Paths;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest("server.port:0")
public abstract class ApiTestBase {
    private static final ContractContainer container = new ContractContainer(Paths.get("src/test/resources/contracts"));
    @Rule
    public final TestName name = new TestName();
    @Value("${local.server.port}")
    protected int port;
    @Autowired
    private Environment environment;

    protected Map<String, String> assertContract() {
        return assertContract(name.getMethodName());
    }

    protected Map<String, String> assertContract(String description) {
        return new ContractAssertion(container.findContracts(description))
                .setPort(port)
                .setExecutionTimeout(200)
                .withPropertyProvider(new PropertyResolverProvider(environment))
                .assertContract();
    }
}
