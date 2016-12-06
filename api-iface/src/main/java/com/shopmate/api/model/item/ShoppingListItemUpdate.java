package com.shopmate.api.model.item;

import com.google.common.base.Optional;

public class ShoppingListItemUpdate {

    private final Optional<String> name;
    private final Optional<String> description;
    private final Optional<Optional<String>> imageUrl;
    private final Optional<Optional<Integer>> maxPriceCents;
    private final Optional<Integer> quantity;
    private final int quantityPurchasedDelta;
    private final Optional<ShoppingListItemPriority> priority;

    public ShoppingListItemUpdate(
            Optional<String> name,
            Optional<String> description,
            Optional<Optional<String>> imageUrl,
            Optional<Optional<Integer>> maxPriceCents,
            Optional<Integer> quantity,
            int quantityPurchasedDelta,
            Optional<ShoppingListItemPriority> priority) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.maxPriceCents = maxPriceCents;
        this.quantity = quantity;
        this.quantityPurchasedDelta = quantityPurchasedDelta;
        this.priority = priority;
    }

    public static ShoppingListItemUpdate fromDifference(ShoppingListItem oldItem, ShoppingListItem newItem) {
        return new ShoppingListItemUpdate(
                updateIfNotEqual(oldItem.getName(), newItem.getName()),
                updateIfNotEqual(oldItem.getDescription(), newItem.getDescription()),
                updateIfNotEqual(oldItem.getImageUrl(), newItem.getImageUrl()),
                updateIfNotEqual(oldItem.getMaxPriceCents(), newItem.getMaxPriceCents()),
                updateIfNotEqual(oldItem.getQuantity(), newItem.getQuantity()),
                newItem.getQuantityPurchased() - oldItem.getQuantityPurchased(),
                updateIfNotEqual(oldItem.getPriority(), newItem.getPriority()));
    }

    public Optional<String> getNameUpdate() {
        return name;
    }

    public Optional<String> getDescriptionUpdate() {
        return description;
    }

    public Optional<Optional<String>> getImageUrlUpdate() {
        return imageUrl;
    }

    public Optional<Optional<Integer>> getMaxPriceCentsUpdate() {
        return maxPriceCents;
    }

    public Optional<Integer> getQuantityUpdate() {
        return quantity;
    }

    public int getQuantityPurchasedDelta() { return quantityPurchasedDelta; }

    public Optional<ShoppingListItemPriority> getPriorityUpdate() {
        return priority;
    }

    private static <T> Optional<T> updateIfNotEqual(T originalValue, T newValue) {
        return originalValue.equals(newValue) ? Optional.<T>absent() : Optional.of(newValue);
    }
}
