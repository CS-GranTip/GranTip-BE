package com.grantip.backend.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UnivYear {
    FRESHMAN("신입생"),
    FIRST("1학년"),
    SECOND("2학년"),
    THIRD("3학년"),
    FOURTH("4학년"),
    OVER("초과학기");

    private final String description;

    UnivYear(String description) {
        this.description = description;
    }
    @JsonValue
    public String getDescription() {
        return description;
    }
}
