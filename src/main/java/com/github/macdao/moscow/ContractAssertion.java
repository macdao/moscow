package com.github.macdao.moscow;

import com.google.common.base.Charsets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class ContractAssertion {
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final List<Contract> contracts;
    private final RestTemplate restTemplate = new TestRestTemplate();
    private String host = "localhost";
    private int port = 8080;

    public ContractAssertion(List<Contract> contracts) {
        this.contracts = contracts;
    }

    public ContractAssertion setPort(int port) {
        this.port = port;
        return this;
    }

    public void assertContract() {
        for (Contract contract : contracts) {
            assertContract(contract);
        }
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
//        assertHeaders(responseEntity, contractResponse);
        assertBody(responseEntity, contractResponse);
    }

    private void assertStatusCode(ResponseEntity<?> responseEntity, ContractResponse contractResponse) {
        assertThat(responseEntity.getStatusCode().value(), is(contractResponse.getStatus()));
    }

    private void assertBody(ResponseEntity<String> responseEntity, ContractResponse contractResponse) {
        assertThat(responseEntity.getBody(), is(contractResponse.getText()));
    }

    private ResponseEntity<String> execute(Contract contract) {
        final ContractRequest contractRequest = contract.getRequest();

        final String uri = format("http://%s:%d%s", host, port, decode(contractRequest.getUri()));
        final UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(uri);

        final ResponseEntity<String> responseEntity = restTemplate.exchange(builder.build().toUri(), contractRequest.getMethod(), new HttpEntity<Object>(null, null), String.class);
        return responseEntity;
    }

    private String decode(String uri) {
        try {
            return URLDecoder.decode(uri, Charsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
}
