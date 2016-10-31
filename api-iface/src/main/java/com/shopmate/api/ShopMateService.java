package com.shopmate.api;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ListenableFuture;
import com.shopmate.api.model.list.ShoppingList;
import com.shopmate.api.model.result.CreateShoppingListResult;

/**
 * Interface for an object which opens connections to the ShopMate API.
 * Use this to acquire a session object.
 */
public interface ShopMateService {

    /**
     * Asynchronously requests to create a shopping list.
     * @param fbToken The user's Facebook token.
     * @param title The title of the list to create.
     * @return The actual shopping list that was created.
     */
    ListenableFuture<CreateShoppingListResult> createShoppingListAsync(String fbToken, String title);

    /**
     * Asynchronously gets the shopping lists that the user is a member of.
     * @param fbToken The user's Facebook token.
     * @return The shopping lists, keyed by ID.
     */
    ListenableFuture<ImmutableMap<Long, ShoppingList>> getShoppingListsAsync(String fbToken);
}
