package com.nullables.jsonrpclib.client.exceptions;

public class ClientException extends InvocationException {
    public ClientException(String message) {
        super(message);
    }

    public ClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
