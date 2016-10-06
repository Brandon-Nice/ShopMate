package com.shopmate.api.model.item;

import com.google.common.base.Optional;

/**
 * Contains information about an item in a shopping list.
 */
public class ShoppingListItem {

    private final String name;
    private final String description;
    private final Optional<String> imageUrl;
    private final Optional<Integer> maxPriceCents;
    private final int quantity;
    private final int quantityPurchased;
    private final ShoppingListItemPriority priority;

    public ShoppingListItem(
            String name,
            String description,
            Optional<String> imageUrl,
            Optional<Integer> maxPriceCents,
            int quantity,
            int quantityPurchased,
            ShoppingListItemPriority priority) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.maxPriceCents = maxPriceCents;
        this.quantity = quantity;
        this.quantityPurchased = quantityPurchased;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Optional<String> getImageUrl() {
        return imageUrl;
    }

    /**
     * @return The item's maximum price in cents (e.g. 100 = $1) if one is set.
     */
    public Optional<Integer> getMaxPriceCents() {
        return maxPriceCents;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getQuantityPurchased() {
        return quantityPurchased;
    }

    public ShoppingListItemPriority getPriority() {
        return priority;
    }
}
