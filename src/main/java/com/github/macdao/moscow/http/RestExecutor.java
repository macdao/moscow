package com.github.macdao.moscow.http;

import java.net.URI;
import java.util.Map;

public interface RestExecutor {
    RestResponse execute(String method, URI uri, Map<String, String> headers, Object body);
}
