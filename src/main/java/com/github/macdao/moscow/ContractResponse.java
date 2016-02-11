package com.github.macdao.moscow;

import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

public class ContractResponse {
    private int status = HttpStatus.OK.value();
    private String text;
    private Map<String, String> headers = new HashMap<>();

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }
}
