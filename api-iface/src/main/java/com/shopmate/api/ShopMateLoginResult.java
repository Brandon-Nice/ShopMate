package com.shopmate.api;

import com.google.common.collect.ImmutableSet;
import com.shopmate.api.ShopMateSession;
import com.shopmate.api.model.list.ShoppingList;

public class ShopMateLoginResult {

    private final ShopMateSession session;
    private final ImmutableSet<ShoppingList> shoppingLists;

    public ShopMateLoginResult(ShopMateSession session, ImmutableSet<ShoppingList> shoppingLists) {
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
     * @return The shopping lists that the user is a member of.
     */
    public ImmutableSet<ShoppingList> getShoppingLists() {
        return shoppingLists;
    }
}
