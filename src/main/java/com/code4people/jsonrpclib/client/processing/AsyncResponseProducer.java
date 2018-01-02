package com.code4people.jsonrpclib.client.processing;

import com.code4people.jsonrpclib.client.model.Response;

import java.util.concurrent.CompletableFuture;

public interface AsyncResponseProducer extends AutoCloseable {
    CompletableFuture<Response> produceAsyncResponse(Object id);
    @Override
    void close();
}
