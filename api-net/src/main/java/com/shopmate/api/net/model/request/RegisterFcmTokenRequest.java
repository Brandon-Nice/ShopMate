package com.shopmate.api.net.model.request;

public class RegisterFcmTokenRequest extends AuthenticatedRequest {
    private final String key;

    public RegisterFcmTokenRequest(String fbToken, String key) {
        super(fbToken);
        this.key = key;
    }
}
