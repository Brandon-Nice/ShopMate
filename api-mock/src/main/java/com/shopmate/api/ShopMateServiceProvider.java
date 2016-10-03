package com.shopmate.api;

public class ShopMateServiceProvider {

    private static ShopMateService service;

    private ShopMateServiceProvider() {
    }

    public static ShopMateService get() {
        if (service == null) {
            service = new MockShopMateService();
        }
        return service;
    }
}
