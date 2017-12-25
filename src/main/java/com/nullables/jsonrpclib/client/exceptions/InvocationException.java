package com.nullables.jsonrpclib.client.exceptions;

public abstract class InvocationException extends RuntimeException {
    public InvocationException(String message) {
        super(message);
    }

    public InvocationException(String message, Throwable cause) {
        super(message, cause);
    }
}
