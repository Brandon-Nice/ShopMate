package com.shopmate.api.net.model.request;

import com.shopmate.api.model.item.ShoppingListItemUpdate;
import com.shopmate.api.net.model.item.ShoppingListItemUpdateJson;

public class UpdateItemRequest extends AuthenticatedRequest {
    private final ShoppingListItemUpdateJson changes;

    public UpdateItemRequest(String fbToken, ShoppingListItemUpdate changes) {
        super(fbToken);
        this.changes = new ShoppingListItemUpdateJson(changes);
    }
}
