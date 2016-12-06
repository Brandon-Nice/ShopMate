package com.shopmate.api.net.model.request;

public class KickUserRequest extends AuthenticatedRequest {
    private final String kickFbid;

    public KickUserRequest(String fbToken, String kickFbid) {
        super(fbToken);
        this.kickFbid = kickFbid;
    }
}
