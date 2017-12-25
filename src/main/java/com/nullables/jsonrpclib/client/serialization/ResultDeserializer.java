package com.nullables.jsonrpclib.client.serialization;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.nullables.jsonrpclib.client.exceptions.SerializationException;

import java.lang.reflect.Type;

public class ResultDeserializer {
    private final ObjectMapper mapper;
    private final TypeFactory typeFactory = TypeFactory.defaultInstance();

    public ResultDeserializer(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    public <T> T deserialize(String json, Type type) throws SerializationException {
        try {
            JavaType javaType = typeFactory.constructType(type);
            return mapper.convertValue(json, javaType);
        }
        catch (IllegalArgumentException e) {
            String message = String.format("Deserialization failed. json: '%s', type: '%s'", json, type);
            throw new SerializationException(message, e);
        }
    }

    public <T> T deserialize(JsonNode jsonNode, Type type) throws SerializationException {
        try {
            JavaType javaType = typeFactory.constructType(type);
            return mapper.convertValue(jsonNode, javaType);
        }
        catch (IllegalArgumentException e) {
            String message = String.format("Deserialization failed. json: '%s', type: '%s'", jsonNode, type);
            throw new SerializationException(message, e);
        }
    }
}
