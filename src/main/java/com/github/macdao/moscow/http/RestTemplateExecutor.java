package com.github.macdao.moscow.http;

import org.springframework.boot.test.TestRestTemplate;
import org.springframework.core.io.PathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.nio.file.Path;
import java.util.Map;

public class RestTemplateExecutor implements RestExecutor {
    private final RestTemplate restTemplate = new TestRestTemplate();

    public RestResponse execute(String method, URI uri, Map<String, String> headers, Object body) {
        final HttpMethod httpMethod = HttpMethod.valueOf(method);
        final ResponseEntity<String> responseEntity = restTemplate.exchange(uri, httpMethod, new HttpEntity<>(body(body), headers(headers)), String.class);
        return new RestResponse(responseEntity.getStatusCode().value(), responseEntity.getHeaders().toSingleValueMap(), responseEntity.getBody());
    }

    private Object body(Object body) {
        if (body instanceof Path) {
            return new PathResource((Path) body);
        }
        return body;
    }

    private MultiValueMap<String, String> headers(Map<String, String> headers) {
        final LinkedMultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
        multiValueMap.setAll(headers);
        return multiValueMap;
    }
}
