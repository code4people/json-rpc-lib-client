package com.nullables.jsonrpclib.client.exceptions;

import com.fasterxml.jackson.databind.JsonNode;

public class ServerException extends InvocationException {
    private final int code;
    private final JsonNode data;

    public ServerException(int code, String message, JsonNode data) {
        super(message);
        this.code = code;
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public JsonNode getData() {
        return data;
    }
}
