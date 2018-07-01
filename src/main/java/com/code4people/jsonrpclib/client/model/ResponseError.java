package com.code4people.jsonrpclib.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.Objects;

public class ResponseError {

    private final int code;
    private final String message;
    private final JsonNode data;
    private final JsonNode debugErrorData;

    @JsonCreator
    public ResponseError(@JsonProperty("code") int code,
                         @JsonProperty("message") String message,
                         @JsonProperty("data") JsonNode data,
                         @JsonProperty("_debugErrorData") JsonNode debugErrorData) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.debugErrorData = debugErrorData;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public JsonNode getData() {
        return data;
    }

    public JsonNode getDebugErrorData() {
        return debugErrorData;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResponseError that = (ResponseError) o;
        return code == that.code &&
                Objects.equals(message, that.message) &&
                Objects.equals(data, that.data) &&
                Objects.equals(debugErrorData, that.debugErrorData);
    }

    @Override
    public int hashCode() {

        return Objects.hash(code, message, data, debugErrorData);
    }
}
