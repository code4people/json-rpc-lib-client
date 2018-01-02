package com.code4people.jsonrpclib.client.factories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.code4people.jsonrpclib.client.serialization.ResultDeserializer;

public class ResultDeserializerFactory {
    private final ObjectMapper objectMapper;

    public ResultDeserializerFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public ResultDeserializer create() {
        return new ResultDeserializer(objectMapper);
    }
}
