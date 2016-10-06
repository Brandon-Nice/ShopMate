package com.shopmate.api.model.result;

import com.shopmate.api.model.list.ShoppingList;

public class CreateShoppingListResult {

    private final long id;
    private final ShoppingList list;

    public CreateShoppingListResult(long id, ShoppingList list) {
        this.id = id;
        this.list = list;
    }

    public long getId() {
        return id;
    }

    public ShoppingList getList() {
        return list;
    }
}
