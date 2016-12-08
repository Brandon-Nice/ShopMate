package com.shopmate.shopmate;

public abstract class UpdateHandler {
    public void onInvited(long inviteId, String listTitle, String senderId) {
    }

    public void onListShared(long listId) {
    }

    public void onListDeleted(long listId) {
    }

    public void onListMemberLeft(long listId, String userId) {
    }

    public void onItemAdded(long listId, long itemId) {
    }

    public void onItemUpdated(long itemId) {
    }

    public void onItemDeleted(long itemId) {
    }

    public void onReimbursementRequested(long purchaseId) {
    }

    public void onPurchaseCompleted(long purchaseId) {
    }
}
