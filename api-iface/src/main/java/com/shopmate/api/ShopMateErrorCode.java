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
     * An internal exception occurred.
     */
    INTERNAL_ERROR,
}