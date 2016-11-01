package com.shopmate.api.net.model.response;

import com.shopmate.api.model.item.ShoppingListItemHandle;
import com.shopmate.api.model.result.CreateShoppingListItemResult;
import com.shopmate.api.net.model.item.ShoppingListItemHandleJson;

public class CreateItemResponse {
    private ShoppingListItemHandleJson item = new ShoppingListItemHandleJson();

    public CreateShoppingListItemResult toResult() {
        ShoppingListItemHandle handle = item.toHandle();
        return new CreateShoppingListItemResult(handle.getId(), handle.getItem().get());
    }
}
