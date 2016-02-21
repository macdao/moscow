package com.github.macdao.moscow;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.macdao.moscow.http.RestExecutor;
import com.github.macdao.moscow.http.RestExecutorFactory;
import com.github.macdao.moscow.http.RestResponse;
import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ContractAssertion {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final List<Contract> contracts;
    private final PathMatcher pathMatcher = new AntPathMatcher();
    private final Map<String, String> variables = new HashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private RestExecutor restExecutor = RestExecutorFactory.getRestExecutor();
    private String scheme = "http";
    private String host = "localhost";
    private int port = 8080;
    private boolean necessity = false;
    private int executionTimeout = 0;

    public ContractAssertion(List<Contract> contracts) {
        Preconditions.checkArgument(!contracts.isEmpty(), "Given contract list is empty!");
        this.contracts = contracts;
    }

    public ContractAssertion setRestExecutor(RestExecutor restExecutor) {
        this.restExecutor = restExecutor;
        return this;
    }

    public ContractAssertion setScheme(String scheme) {
        this.scheme = scheme;
        return this;
    }

    public ContractAssertion setPort(int port) {
        this.port = port;
        return this;
    }

    public ContractAssertion setHost(String host) {
        this.host = host;
        return this;
    }

    public ContractAssertion setNecessity(boolean necessity) {
        this.necessity = necessity;
        return this;
    }

    public ContractAssertion setExecutionTimeout(int executionTimeout) {
        this.executionTimeout = executionTimeout;
        return this;
    }

    public Map<String, String> assertContract() {
        for (Contract contract : contracts) {
            assertContract(contract);
        }
        return variables;
    }

    private void assertContract(Contract contract) {
        final RestResponse responseEntity = execute(contract);

        logger.info("Status code: {}", responseEntity.getStatusCode());
        logger.info("Headers: {}", responseEntity.getHeaders());
        logger.info("Body: {}", responseEntity.getBody());

        assertContract(responseEntity, contract.getResponse());
    }

    private void assertContract(RestResponse responseEntity, ContractResponse contractResponse) {
        assertStatusCode(responseEntity, contractResponse);
        assertHeaders(responseEntity, contractResponse);
        assertBody(responseEntity, contractResponse);
    }

    private void assertStatusCode(RestResponse responseEntity, ContractResponse contractResponse) {
        assertThat(responseEntity.getStatusCode(), is(contractResponse.getStatus()));
    }

    private void assertHeaders(RestResponse responseEntity, ContractResponse contractResponse) {
        for (Map.Entry<String, String> entry : contractResponse.getHeaders().entrySet()) {
            final String actualHeader = responseEntity.getHeaders().get(entry.getKey());
            final String expectedPattern = entry.getValue().replace("{port}", String.valueOf(port));
            assertThat(actualHeader, new StringTypeSafeMatcher(expectedPattern));
        }
    }

    private void assertBody(RestResponse responseEntity, ContractResponse contractResponse) {
        final String actualBody = responseEntity.getBody();
        if (contractResponse.getText() != null) {
            assertThat(actualBody, is(contractResponse.getText()));
        } else if (contractResponse.getJson() != null) {
            assertJson(resolve(contractResponse.getJson()), actualBody);
        }
    }

    private String resolve(Object json) {
        String expectedJson = serialize(json);
        for (Map.Entry<String, String> entry : variables.entrySet()) {
            expectedJson = expectedJson.replace(format("{%s}", entry.getKey()), entry.getValue());
        }
        return expectedJson;
    }

    private void assertJson(String expectedStr, String actualStr) {
        final JSONCompareMode mode = necessity ? JSONCompareMode.STRICT_ORDER : JSONCompareMode.STRICT;

        try {
            JSONAssert.assertEquals(expectedStr, actualStr, mode);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String serialize(Object json) {
        try {
            return objectMapper.writeValueAsString(json);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private RestResponse execute(Contract contract) {
        final ContractRequest contractRequest = contract.getRequest();

        final String url = String.format("%s://%s:%d%s%s", scheme, host, port, contractRequest.getUri(), queryString(contractRequest.getQueries()));
        final URI uri = toUri(url);

        final long start = System.currentTimeMillis();

        final RestResponse responseEntity = restExecutor.execute(contractRequest.getMethod(), uri, contractRequest.getHeaders(), body(contract));
        final long spent = System.currentTimeMillis() - start;

        final String description = contract.getDescription();
        logger.info("Contract `{}` spent {}ms", description, spent);

        if (executionTimeout > 0 && spent > executionTimeout) {
            throw new ExecutionTimeoutException(format("Contract `%s` spent %dms", description, spent));
        }

        return responseEntity;
    }

    private String queryString(Map<String, String> queries) {
        if (queries.isEmpty()) {
            return "";
        }

        final List<String> list = new ArrayList<>();
        for (Map.Entry<String, String> query : queries.entrySet()) {
            list.add(query.getKey() + "=" + encode(query.getValue()));
        }

        return "?" + Joiner.on("&").join(list);
    }

    private URI toUri(String url) {
        try {
            return new URI(url);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private Object body(Contract contract) {
        final ContractRequest contractRequest = contract.getRequest();

        if (contractRequest.getText() != null) {
            return contractRequest.getText();
        }

        if (contractRequest.getFile() != null) {
            return contract.getBase().resolve(contractRequest.getFile());
        }

        return contractRequest.getJson();
    }

    private String encode(String value) {
        try {
            return URLEncoder.encode(value, Charsets.UTF_8.name());
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
