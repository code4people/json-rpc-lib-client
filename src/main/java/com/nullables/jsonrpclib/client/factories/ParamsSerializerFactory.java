package com.nullables.jsonrpclib.client.factories;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nullables.jsonrpclib.client.serialization.MissingParamsSerializer;
import com.nullables.jsonrpclib.client.serialization.NamedParamsSerializer;
import com.nullables.jsonrpclib.client.serialization.PositionalParamsSerializer;
import com.nullables.jsonrpclib.client.serialization.SingleArgumentParamsSerializer;
import com.nullables.jsonrpclib.binding.info.NamedParamsInfo;

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
