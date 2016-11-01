package com.shopmate.api.net.model.item;

import com.google.common.base.Optional;
import com.shopmate.api.model.item.ShoppingListItem;
import com.shopmate.api.model.item.ShoppingListItemHandle;

public class ShoppingListItemHandleJson {
    private long id;
    private ShoppingListItemJson item;

    public ShoppingListItemHandleJson() {
    }

    public ShoppingListItemHandle toHandle() {
        return new ShoppingListItemHandle(
                id,
                item != null
                        ? Optional.of(item.toItem())
                        : Optional.<ShoppingListItem>absent());
    }
}
