package com.shopmate.api.model.item;

import com.google.common.base.Optional;

public class ShoppingListItemBuilder {

    private String itemName;
    private String itemDescription;
    private Optional<String> itemImageUrl;
    private Optional<Integer> itemMaxPriceCents;
    private int itemQuantity;
    private int itemQuantityPurchased;
    private ShoppingListItemPriority itemPriority;

    public ShoppingListItemBuilder(String name) {
        itemName = name;
        itemDescription = "";
        itemImageUrl = Optional.absent();
        itemMaxPriceCents = Optional.absent();
        itemQuantity = 1;
        itemQuantityPurchased = 0;
        itemPriority = ShoppingListItemPriority.NORMAL;
    }

    public ShoppingListItemBuilder name(String name) {
        itemName = name;
        return this;
    }

    public ShoppingListItemBuilder description(String description) {
        itemDescription = description;
        return this;
    }

    public ShoppingListItemBuilder imageUrl(Optional<String> url) {
        itemImageUrl = url;
        return this;
    }

    public ShoppingListItemBuilder maxPriceCents(Optional<Integer> maxPriceCents) {
        itemMaxPriceCents = maxPriceCents;
        return this;
    }

    public ShoppingListItemBuilder quantity(int quantity) {
        itemQuantity = quantity;
        return this;
    }

    public ShoppingListItemBuilder quantityPurchased(int quantityPurchased) {
        itemQuantityPurchased = quantityPurchased;
        return this;
    }

    public ShoppingListItemBuilder priority(ShoppingListItemPriority priority) {
        itemPriority = priority;
        return this;
    }

    public ShoppingListItem build() {
        return new ShoppingListItem(
                itemName,
                itemDescription,
                itemImageUrl,
                itemMaxPriceCents,
                itemQuantity,
                itemQuantityPurchased,
                itemPriority);
    }
}
