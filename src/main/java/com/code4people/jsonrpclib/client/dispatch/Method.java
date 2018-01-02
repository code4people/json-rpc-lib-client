package com.code4people.jsonrpclib.client.dispatch;

import com.code4people.jsonrpclib.client.exceptions.ClientException;
import com.code4people.jsonrpclib.client.exceptions.ServerException;
import com.code4people.jsonrpclib.client.model.Request;
import com.code4people.jsonrpclib.client.serialization.ParamsSerializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.code4people.jsonrpclib.client.exceptions.SerializationException;
import com.code4people.jsonrpclib.client.model.ResponseError;
import com.code4people.jsonrpclib.client.serialization.ResultDeserializer;

import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class Method {
    private final String methodName;
    private final Type returnType;
    private final ParamsSerializer paramsSerializer;
    private final ResultDeserializer resultDeserializer;
    private final Client client;
    private final Supplier<? extends Object> idSupplier;

    public Method(String methodName,
                  Type returnType,
                  ParamsSerializer paramsSerializer,
                  ResultDeserializer resultDeserializer,
                  Client client,
                  Supplier<? extends Object> idSupplier) {
        this.methodName = methodName;
        this.returnType = returnType;
        this.paramsSerializer = paramsSerializer;
        this.resultDeserializer = resultDeserializer;
        this.client = client;
        this.idSupplier = idSupplier;
    }

    public CompletableFuture<Object> invoke(Object[] args) {

        JsonNode params;
        try {
            params = paramsSerializer.serialize(args);
        } catch (SerializationException e) {
            CompletableFuture<Object> failedCf = new CompletableFuture<>();
            failedCf.completeExceptionally(new ClientException("", e));
            return failedCf;
        }
        Request request = new Request("2.0", methodName, idSupplier.get(), params);

        return client.sendAsync(request)
                .thenApply(response -> {
                    if (response.getError() == null) {
                        try {
                            return resultDeserializer.deserialize(response.getResult(), returnType);
                        } catch (SerializationException e) {
                            throw new ClientException("Cannot deserialize result.", e);
                        }
                    } else {
                        ResponseError responseError = response.getError();
                        throw new ServerException(responseError.getCode(), responseError.getMessage(), responseError.getData());
                    }
                });
    }
}
