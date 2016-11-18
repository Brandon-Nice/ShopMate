package com.shopmate.api.net.model.request;

public class GetItemRequest extends AuthenticatedRequest {
    public GetItemRequest(String fbToken) {
        super(fbToken);
    }
}
