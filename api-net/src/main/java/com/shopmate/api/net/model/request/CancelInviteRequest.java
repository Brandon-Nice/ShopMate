package com.shopmate.api.net.model.request;

public class CancelInviteRequest extends AuthenticatedRequest {
    public CancelInviteRequest(String token) {
        super(token);
    }
}
