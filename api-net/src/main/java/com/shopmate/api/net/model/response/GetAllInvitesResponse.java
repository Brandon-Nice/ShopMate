package com.shopmate.api.net.model.response;

import com.google.common.collect.ImmutableList;
import com.shopmate.api.model.list.ShoppingListInvite;
import com.shopmate.api.model.result.GetAllInvitesResult;
import com.shopmate.api.net.model.list.ShoppingListInviteJson;

import java.util.ArrayList;
import java.util.List;

public class GetAllInvitesResponse {
    private List<ShoppingListInviteJson> incoming = new ArrayList<>();
    private List<ShoppingListInviteJson> outgoing = new ArrayList<>();

    public GetAllInvitesResult toResult() {
        return new GetAllInvitesResult(convertInvites(incoming), convertInvites(outgoing));
    }

    private static ImmutableList<ShoppingListInvite> convertInvites(List<ShoppingListInviteJson> invites) {
        List<ShoppingListInvite> result = new ArrayList<>();
        for (ShoppingListInviteJson invite : invites) {
            result.add(invite.toInvite());
        }
        return ImmutableList.copyOf(result);
    }
}
