package com.github.macdao.moscow;

import com.github.macdao.moscow.http.RestExecutor;
import com.github.macdao.moscow.http.RestExecutorFactory;
import com.github.macdao.moscow.http.RestResponse;
import com.github.macdao.moscow.json.JsonConverter;
import com.github.macdao.moscow.json.JsonConverterFactory;
import com.github.macdao.moscow.model.Contract;
import com.github.macdao.moscow.model.ContractRequest;
import com.github.macdao.moscow.model.ContractResponse;
import com.github.macdao.moscow.property.PropertyProvider;
import com.github.macdao.moscow.util.PlaceholderUtils;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.String.format;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ContractAssertion {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final List<Contract> contracts;
    private final Map<String, String> variables = new HashMap<>();
    private final JsonConverter jsonConverter = JsonConverterFactory.getJsonConverter();

    private RestExecutor restExecutor = RestExecutorFactory.getRestExecutor();
    private String scheme = "http";
    private String host = "localhost";
    private int port = 8080;
    private boolean necessity = false;
    private int executionTimeout = 0;
    private PropertyProvider propertyProvider;
    private String globPattern = "\\{([a-z\\-]+)\\}";

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

    public ContractAssertion withPropertyProvider(PropertyProvider propertyProvider) {
        this.propertyProvider = propertyProvider;
        return this;
    }

    public ContractAssertion withGlobPattern(String globPattern) {
        this.globPattern = globPattern;
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

        assertContract(responseEntity, contract);
    }

    private void assertContract(RestResponse responseEntity, Contract contract) {
        final ContractResponse contractResponse = contract.getResponse();
        assertStatusCode(responseEntity, contractResponse);
        assertHeaders(responseEntity, contractResponse);
        assertBody(responseEntity, contract);
    }

    private void assertStatusCode(RestResponse responseEntity, ContractResponse contractResponse) {
        assertThat(responseEntity.getStatusCode(), is(contractResponse.getStatus()));
    }

    private void assertHeaders(RestResponse responseEntity, ContractResponse contractResponse) {
        for (Map.Entry<String, String> entry : contractResponse.getHeaders().entrySet()) {
            final String actualHeader = responseEntity.getHeaders().get(entry.getKey());
            final String expectedPattern = entry.getValue().replace("{port}", String.valueOf(port))
                    .replace("{host}", host);
            assertThat(actualHeader, new StringTypeSafeMatcher(expectedPattern));
        }
    }

    private void assertBody(RestResponse responseEntity, Contract contract) {
        final ContractResponse contractResponse = contract.getResponse();
        final String actualBody = responseEntity.getBody();

        if (contractResponse.getText() != null) {
            assertThat(actualBody, is(contractResponse.getText()));
        } else if (contractResponse.getJson() != null) {
            assertJson(jsonConverter.serialize(contractResponse.getJson()), actualBody);
        } else if (contractResponse.getFile() != null) {
            final String file = contractResponse.getFile();
            final String expectedBody = new String(readAllBytes(contract.getBase().resolve(file)), UTF_8);
            if (file.endsWith(".json")) {
                assertJson(expectedBody, actualBody);
            } else {
                assertThat(actualBody, is(expectedBody));
            }
        }
    }

    private void assertJson(String json, String actualBody) {
        assertJsonEquals(resolve(json), actualBody);
    }

    private byte[] readAllBytes(Path path) {
        try {
            return Files.readAllBytes(path);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String resolve(String expectedJson) {
        String result = expectedJson.replace("{port}", String.valueOf(port))
                .replace("{host}", host);

        final String format = globPattern.replaceAll("\\(.*\\)", "%s")
                .replaceAll("\\\\(.)", "$1");

        for (Map.Entry<String, String> entry : variables.entrySet()) {
            result = result.replace(format(format, entry.getKey()), entry.getValue());
        }

        for (String key : PlaceholderUtils.parse(result)) {
            final String value = propertyProvider.getProperty(key);
            if (value != null) {
                result = result.replace(format("${%s}", key), value);
            }
        }

        return result;
    }

    private void assertJsonEquals(String expectedStr, String actualStr) {
        final JSONCompareMode mode = necessity ? JSONCompareMode.STRICT_ORDER : JSONCompareMode.STRICT;

        try {
            JSONAssert.assertEquals(expectedStr, actualStr, mode);
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
            return URLEncoder.encode(value, UTF_8.name());
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
        protected boolean matchesSafely(String str) {
            return new AntPathStringMatcher(pattern, globPattern).matchStrings(str, variables);
        }
    }
}
