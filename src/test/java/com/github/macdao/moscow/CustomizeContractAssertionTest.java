package com.github.macdao.moscow;

import com.github.macdao.moscow.http.RestResponse;
import com.github.macdao.moscow.http.RestTemplateExecutor;
import com.github.macdao.moscow.json.JsonConverter;
import com.github.macdao.moscow.json.JsonConverterFactory;
import com.github.macdao.moscow.model.Contract;
import com.github.macdao.moscow.model.ContractResponse;
import com.jayway.jsonpath.JsonPath;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

import java.nio.file.Paths;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class CustomizeContractAssertionTest {
    private static final ContractContainer contractContainer =
            new ContractContainer(Paths.get("src/test/resources/contracts"));

    @Rule
    public final TestName name = new TestName();

    @Test
    public void should_return_complicated_json_payload() throws Exception {
        new MyContractAssertion(contractContainer.findContracts(name.getMethodName()))
                .setPort(12306)
                .setRestExecutor(new RestTemplateExecutor())
                .assertContract();
    }

}

class MyContractAssertion extends DefaultContractAssertion {

    public MyContractAssertion(List<Contract> contracts) {
        super(contracts);
    }

    @Override
    public void assertBody(RestResponse responseEntity, Contract contract) {
        String body = responseEntity.getBody();

        ContractResponse contractResponse = contract.getResponse();
        JsonConverter jsonConverter = JsonConverterFactory.getJsonConverter();

        List<String> items = JsonPath.read(body, "$.items");
        assert jsonConverter != null;

        String serializedContract = jsonConverter.serialize(contractResponse.getJson());
        List<String> items2 = JsonPath.read(serializedContract, "$.items");

        assertThat(items, is(items2));
    }
}
