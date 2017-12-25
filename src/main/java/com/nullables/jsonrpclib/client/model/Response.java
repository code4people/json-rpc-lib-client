package com.nullables.jsonrpclib.client.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

public class Response {
    private final String jsonrpc;
    private final Object id;
    private final JsonNode result;
    private final ResponseError error;

    @JsonCreator
    public static Response create(
            @JsonProperty("jsonrpc") String jsonrpc,
            @JsonProperty("id") Object id,
            @JsonProperty("result") JsonNode result,
            @JsonProperty("error") ResponseError error) {
        return new Response(jsonrpc, id, result, error);
    }

    private Response(String jsonrpc, Object id, JsonNode result, ResponseError error) {
        this.jsonrpc = jsonrpc;
        this.id = id;
        this.result = result;
        this.error = error;
    }

    public String getJsonrpc() {
        return jsonrpc;
    }

    public Object getId() {
        return id;
    }

    public JsonNode getResult() {
        return result;
    }

    public ResponseError getError() {
        return error;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Response response = (Response) o;

        if (id != null ? !id.equals(response.id) : response.id != null) return false;
        if (result != null ? !result.equals(response.result) : response.result != null) return false;
        return error != null ? error.equals(response.error) : response.error == null;
    }

    @Override
    public int hashCode() {
        int result1 = jsonrpc.hashCode();
        result1 = 31 * result1 + (id != null ? id.hashCode() : 0);
        result1 = 31 * result1 + (result != null ? result.hashCode() : 0);
        result1 = 31 * result1 + (error != null ? error.hashCode() : 0);
        return result1;
    }
}
