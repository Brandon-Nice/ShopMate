package com.shopmate.api.model.result;

import com.google.common.collect.ImmutableList;
import com.shopmate.api.model.list.ShoppingListInvite;

public class GetAllInvitesResult {
    private final ImmutableList<ShoppingListInvite> incomingInvites;
    private final ImmutableList<ShoppingListInvite> outgoingInvites;

    public GetAllInvitesResult(
            ImmutableList<ShoppingListInvite> incomingInvites,
            ImmutableList<ShoppingListInvite> outgoingInvites) {
        this.incomingInvites = incomingInvites;
        this.outgoingInvites = outgoingInvites;
    }

    /**
     * @return A list of invites that were sent to the user.
     */
    public ImmutableList<ShoppingListInvite> getIncomingInvites() {
        return incomingInvites;
    }

    /**
     * @return A list of invites that were sent by the user.
     */
    public ImmutableList<ShoppingListInvite> getOutgoingInvites() {
        return outgoingInvites;
    }
}
