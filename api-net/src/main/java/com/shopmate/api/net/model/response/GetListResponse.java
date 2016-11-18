package com.shopmate.api.net.model.response;

import com.shopmate.api.model.list.ShoppingList;
import com.shopmate.api.net.model.list.ShoppingListJson;

public class GetListResponse {
    private ShoppingListJson list = new ShoppingListJson();

    public ShoppingList toShoppingList() {
        return list.toShoppingList();
    }
}
