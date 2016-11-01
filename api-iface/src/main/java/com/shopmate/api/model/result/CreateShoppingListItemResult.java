package com.shopmate.api.model.result;

import com.shopmate.api.model.item.ShoppingListItem;

public class CreateShoppingListItemResult {
    private final long id;
    private final ShoppingListItem item;

    public CreateShoppingListItemResult(long id, ShoppingListItem item) {
        this.id = id;
        this.item = item;
    }

    public long getId() {
        return id;
    }

    public ShoppingListItem getItem() {
        return item;
    }
}
