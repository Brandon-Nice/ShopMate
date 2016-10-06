package com.shopmate.api;

public enum ShopMateErrorCode {
    /**
     * An invalid login token was supplied.
     */
    INVALID_TOKEN,

    /**
     * A network connection error occurred.
     */
    CONNECTION_ERROR,

    /**
     * The request was invalid.
     */
    BAD_REQUEST,

    /**
     * An internal error occurred on the server.
     */
    SERVER_ERROR,
}