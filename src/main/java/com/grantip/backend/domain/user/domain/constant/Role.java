package com.grantip.backend.domain.user.domain.constant;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Role {
    USER("사용자"),
    ADMIN("관리자");

    private final String description;

    Role(String description) {
        this.description = description;
    }

    @JsonValue
    public String getDescription() {
        return description;
    }
}
