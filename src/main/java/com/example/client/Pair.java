package com.example.client;

public class Pair<REQUEST, RESPONSE> {
    private RESPONSE response;
    private REQUEST request;

    public Pair(REQUEST request, RESPONSE response) {
        this.request = request;
        this.response = response;
    }

    public RESPONSE getResponse() {
        return response;
    }

    public REQUEST getRequest() {
        return request;
    }
}
