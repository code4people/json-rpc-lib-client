package com.code4people.jsonrpclib.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;

@JsonPropertyOrder({ "jsonrpc", "id", "method", "params" })
public class Request {
    private final String jsonrpc;
    private final Object id;
    private final String method;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private final JsonNode params;

    @JsonCreator
    public Request(
            String jsonrpc,
            String method,
            Object id,
            JsonNode params) {
        this.jsonrpc = jsonrpc;
        this.method = method;
        this.id = id;
        this.params = params;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public Object getId() {
        return id;
    }

    public String getMethod() {
        return method;
    }

    public JsonNode getParams() {
        return params;
    }
}
