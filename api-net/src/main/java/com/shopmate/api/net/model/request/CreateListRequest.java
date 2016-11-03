package com.shopmate.api.net.model.request;

import com.google.common.collect.ImmutableSet;

public class CreateListRequest extends AuthenticatedRequest {
    private final String title;
    private final ImmutableSet<String> inviteFbids;

    public CreateListRequest(String token, String title, ImmutableSet<String> inviteFbids) {
        super(token);
        this.title = title;
        this.inviteFbids = inviteFbids;
    }
}
