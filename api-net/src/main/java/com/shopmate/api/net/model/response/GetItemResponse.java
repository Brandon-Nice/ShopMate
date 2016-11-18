package com.shopmate.api.net.model.response;

import com.shopmate.api.model.item.ShoppingListItem;
import com.shopmate.api.net.model.item.ShoppingListItemJson;

public class GetItemResponse {
    private ShoppingListItemJson item = new ShoppingListItemJson();

    public ShoppingListItem toItem() {
        return item.toItem();
    }
}
