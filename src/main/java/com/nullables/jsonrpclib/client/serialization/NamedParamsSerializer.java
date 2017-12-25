package com.nullables.jsonrpclib.client.serialization;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public class NamedParamsSerializer implements ParamsSerializer {

    private final ObjectMapper objectMapper;
    private final List<String> parameters;

    public NamedParamsSerializer(ObjectMapper objectMapper, List<String> parameters) {
        this.objectMapper = objectMapper;
        this.parameters = parameters;
    }

    @Override
    public JsonNode serialize(Object[] args) {
        ObjectNode objectNode = objectMapper.createObjectNode();
        for (int i = 0; i < parameters.size(); i++) {
            String parameterName = parameters.get(i);
            JsonNode parameterValue = objectMapper.valueToTree(args[i]);
            objectNode.set(parameterName, parameterValue);
        }
        return objectNode;
    }
}
