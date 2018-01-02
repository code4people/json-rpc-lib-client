package com.code4people.jsonrpclib.client.factories;

import com.code4people.jsonrpclib.client.serialization.MissingParamsSerializer;
import com.code4people.jsonrpclib.client.serialization.NamedParamsSerializer;
import com.code4people.jsonrpclib.client.serialization.SingleArgumentParamsSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.code4people.jsonrpclib.client.serialization.PositionalParamsSerializer;
import com.code4people.jsonrpclib.binding.info.NamedParamsInfo;

import java.util.List;
import java.util.stream.Collectors;

public class ParamsSerializerFactory {
    private final ObjectMapper objectMapper;

    public ParamsSerializerFactory(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public NamedParamsSerializer createNamedParamsSerializer(NamedParamsInfo namedParamsInfo) {
        List<String> parameters = namedParamsInfo.getParameters().stream().map(parameter -> parameter.name).collect(Collectors.toList());
        return new NamedParamsSerializer(objectMapper, parameters);
    }

    public PositionalParamsSerializer createPositionalParamsSerializer() {
        return new PositionalParamsSerializer(objectMapper);
    }

    public SingleArgumentParamsSerializer createSingleArgumentParamsSerializer() {
        return new SingleArgumentParamsSerializer(objectMapper);
    }

    public MissingParamsSerializer createMissingParamsSerializer() {
        return new MissingParamsSerializer();
    }
}
