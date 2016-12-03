package com.shopmate.api.net.model.item;

import com.google.common.base.Optional;
import com.shopmate.api.model.item.ShoppingListItemPriority;
import com.shopmate.api.model.item.ShoppingListItemUpdate;

public class ShoppingListItemUpdateJson {
    private String name;
    private String description;
    private String imageUrl;
    private Integer price;
    private Integer quantity;
    private Integer quantityPurchasedDelta;
    private Integer priority;

    public ShoppingListItemUpdateJson() {
    }

    public ShoppingListItemUpdateJson(ShoppingListItemUpdate update) {
        this.name = update.getNameUpdate().orNull();
        this.description = update.getDescriptionUpdate().orNull();
        this.imageUrl = update.getImageUrlUpdate().or(Optional.<String>absent()).orNull();
        this.price = update.getMaxPriceCentsUpdate().or(Optional.<Integer>absent()).orNull();;
        this.quantity = update.getQuantityUpdate().orNull();
        if (update.getQuantityPurchasedDelta() != 0) {
            this.quantityPurchasedDelta = update.getQuantityPurchasedDelta();
        }
        if (update.getPriorityUpdate().isPresent()) {
            this.priority = priorityToInt(update.getPriorityUpdate().get());
        }
    }

    private static int priorityToInt(ShoppingListItemPriority priority) {
        switch (priority) {
            case LOW:
                return 0;
            case NORMAL:
                return 1;
            case HIGH:
                return 2;
            default:
                return 1;
        }
    }
}
