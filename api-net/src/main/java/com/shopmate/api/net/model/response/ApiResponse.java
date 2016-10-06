package com.shopmate.api.net.model.response;

import com.google.common.base.Optional;

public class ApiResponse<T> {
    private T result;
    private String error;

    public ApiResponse() {
    }

    public Optional<T> getResult() {
        return Optional.fromNullable(result);
    }

    public Optional<String> getError() {
        return Optional.fromNullable(error);
    }
}
