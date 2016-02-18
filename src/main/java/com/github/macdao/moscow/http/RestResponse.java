package com.github.macdao.moscow.http;

import java.util.Map;

public class RestResponse {

    private final int statusCode;
    private final Map<String, String> headers;
    private final String body;

    public RestResponse(int statusCode, Map<String, String> headers, String body) {
        this.statusCode = statusCode;
        this.headers = headers;
        this.body = body;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getBody() {
        return body;
    }
}
