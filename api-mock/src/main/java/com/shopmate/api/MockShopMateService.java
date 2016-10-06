package com.shopmate.api;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.util.concurrent.AsyncFunction;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.shopmate.api.model.list.ShoppingList;
import com.shopmate.api.model.result.CreateShoppingListResult;
import com.shopmate.api.model.result.LogInResult;

import java.util.HashMap;
import java.util.Map;

public class MockShopMateService implements ShopMateService {

    private Map<String, MockShopMateState> states = new HashMap<>();

    @Override
    public ListenableFuture<LogInResult> logInAsync(String fbToken) {
        ShopMateSession session = new ShopMateSession("100000476614024", fbToken);
        MockShopMateState state = new MockShopMateState(session);
        states.put(session.getSessionToken(), state);
        LogInResult result = new LogInResult(session, ImmutableMap.<Long, ShoppingList>of());
        return Futures.immediateFuture(result);
    }

    @Override
    public ListenableFuture<CreateShoppingListResult> createShoppingListAsync(
            ShopMateSession session,
            final String title) {
        return Futures.transformAsync(getState(session), new AsyncFunction<MockShopMateState, CreateShoppingListResult>() {
            @Override
            public ListenableFuture<CreateShoppingListResult> apply(MockShopMateState input) throws Exception {
                return input.createShoppingListAsync(title);
            }
        });
    }

    @Override
    public ListenableFuture<ImmutableMap<Long, ShoppingList>> getShoppingListsAsync(
            ShopMateSession session) {
        return Futures.transformAsync(getState(session), new AsyncFunction<MockShopMateState, ImmutableMap<Long, ShoppingList>>() {
            @Override
            public ListenableFuture<ImmutableMap<Long, ShoppingList>> apply(MockShopMateState input) throws Exception {
                return input.getShoppingListsAsync();
            }
        });
    }

    private ListenableFuture<MockShopMateState> getState(ShopMateSession session) {
        synchronized (states) {
            if (!states.containsKey(session.getSessionToken())) {
                return Futures.immediateFailedFuture(new ShopMateException(ShopMateErrorCode.INVALID_TOKEN, "Invalid login token"));
            }
            return Futures.immediateFuture(states.get(session.getSessionToken()));
        }
    }

    private static class MockShopMateState {

        private final ShopMateSession session;
        private final Map<Long, ShoppingList> shoppingLists = new HashMap<>();
        private long nextId;

        public MockShopMateState(ShopMateSession session) {
            this.session = session;
        }

        public synchronized ListenableFuture<CreateShoppingListResult> createShoppingListAsync(String title) {
            ShoppingList newList = new ShoppingList(
                    session.getUserFbid(),
                    title,
                    ImmutableSet.of(session.getUserFbid()),
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
