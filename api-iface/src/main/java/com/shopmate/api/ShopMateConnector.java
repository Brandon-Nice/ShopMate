package com.shopmate.api;

import com.google.common.util.concurrent.ListenableFuture;

/**
 * Interface for an object which opens connections to the ShopMate API.
 * Use this to acquire a session object.
 */
public interface ShopMateConnector {

    /**
     * Asynchronously logs into the ShopMate API.
     * @param fbToken The Facebook access token to log in with.
     */
    ListenableFuture<ShopMateLoginResult> logInAsync(String fbToken);
}
