package com.github.macdao.moscow;

import org.springframework.http.HttpMethod;

import java.util.HashMap;
import java.util.Map;

public class ContractRequest {
    private String uri = "/";
    private HttpMethod method = HttpMethod.GET;
    private String text;
    private String file;
    private Map<String, String> queries = new HashMap<>();
    private Map<String, String> headers = new HashMap<>();

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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public Map<String, String> getQueries() {
        return queries;
    }

    public void setQueries(Map<String, String> queries) {
        this.queries = queries;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
