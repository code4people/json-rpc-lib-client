package com.code4people.jsonrpclib.client.processing;

import com.code4people.jsonrpclib.client.exceptions.ClientException;
import com.code4people.jsonrpclib.client.model.Response;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.*;

public class ResponseBuffer implements AsyncResponseProducer, ResponseReceiver {
    private final Map<Object, CompletableFuture<Response>> futureMap = new ConcurrentHashMap<>();
    private final Duration timeout;
    private volatile boolean closed;

    public ResponseBuffer(Duration timeout) {
        this.timeout = timeout;
    }

    @Override
    public boolean receive(Response response) {

        CompletableFuture<Response> future = futureMap.get(response.getId());
        return future != null && future.complete(response);
    }

    @Override
    public CompletableFuture<Response> produceAsyncResponse(Object id) {
        if (closed) {
            CompletableFuture<Response> failedCf = new CompletableFuture<>();
            failedCf.completeExceptionally(new IllegalStateException("ResponseBuffer already closed"));
            return failedCf;
        }

        CompletableFuture<Response> responseCf = futureMap.computeIfAbsent(
                id,
                (key) -> {
                    CompletableFuture<Response> cf = new CompletableFuture<>();
                    return cf.whenComplete((response, throwable) -> futureMap.remove(key))
                            .orTimeout(timeout.toMillis(), TimeUnit.MILLISECONDS)
                            .handle((response, throwable) -> {
                                if (response != null) {
                                    return response;
                                }
                                else if (throwable instanceof TimeoutException) {
                                    throw new ClientException("Invocation expired", throwable);
                                }
                                else {
                                    throw new CompletionException(throwable);
                                }
                            });
                });

        if (closed) {
            responseCf.cancel(true);
        }

        return responseCf;
    }

    public void close() {
        if (closed) {
            return;
        }

        closed = true;
        futureMap.values().forEach(x -> x.cancel(true));
    }
}
