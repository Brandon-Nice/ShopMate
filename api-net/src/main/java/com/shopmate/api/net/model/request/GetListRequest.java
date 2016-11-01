package com.shopmate.api.net.model.request;

public class GetListRequest extends AuthenticatedRequest {
    public GetListRequest(String fbToken) {
        super(fbToken);
    }
}
