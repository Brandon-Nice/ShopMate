package com.shopmate.api;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.shopmate.api.model.list.ShoppingList;
import com.shopmate.api.model.result.CreateShoppingListResult;

import java.util.HashMap;
import java.util.Map;

public class MockShopMateService implements ShopMateService {

    public static final String USER_ID = "100000476614024";

    private Map<String, MockShopMateState> states = new HashMap<>();

    @Override
    public ListenableFuture<CreateShoppingListResult> createShoppingListAsync(String fbToken, final String title) {
        return Futures.transformAsync(getState(fbToken), new AsyncFunction<MockShopMateState, CreateShoppingListResult>() {
            @Override
            public ListenableFuture<CreateShoppingListResult> apply(MockShopMateState input) throws Exception {
                return input.createShoppingListAsync(title);
            }
        });
    }

    @Override
    public ListenableFuture<ImmutableMap<Long, ShoppingList>> getShoppingListsAsync(String fbToken) {
        return Futures.transformAsync(getState(fbToken), new AsyncFunction<MockShopMateState, ImmutableMap<Long, ShoppingList>>() {
            @Override
            public ListenableFuture<ImmutableMap<Long, ShoppingList>> apply(MockShopMateState input) throws Exception {
                return input.getShoppingListsAsync();
            }
        });
    }

    private ListenableFuture<MockShopMateState> getState(String fbToken) {
        synchronized (states) {
            MockShopMateState state;
            if (states.containsKey(fbToken)) {
                state = states.get(fbToken);
            } else {
                state = new MockShopMateState(USER_ID);
                states.put(fbToken, state);
            }
            return Futures.immediateFuture(state);
        }
    }

    private static class MockShopMateState {

        private final String fbid;
        private final Map<Long, ShoppingList> shoppingLists = new HashMap<>();
        private long nextId;

        public MockShopMateState(String fbid) {
            this.fbid = fbid;
        }

        public synchronized ListenableFuture<CreateShoppingListResult> createShoppingListAsync(String title) {
            ShoppingList newList = new ShoppingList(
                    fbid,
                    title,
                    ImmutableSet.of(fbid),
                    ImmutableSet.<Long>of());
            long id = nextId++;
            shoppingLists.put(id, newList);
            return Futures.immediateFuture(new CreateShoppingListResult(id, newList));
        }

        public synchronized ListenableFuture<ImmutableMap<Long, ShoppingList>> getShoppingListsAsync() {
            return Futures.immediateFuture(ImmutableMap.copyOf(shoppingLists));
        }
    }
}
