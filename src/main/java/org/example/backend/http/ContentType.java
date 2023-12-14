package org.example.backend.http;

import lombok.Getter;

public enum ContentType {
    HTML("text/html"),
    TEXT("text/plain"),
    JSON("application/json");
    @Getter
    private final String type;

    ContentType(String type) {
        this.type = type;
    }
}
