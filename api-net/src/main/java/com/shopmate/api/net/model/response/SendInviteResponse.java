package com.shopmate.api.net.model.response;

import com.shopmate.api.model.result.SendInviteResult;

public class SendInviteResponse {
    private long id;

    public SendInviteResult toResult() {
        return new SendInviteResult(id);
    }
}
