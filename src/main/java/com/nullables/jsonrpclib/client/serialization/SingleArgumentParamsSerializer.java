package com.nullables.jsonrpclib.client.serialization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nullables.jsonrpclib.client.exceptions.SerializationException;

public class SingleArgumentParamsSerializer implements ParamsSerializer {

    private final ObjectMapper objectMapper;

    public SingleArgumentParamsSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public JsonNode serialize(Object[] args) throws SerializationException {
        return objectMapper.valueToTree(args[0]);
    }
}
