package com.khalilt.n26.http;

public class JsonHttpResponse {

    private String status;

    public JsonHttpResponse(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
