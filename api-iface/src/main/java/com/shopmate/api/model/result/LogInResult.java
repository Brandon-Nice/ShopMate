package com.shopmate.api.model.result;

import com.google.common.collect.ImmutableMap;
import com.shopmate.api.ShopMateSession;
import com.shopmate.api.model.list.ShoppingList;

public class LogInResult {

    private final ShopMateSession session;
    private final ImmutableMap<Long, ShoppingList> shoppingLists;

    public LogInResult(ShopMateSession session, ImmutableMap<Long, ShoppingList> shoppingLists) {
        this.session = session;
        this.shoppingLists = shoppingLists;
    }

    /**
     * @return The session object to use to make requests as the user.
     */
    public ShopMateSession getSession() {
        return session;
    }

    /**
     * @return The shopping lists that the user is a member of, keyed by list ID.
     */
    public ImmutableMap<Long, ShoppingList> getShoppingLists() {
        return shoppingLists;
    }
}
