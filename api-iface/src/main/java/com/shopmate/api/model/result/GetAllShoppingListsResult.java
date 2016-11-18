package com.shopmate.api.model.result;

import com.google.common.collect.ImmutableMap;
import com.shopmate.api.model.list.ShoppingList;

public class GetAllShoppingListsResult {
    private final ImmutableMap<Long, ShoppingList> listsById;

    public GetAllShoppingListsResult(ImmutableMap<Long, ShoppingList> listsById) {
        this.listsById = listsById;
    }

    public ImmutableMap<Long, ShoppingList> getLists() {
        return listsById;
    }
}
