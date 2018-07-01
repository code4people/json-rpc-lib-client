package com.code4people.jsonrpclib.client.exceptions;

import com.fasterxml.jackson.databind.JsonNode;

public class ServerException extends InvocationException {
    private final int code;
    private final JsonNode data;
    private final JsonNode debugErrorData;

    public ServerException(int code, String message, JsonNode data, JsonNode debugErrorData) {
        super(message);
        this.code = code;
        this.data = data;
        this.debugErrorData = debugErrorData;
    }

    public int getCode() {
        return code;
    }

    public JsonNode getData() {
        return data;
    }

    public JsonNode getDebugErrorData() {
        return debugErrorData;
    }
}
