package com.shopmate.api.net.model.request;

public class CreateListRequest extends AuthenticatedRequest {
    private final String title;

    public CreateListRequest(String token, String title) {
        super(token);
        this.title = title;
    }
}
