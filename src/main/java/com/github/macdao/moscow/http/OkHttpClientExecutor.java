package com.github.macdao.moscow.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import okhttp3.internal.http.HttpMethod;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OkHttpClientExecutor implements RestExecutor {
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public RestResponse execute(String method, URI uri, Map<String, String> headers, Object body) {
        final Request request = new Request.Builder()
                .url(uri.toString())
                .method(method, body(body, method))
                .headers(Headers.of(headers))
                .build();

        final Response response = execute(request);

        return new RestResponse(response.code(), responseHeaders(response), responseBody(response));
    }

    private Map<String, String> responseHeaders(Response response) {
        final Map<String, String> headers = new HashMap<>();
        for (Map.Entry<String, List<String>> entry : response.headers().toMultimap().entrySet()) {
            headers.put(entry.getKey(), entry.getValue().get(0));
        }
        return headers;
    }

    private RequestBody body(Object body, String method) {
        if (body == null) {
            if (HttpMethod.requiresRequestBody(method)) {
                return RequestBody.create(null, "");
            }
            return null;
        }

        if (body instanceof String) {
            return RequestBody.create(MediaType.parse("text/plain;charset=utf-8"), (String) body);
        }

        if (body instanceof Path) {
            return RequestBody.create(null, ((Path) body).toFile());
        }

        return RequestBody.create(MediaType.parse("application/json;charset=utf-8"), serialize(body));
    }

    private String serialize(Object body) {
        if (body == null) {
            return "";
        }
        try {
            return objectMapper.writeValueAsString(body);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Response execute(Request request) {
        try {
            return client.newCall(request).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String responseBody(Response response) {
        try {
            return response.body().string();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
