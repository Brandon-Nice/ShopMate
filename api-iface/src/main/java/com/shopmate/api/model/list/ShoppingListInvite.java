package com.shopmate.api.model.list;

public class ShoppingListInvite {
    private final long id;
    private final String listTitle;
    private final String senderId;
    private final String receiverId;

    public ShoppingListInvite(long id, String listTitle, String senderId, String receiverId) {
        this.id = id;
        this.listTitle = listTitle;
        this.senderId = senderId;
        this.receiverId = receiverId;
    }

    public long getId() {
        return id;
    }

    public String getListTitle() {
        return listTitle;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }
}
