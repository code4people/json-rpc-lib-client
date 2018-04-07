package com.code4people.jsonrpclib.client.serialization;

import com.code4people.jsonrpclib.client.exceptions.SerializationException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.NullNode;

public class PositionalParamsSerializer implements ParamsSerializer {

    private final ObjectMapper objectMapper;

    public PositionalParamsSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public JsonNode serialize(Object[] args) throws SerializationException {
        if (args == null) {
            return null;
        }
        ArrayNode arrayNode = objectMapper.createArrayNode();
        for (int i = 0; i < args.length; i++) {
            JsonNode jsonNode = objectMapper.valueToTree(args[i]);
            arrayNode.add(jsonNode);
        }
        return arrayNode;
    }
}
