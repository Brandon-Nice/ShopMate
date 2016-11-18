package com.shopmate.api.net.model.list;

import com.shopmate.api.model.list.ShoppingListInvite;

public class ShoppingListInviteJson {
    private long id;
    private String title = "";
    private String senderFbid = "";
    private String receiverFbid = "";

    public ShoppingListInvite toInvite() {
        return new ShoppingListInvite(id, title, senderFbid, receiverFbid);
    }
}
