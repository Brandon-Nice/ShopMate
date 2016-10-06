package com.shopmate.api;

import com.google.common.collect.ImmutableMap;
import com.google.common.util.concurrent.ListenableFuture;
import com.shopmate.api.model.list.ShoppingList;
import com.shopmate.api.model.result.CreateShoppingListResult;
import com.shopmate.api.model.result.LogInResult;

/**
 * Interface for an object which opens connections to the ShopMate API.
 * Use this to acquire a session object.
 */
public interface ShopMateService {

    /**
     * Asynchronously logs into the ShopMate API.
     * @param fbToken The Facebook access token to log in with.
     */
    ListenableFuture<LogInResult> logInAsync(String fbToken);

    /**
     * Asynchronously requests to create a shopping list.
     * @param session The session to create the list under.
     * @param title The title of the list to create.
     * @return The actual shopping list that was created.
     */
    ListenableFuture<CreateShoppingListResult> createShoppingListAsync(ShopMateSession session, String title);

    /**
     * Asynchronously gets the shopping lists that the user is a member of.
     * @param session The session to create the list under.
     * @return The shopping lists, keyed by ID.
     */
    ListenableFuture<ImmutableMap<Long, ShoppingList>> getShoppingListsAsync(ShopMateSession session);
}
