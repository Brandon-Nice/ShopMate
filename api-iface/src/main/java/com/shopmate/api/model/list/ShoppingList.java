package com.shopmate.api.model.list;

import com.google.common.base.Optional;
import com.google.common.collect.ImmutableSet;

public class ShoppingList {

    private final Optional<Long> id;
    private final String creatorId;
    private final String title;
    private final ImmutableSet<String> memberIds;
    private final ImmutableSet<Long> itemIds;

    public ShoppingList(
            Optional<Long> id,
            String creatorId,
            String title,
            ImmutableSet<String> memberIds,
            ImmutableSet<Long> itemIds) {
        this.id = id;
        this.creatorId = creatorId;
        this.title = title;
        this.memberIds = memberIds;
        this.itemIds = itemIds;
    }

    public Optional<Long> getId() {
        return this.id;
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
