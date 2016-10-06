package com.shopmate.api;

import com.shopmate.api.net.NetShopMateService;

public class ShopMateServiceProvider {

    private static ShopMateService service;

    private ShopMateServiceProvider() {
    }

    public static ShopMateService get() {
        if (service == null) {
            service = new NetShopMateService();
        }
        return service;
    }
}
