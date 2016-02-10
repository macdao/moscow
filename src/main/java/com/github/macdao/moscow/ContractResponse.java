package com.github.macdao.moscow;

import org.springframework.http.HttpStatus;

public class ContractResponse {
    private int status = HttpStatus.OK.value();
    private String text;

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
}
