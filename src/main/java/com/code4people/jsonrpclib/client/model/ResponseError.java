package com.code4people.jsonrpclib.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class ResponseError {

    private final int code;
    private final String message;
    private final JsonNode data;

    @JsonCreator
    public ResponseError(@JsonProperty("code") int code,
                         @JsonProperty("message") String message,
                         @JsonProperty("data") JsonNode data) {
        this.code = code;
        this.message = message;
        this.data = data;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ResponseError error = (ResponseError) o;

        if (code != error.code) return false;
        if (message != null ? !message.equals(error.message) : error.message != null) return false;
        return data != null ? data.equals(error.data) : error.data == null;
    }

    @Override
    public int hashCode() {
        int result = code;
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }
}
