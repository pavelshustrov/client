package com.example.client;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ClientConfig {

    @Value("${scheme:http}")
    @Getter
    private String scheme;

    @Value("${serverHost:localhost}")
    @Getter
    private String host;

    @Value("${serverPort:8080}")
    @Getter
    private int port;

    @Value("${api:/airport/{id}}")
    @Getter
    private String api;

}
