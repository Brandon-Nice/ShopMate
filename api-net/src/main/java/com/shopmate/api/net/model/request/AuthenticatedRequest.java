package com.shopmate.api.net.model.request;

public class AuthenticatedRequest {
    private final String token;

    public AuthenticatedRequest(String token) {
        this.token = token;
    }
}
