package com.grantip.backend.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum UnivYear {
    FIRST_SEMESTER("대학1학기"),
    SECOND_SEMESTER("대학2학기"),
    THIRD_SEMESTER("대학3학기"),
    FOURTH_SEMESTER("대학4학기"),
    FIFTH_SEMESTER("대학5학기"),
    SIXTH_SEMESTER("대학6학기"),
    SEVENTH_SEMESTER("대학7학기"),
    EIGHTH_SEMESTER_OR_ABOVE("대학8학기 이상");

    // SEMESTER_1, SEM_1 이런식으로 축약 가능

    private final String description;

    UnivYear(String description) {
        this.description = description;
    }
    @JsonValue
    public String getDescription() {
        return description;
    }
}
