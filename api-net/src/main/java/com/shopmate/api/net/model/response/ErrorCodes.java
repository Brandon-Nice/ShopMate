package com.shopmate.api.net.model.response;

import com.shopmate.api.ShopMateErrorCode;

import java.util.HashMap;
import java.util.Map;

public final class ErrorCodes {
    private ErrorCodes() {
    }

    private static Map<String, ShopMateErrorCode> ErrorStringMap;
    private static Map<ShopMateErrorCode, String> ErrorMessageMap;

    static {
        ErrorStringMap = new HashMap<>();
        ErrorStringMap.put("invalid_token", ShopMateErrorCode.INVALID_TOKEN);
        ErrorStringMap.put("bad_request", ShopMateErrorCode.BAD_REQUEST);
        ErrorStringMap.put("server_error", ShopMateErrorCode.SERVER_ERROR);

        ErrorMessageMap = new HashMap<>();
        ErrorMessageMap.put(ShopMateErrorCode.INVALID_TOKEN, "An invalid login token was supplied");
        ErrorMessageMap.put(ShopMateErrorCode.CONNECTION_ERROR, "An error occurred while connecting to ShopMate");
        ErrorMessageMap.put(ShopMateErrorCode.BAD_REQUEST, "The request was invalid");
        ErrorMessageMap.put(ShopMateErrorCode.SERVER_ERROR, "An internal error occurred on the server");
    }

    public static ShopMateErrorCode getErrorCode(String errorString) {
        if (errorString != null && ErrorStringMap.containsKey(errorString)) {
            return ErrorStringMap.get(errorString);
        } else {
            return ShopMateErrorCode.SERVER_ERROR;
        }
    }

    public static String getErrorMessage(ShopMateErrorCode errorCode) {
        if (ErrorMessageMap.containsKey(errorCode)) {
            return ErrorMessageMap.get(errorCode);
        } else {
            return "An unknown error occurred";
        }
    }
}
