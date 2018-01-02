package com.code4people.jsonrpclib.client.exceptions;

public class ClientException extends InvocationException {
    public ClientException(String message) {
        super(message);
    }

    public ClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
