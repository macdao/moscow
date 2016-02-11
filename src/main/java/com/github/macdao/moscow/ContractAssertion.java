package com.github.macdao.moscow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.core.io.PathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.PathMatcher;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ContractAssertion {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final List<Contract> contracts;
    private final RestTemplate restTemplate = new TestRestTemplate();
    private final PathMatcher pathMatcher = new AntPathMatcher();
    private final Map<String, String> variables = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private String host = "localhost";
    private int port = 8080;

    public ContractAssertion(List<Contract> contracts) {
        Preconditions.checkArgument(!contracts.isEmpty(), "Given contract list is empty!");
        this.contracts = contracts;
    }

    public ContractAssertion setPort(int port) {
        this.port = port;
        return this;
    }

    public Map<String, String> assertContract() {
        for (Contract contract : contracts) {
            assertContract(contract);
        }
        return variables;
    }

    private void assertContract(Contract contract) {
        final ResponseEntity<String> responseEntity = execute(contract);

        logger.info("Status code: {}", responseEntity.getStatusCode());
        logger.info("Headers: {}", responseEntity.getHeaders());
        logger.info("Body: {}", responseEntity.getBody());

        assertContract(responseEntity, contract.getResponse());
    }

    private void assertContract(ResponseEntity<String> responseEntity, ContractResponse contractResponse) {
        assertStatusCode(responseEntity, contractResponse);
        assertHeaders(responseEntity, contractResponse);
        assertBody(responseEntity, contractResponse);
    }

    private void assertStatusCode(ResponseEntity<?> responseEntity, ContractResponse contractResponse) {
        assertThat(responseEntity.getStatusCode().value(), is(contractResponse.getStatus()));
    }

    private void assertHeaders(ResponseEntity<?> responseEntity, ContractResponse contractResponse) {
        for (Map.Entry<String, String> entry : contractResponse.getHeaders().entrySet()) {
            final String actualHeader = responseEntity.getHeaders().get(entry.getKey()).get(0);
            final String expectedPattern = entry.getValue().replace("{port}", String.valueOf(port));
            assertThat(actualHeader, new StringTypeSafeMatcher(expectedPattern));
        }
    }

    private void assertBody(ResponseEntity<String> responseEntity, ContractResponse contractResponse) {
        final String actualBody = responseEntity.getBody();
        if (contractResponse.getText() != null) {
            assertThat(actualBody, is(contractResponse.getText()));
        } else if (contractResponse.getJson() != null) {
            assertJson(serialize(contractResponse.getJson()), actualBody);
        }
    }

    private void assertJson(String expectedStr, String actualStr) {
        try {
            JSONAssert.assertEquals(expectedStr, actualStr, JSONCompareMode.STRICT);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private String serialize(Object json) {
        try {
            return objectMapper.writeValueAsString(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private ResponseEntity<String> execute(Contract contract) {
        final ContractRequest contractRequest = contract.getRequest();

        final String uri = format("http://%s:%d%s", host, port, decode(contractRequest.getUri()));
        final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri);

        for (Map.Entry<String, String> query : contractRequest.getQueries().entrySet()) {
            builder.queryParam(query.getKey(), query.getValue());
        }

        final ResponseEntity<String> responseEntity = restTemplate.exchange(builder.build().toUri(), contractRequest.getMethod(), new HttpEntity<>(body(contract), headers(contractRequest)), String.class);
        return responseEntity;
    }

    private Object body(Contract contract) {
        final ContractRequest contractRequest = contract.getRequest();

        if (contractRequest.getText() != null) {
            return contractRequest.getText();
        }

        if (contractRequest.getFile() != null) {
            return new PathResource(contract.getBase().resolve(contractRequest.getFile()));
        }

        return contractRequest.getJson();
    }


    private MultiValueMap<String, String> headers(ContractRequest contractRequest) {
        final LinkedMultiValueMap<String, String> headers = new LinkedMultiValueMap<>();
        headers.setAll(contractRequest.getHeaders());
        return headers;
    }

    private String decode(String uri) {
        try {
            return URLDecoder.decode(uri, Charsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private class StringTypeSafeMatcher extends TypeSafeMatcher<String> {

        private final String pattern;

        public StringTypeSafeMatcher(String pattern) {
            this.pattern = pattern;
        }

        @Override
        public void describeTo(Description description) {
            description.appendText(pattern);
        }

        @Override
        protected boolean matchesSafely(String item) {
            final boolean match = pathMatcher.match(pattern, item);
            if (match) {
                variables.putAll(pathMatcher.extractUriTemplateVariables(pattern, item));
            }
            return match;
        }
    }
}
