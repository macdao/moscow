package com.github.macdao.moscow.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class GlobalSetting {
    private String include;
    private String context;
    @JsonProperty("file_root")
    private String fileRoot;

    public String getInclude() {
        return include;
    }

    public void setInclude(String include) {
        this.include = include;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getFileRoot() {
        return fileRoot;
    }

    public void setFileRoot(String fileRoot) {
        this.fileRoot = fileRoot;
    }
}
