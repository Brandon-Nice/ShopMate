package com.shopmate.api.net.model.request;

public class SendInviteRequest extends AuthenticatedRequest {
    private final long listId;
    private final String receiverFbid;

    public SendInviteRequest(String token, long listId, String receiverFbid) {
        super(token);
        this.listId = listId;
        this.receiverFbid = receiverFbid;
    }
}
