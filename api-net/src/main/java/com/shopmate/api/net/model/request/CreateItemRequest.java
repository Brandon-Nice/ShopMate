package com.shopmate.api.net.model.request;

import com.shopmate.api.model.item.ShoppingListItem;
import com.shopmate.api.net.model.item.ShoppingListItemJson;

public class CreateItemRequest extends AuthenticatedRequest {
    private final ShoppingListItemJson item;

    public CreateItemRequest(String fbToken, long listId, ShoppingListItem item) {
        super(fbToken);
        this.item = new ShoppingListItemJson(listId, item);
    }
}
