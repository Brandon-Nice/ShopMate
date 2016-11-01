package com.shopmate.api.net.model.item;

import com.google.common.base.Optional;
import com.shopmate.api.model.item.ShoppingListItem;
import com.shopmate.api.model.item.ShoppingListItemPriority;

public class ShoppingListItemJson {
    private long listId;
    private String name = "";
    private String description = "";
    private String imageUrl;
    private Integer price;
    private int quantity;
    private int quantityPurchased;
    private int priority;

    public ShoppingListItemJson() {
    }

    public ShoppingListItemJson(long listId, ShoppingListItem item) {
        this.listId = listId;
        this.name = item.getName();
        this.description = item.getDescription();
        this.imageUrl = item.getImageUrl().orNull();
        this.price = item.getMaxPriceCents().orNull();
        this.quantity = item.getQuantity();
        this.quantityPurchased = item.getQuantityPurchased();
        this.priority = priorityToInt(item.getPriority());
    }

    public ShoppingListItem toItem() {
        return new ShoppingListItem(
                name,
                description,
                Optional.fromNullable(imageUrl),
                Optional.fromNullable(price),
                quantity,
                quantityPurchased,
                intToPriority(priority));
    }

    private static ShoppingListItemPriority intToPriority(int priority) {
        switch (priority) {
            case 0:
                return ShoppingListItemPriority.LOW;
            case 1:
                return ShoppingListItemPriority.NORMAL;
            case 2:
                return ShoppingListItemPriority.HIGH;
            default:
                return ShoppingListItemPriority.NORMAL;
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
