package com.nullables.jsonrpclib.client.processing;

import com.nullables.jsonrpclib.client.model.Response;

import java.util.concurrent.CompletableFuture;

public interface AsyncResponseProducer extends AutoCloseable {
    CompletableFuture<Response> produceAsyncResponse(Object id);
    @Override
    void close();
}
