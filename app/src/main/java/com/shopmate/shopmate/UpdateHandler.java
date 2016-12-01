package com.shopmate.shopmate;

public abstract class UpdateHandler {
    public void onInvited(String listTitle, long inviteId) {
    }

    public void onListShared(long listId) {
    }

    public void onListLeft(long listId) {
    }

    public void onItemAdded(long listId, long itemId) {
    }

    public void onItemUpdated(long listId, long itemId) {
    }
}
