package com.shopmate.api.model.list;

import com.google.common.collect.ImmutableSet;

public class ShoppingList {

    private final String creatorId;
    private final String title;
    private final ImmutableSet<String> memberIds;
    private final ImmutableSet<Long> itemIds;

    public ShoppingList(
            String creatorId,
            String title,
            ImmutableSet<String> memberIds,
            ImmutableSet<Long> itemIds) {
        this.creatorId = creatorId;
        this.title = title;
        this.memberIds = memberIds;
        this.itemIds = itemIds;
    }

    public String getCreatorId() {
        return this.creatorId;
    }

    public String getTitle() {
        return this.title;
    }

    public ImmutableSet<String> getMemberIds() {
        return memberIds;
    }

    public ImmutableSet<Long> getItemIds() {
        return itemIds;
    }
}
