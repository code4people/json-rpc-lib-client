package com.nullables.jsonrpclib.client.serialization;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nullables.jsonrpclib.client.exceptions.SerializationException;
import com.nullables.jsonrpclib.client.model.Response;

import java.io.IOException;

public class MessageSerializer {
    private final ObjectMapper objectMapper;

    public MessageSerializer(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
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
