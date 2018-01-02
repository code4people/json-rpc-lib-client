package com.code4people.jsonrpclib.client.serialization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.code4people.jsonrpclib.client.exceptions.SerializationException;

public class PositionalParamsSerializer implements ParamsSerializer {

    private final ObjectMapper objectMapper;

    public PositionalParamsSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public JsonNode serialize(Object[] args) throws SerializationException {
        ArrayNode arrayNode = objectMapper.createArrayNode();
        for (int i = 0; i < args.length; i++) {
            JsonNode jsonNode = objectMapper.valueToTree(args[i]);
            arrayNode.add(jsonNode);
        }
        return arrayNode;
    }
}
