package com.shopmate.api.net.model.request;

public class GetAllListsRequest extends AuthenticatedRequest {
    private final boolean withItems;

    public GetAllListsRequest(String token, boolean withItems) {
        super(token);
        this.withItems = withItems;
    }
}
