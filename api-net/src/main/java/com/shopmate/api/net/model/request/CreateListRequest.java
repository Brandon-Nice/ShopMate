package com.shopmate.api.net.model.request;

import com.google.common.collect.ImmutableList;

public class CreateListRequest extends AuthenticatedRequest {
    private final String title;
    private final ImmutableList<String> inviteFbids;

    public CreateListRequest(String token, String title, ImmutableList<String> inviteFbids) {
        super(token);
        this.title = title;
        this.inviteFbids = inviteFbids;
    }
}
