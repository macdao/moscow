package com.github.macdao.moscow.model;

import java.util.HashMap;
import java.util.Map;

public class ContractResponse {
    private int status = 200;
    private String text;
    private Map<String, String> headers = new HashMap<>();
    private Object json;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public Object getJson() {
        return json;
    }

    public void setJson(Object json) {
        this.json = json;
    }
}
