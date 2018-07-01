package com.code4people.jsonrpclib.client.serialization;

import com.code4people.jsonrpclib.client.model.Response;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.code4people.jsonrpclib.client.exceptions.SerializationException;

import java.io.IOException;

public class MessageSerializer {
    private final ObjectMapper objectMapper;

    public MessageSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Response deserialize(JsonNode jsonNode) throws SerializationException {
        try {
            return objectMapper.convertValue(jsonNode, Response.class);
        } catch (IllegalArgumentException e) {
            throw new SerializationException("", e);
        }
    }

    public Response deserialize(String message) throws SerializationException {
        try {
            return objectMapper.readValue(message, Response.class);
        } catch (IOException e) {
            throw new SerializationException("", e);
        }
    }

    public String serializeToString(Object object) throws SerializationException {
        try {
            return objectMapper.writeValueAsString(object);
        }
        catch (JsonProcessingException e) {
            String message = String.format(
                    "Serialization failed. class: '%s'",
                    object == null ? "null object" : object.getClass());
            throw new SerializationException(message, e);
        }
    }
}
