package com.shopmate.api;

import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.ListenableFuture;
import com.shopmate.api.model.list.ShoppingList;

/**
 * Performs ShopMate API commands within a user session context.
 */
public interface ShopMateSession {

    /**
     * @return The logged in user's FBID.
     */
    String getUserId();

    /**
     * Asynchronously creates a shopping list.
     * @param list The requested parameters for the list to create.
     * @return The actual list that was created.
     */
    ListenableFuture<ShoppingList> createShoppingListAsync(ShoppingList list);

    /**
     * Asynchronously gets the shopping lists that the user is a member of.
     */
    ListenableFuture<ImmutableSet<ShoppingList>> getShoppingListsAsync();
}
