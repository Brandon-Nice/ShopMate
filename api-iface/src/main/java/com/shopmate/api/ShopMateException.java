package com.shopmate.api;

/**
 * An exception thrown by API calls.
 */
public class ShopMateException extends Exception {

    private ShopMateErrorCode code;

    public ShopMateException(ShopMateErrorCode code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public ShopMateException(ShopMateErrorCode code, String message) {
        super(message);
        this.code = code;
    }

    public ShopMateException(ShopMateErrorCode code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public ShopMateException(String message) {
        this(ShopMateErrorCode.INTERNAL_ERROR, message);
    }

    public ShopMateException(Throwable cause) {
        this(ShopMateErrorCode.INTERNAL_ERROR, cause);
    }

    public ShopMateErrorCode getCode() {
        return code;
    }
}
