package com.shopmate.api;

import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.shopmate.api.model.list.ShoppingList;

import java.util.HashSet;
import java.util.Set;

public class MockShopMateSession implements ShopMateSession {

    private Set<ShoppingList> shoppingLists = new HashSet<>();

    @Override
    public String getUserId() {
        return "100000476614024"; // Aaron
    }

    @Override
    public ListenableFuture<ShoppingList> createShoppingListAsync(ShoppingList list) {
        shoppingLists.add(list);
        return Futures.immediateFuture(list);
    }

    @Override
    public ListenableFuture<ImmutableSet<ShoppingList>> getShoppingListsAsync() {
        return Futures.immediateFuture(ImmutableSet.copyOf(shoppingLists));
    }
}
