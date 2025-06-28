package com.grantip.backend.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {
    MALE("남자"),
    FEMALE("여자");

    private final String description;

    Gender(String description) {
        this.description = description;
    }
    @JsonValue
    public String getDescription(){
        return description;
    };
}
