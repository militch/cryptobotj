package com.github.militch.jcryptobot.jsonrpc;

import java.util.Map;

public class HttpRequest {
    private final String method;
    private final String url;
    private final String version;
    private String host;
    private Map<String, String> headers;

    public HttpRequest(String method, String url, String version) {
        this.method = method;
        this.url = url;
        this.version = version;
    }

    public void setHeaders(Map<String, String> header){
        this.headers = header;
    }

    public String getMethod() {
        return method;
    }

    public String getUrl() {
        return url;
    }

    public String getVersion() {
        return version;
    }

    public String getHost() {
        return host;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }
}
