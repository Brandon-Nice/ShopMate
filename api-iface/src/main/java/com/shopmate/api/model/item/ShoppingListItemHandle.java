package com.shopmate.api.model.item;

import com.google.common.base.Optional;

/**
 * Points to a shopping list item and optionally contains data about it.
 */
public class ShoppingListItemHandle {
    private final long id;
    private final Optional<ShoppingListItem> item;

    public ShoppingListItemHandle(long id, Optional<ShoppingListItem> item) {
        this.id = id;
        this.item = item;
    }

    public ShoppingListItemHandle(long id) {
        this(id, Optional.<ShoppingListItem>absent());
    }

    public long getId() {
        return id;
    }

    public Optional<ShoppingListItem> getItem() {
        return item;
    }
}
