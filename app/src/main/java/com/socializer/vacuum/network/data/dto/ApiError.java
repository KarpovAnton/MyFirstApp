package com.socializer.vacuum.network.data.dto;

public class ApiError {

    // Pebby API AuthExceptions
    public static final int AUTH_EXCEPTION_NO_FOUND = 100;
    public static final int AUTH_EXCEPTION_INVALID_PASSWORD = 101;
    public static final int AUTH_EXCEPTION_INVALID_TOKEN = 102;
    public static final int AUTH_EXCEPTION_FACEBOOK_LOGIN_ERROR = 103;

    // Pebby API NetworkExceptions
    public static final int NETWORK_EXCEPTION_IOT_GATEWAY_DISABLE = 200;
    public static final int NETWORK_EXCEPTION_IOT_GATEWAY_NOT_AVAILABLE = 201;

    // Pebby API PermissionExceptions
    public static final int PERMISSION_EXCEPTION_SIGNATURE_INVALID = 300;
    public static final int PERMISSION_EXCEPTION_NO_PERMISSION = 301;
    public static final int PERMISSION_EXCEPTION_INVALID_SECRET_CODE = 302;

    // Pebby API ValidationExceptions
    public static final int VALIDATION_EXCEPTION_VALIDATION_ERROR = 400;
    public static final int VALIDATION_EXCEPTION_INVALID_JSON_FORMAT = 401;
    public static final int VALIDATION_EXCEPTION_RESOURCE_NOT_FOUND = 402;
    public static final int VALIDATION_EXCEPTION_REGISTRATION_LOGIN_EXISTS = 403;
    public static final int VALIDATION_EXCEPTION_REGISTRATION_INVALID_CODE = 404;
    public static final int VALIDATION_EXCEPTION_REGISTRATION_EXPIRED_CODE = 405;
    public static final int VALIDATION_EXCEPTION_INVALID_PASSWORD = 406;


    // Default codes
    public static final int PARSE_ERROR = 9998;
    public static final int UNDEFINED = 9999;

    private int code;

    private String message;

    private String exception;

    public ApiError() {
    }

    public ApiError(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getException() {
        return exception;
    }
}
