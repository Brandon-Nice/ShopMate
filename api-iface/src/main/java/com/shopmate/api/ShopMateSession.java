package com.shopmate.api;

/**
 * Represents a ShopMate session token.
 */
public class ShopMateSession {

    private final String fbid;
    private final String sessionToken;

    public ShopMateSession(String fbid, String sessionToken) {
        this.fbid = fbid;
        this.sessionToken = sessionToken;
    }

    /**
     * @return The FBID of the logged in user.
     */
    public String getUserFbid() {
        return fbid;
    }

    /**
     * @return The session token used to identify the user's session (implementation-dependent).
     */
    public String getSessionToken() {
        return sessionToken;
    }
}
