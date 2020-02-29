package com.example.client.model;

import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.NotBlank;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class Request {
    @NonNull
    @NotBlank
    private String id;
    @NonNull
    private LocalTime created;
    @NonNull
    @NotBlank
    private String threadName;
    @NonNull
    @NotBlank
    private String entityID;

    private Request() {
        this.id = UUID.randomUUID().toString();
        this.created = LocalTime.now();
        this.threadName = Thread.currentThread().getName();
    }

    public Request(@NonNull @NotBlank String entityID) {
        this();
        this.entityID = entityID;
    }
}
