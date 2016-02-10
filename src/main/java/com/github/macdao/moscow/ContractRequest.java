package com.github.macdao.moscow;

import org.springframework.http.HttpMethod;

public class ContractRequest {
    private String uri = "/";
    private HttpMethod method = HttpMethod.GET;

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public void setMethod(HttpMethod method) {
        this.method = method;
    }
}
