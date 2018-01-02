package com.code4people.jsonrpclib.client.serialization;

import com.fasterxml.jackson.databind.JsonNode;
import com.code4people.jsonrpclib.client.exceptions.SerializationException;

public interface ParamsSerializer {
    JsonNode serialize(Object[] args) throws SerializationException;
}
