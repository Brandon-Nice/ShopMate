package com.shopmate.api;

public class ShopMateConnectorFactory {

    private ShopMateConnectorFactory() {
    }

    public static ShopMateConnector create() {
        return new MockShopMateConnector();
    }
}
