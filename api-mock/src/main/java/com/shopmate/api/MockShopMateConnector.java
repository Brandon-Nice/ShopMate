package com.shopmate.api;

import com.google.common.util.concurrent.ListenableFuture;

public class MockShopMateConnector implements ShopMateConnector {

    MockShopMateConnector() {
    }

    @Override
    public ListenableFuture<ShopMateLoginResult> logInAsync(String fbToken) {
        return null;
    }
}
