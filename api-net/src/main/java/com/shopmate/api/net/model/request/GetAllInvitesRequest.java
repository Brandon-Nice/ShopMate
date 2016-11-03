package com.shopmate.api.net.model.request;

public class GetAllInvitesRequest extends AuthenticatedRequest {
    public GetAllInvitesRequest(String fbToken) {
        super(fbToken);
    }
}
