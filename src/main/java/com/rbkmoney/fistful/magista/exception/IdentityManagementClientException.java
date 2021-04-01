package com.rbkmoney.fistful.magista.exception;

public class IdentityManagementClientException extends RuntimeException {
    public IdentityManagementClientException() {
    }

    public IdentityManagementClientException(String message) {
        super(message);
    }

    public IdentityManagementClientException(String message, Throwable cause) {
        super(message, cause);
    }

    public IdentityManagementClientException(Throwable cause) {
        super(cause);
    }

    public IdentityManagementClientException(String message, Throwable cause, boolean enableSuppression,
                                             boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
