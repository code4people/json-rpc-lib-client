package com.nullables.jsonrpclib.client.serialization;

import com.fasterxml.jackson.databind.JsonNode;
import com.nullables.jsonrpclib.client.exceptions.SerializationException;

public interface ParamsSerializer {
    JsonNode serialize(Object[] args) throws SerializationException;
}
