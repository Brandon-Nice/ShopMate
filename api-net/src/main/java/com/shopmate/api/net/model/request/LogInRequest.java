package com.shopmate.api.net.model.request;

public class LogInRequest extends AuthenticatedRequest {
    public LogInRequest(String token) {
        super(token);
    }
}
