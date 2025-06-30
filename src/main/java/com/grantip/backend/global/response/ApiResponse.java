package com.grantip.backend.global.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ApiResponse<T> {
    private T result;

    private boolean success;

    private int code;

    private String message;
}

