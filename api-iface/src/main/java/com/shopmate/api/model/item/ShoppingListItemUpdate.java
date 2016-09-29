package com.shopmate.api.model.item;

import com.google.common.base.Optional;

public class ShoppingListItemUpdate {

    private final long id;
    private final Optional<String> name;
    private final Optional<String> description;
    private final Optional<Optional<String>> imageUrl;
    private final Optional<Optional<Integer>> maxPriceCents;
    private final Optional<Integer> quantity;
    private final Optional<Integer> quantityPurchased;
    private final Optional<ShoppingListItemPriority> priority;

    public ShoppingListItemUpdate(
            long id,
            Optional<String> name,
            Optional<String> description,
            Optional<Optional<String>> imageUrl,
            Optional<Optional<Integer>> maxPriceCents,
            Optional<Integer> quantity,
            Optional<Integer> quantityPurchased,
            Optional<ShoppingListItemPriority> priority) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.maxPriceCents = maxPriceCents;
        this.quantity = quantity;
        this.quantityPurchased = quantityPurchased;
        this.priority = priority;
    }

    public static ShoppingListItemUpdate fromDifference(ShoppingListItem originalItem, ShoppingListItem newItem) {
        return new ShoppingListItemUpdate(
                originalItem.getId().or(newItem.getId()).get(),
                updateIfNotEqual(originalItem.getName(), newItem.getName()),
                updateIfNotEqual(originalItem.getDescription(), newItem.getDescription()),
                updateIfNotEqual(originalItem.getImageUrl(), newItem.getImageUrl()),
                updateIfNotEqual(originalItem.getMaxPriceCents(), newItem.getMaxPriceCents()),
                updateIfNotEqual(originalItem.getQuantity(), newItem.getQuantity()),
                updateIfNotEqual(originalItem.getQuantityPurchased(), newItem.getQuantityPurchased()),
                updateIfNotEqual(originalItem.getPriority(), newItem.getPriority()));
    }

    public ShoppingListItem applyTo(ShoppingListItem baseItem) {
        return new ShoppingListItem(
                baseItem.getId(),
                name.or(baseItem.getName()),
                description.or(baseItem.getDescription()),
                imageUrl.or(baseItem.getImageUrl()),
                maxPriceCents.or(baseItem.getMaxPriceCents()),
                quantity.or(baseItem.getQuantity()),
                quantityPurchased.or(baseItem.getQuantityPurchased()),
                priority.or(baseItem.getPriority()));
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

    public Optional<Integer> getQuantityPurchased() { return quantityPurchased; }

    public Optional<ShoppingListItemPriority> getPriorityUpdate() {
        return priority;
    }

    private static <T> Optional<T> updateIfNotEqual(T originalValue, T newValue) {
        return originalValue.equals(newValue) ? Optional.<T>absent() : Optional.of(newValue);
    }
}
