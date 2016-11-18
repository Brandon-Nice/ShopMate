package com.shopmate.api.model.list;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.shopmate.api.model.item.ShoppingListItemHandle;

public class ShoppingList {

    private final String creatorId;
    private final String title;
    private final ImmutableSet<String> memberIds;
    private final ImmutableList<ShoppingListItemHandle> items;

    public ShoppingList(
            String creatorId,
            String title,
            ImmutableSet<String> memberIds,
            ImmutableList<ShoppingListItemHandle> items) {
        this.creatorId = creatorId;
        this.title = title;
        this.memberIds = memberIds;
        this.items = items;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public String getTitle() {
        return title;
    }

    public ImmutableSet<String> getMemberIds() {
        return memberIds;
    }

    public ImmutableList<ShoppingListItemHandle> getItems() {
        return items;
    }
}
