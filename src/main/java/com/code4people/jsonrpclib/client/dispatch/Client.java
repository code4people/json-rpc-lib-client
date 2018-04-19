package com.code4people.jsonrpclib.client.dispatch;

import com.code4people.jsonrpclib.client.model.Request;
import com.code4people.jsonrpclib.client.processing.RequestSender;
import com.code4people.jsonrpclib.client.model.Response;
import com.code4people.jsonrpclib.client.processing.AsyncResponseProducer;

import java.util.concurrent.CompletableFuture;

public class Client {
    private final RequestSender requestSender;
    private final AsyncResponseProducer asyncResponseProducer;

    public Client(RequestSender requestSender, AsyncResponseProducer asyncResponseProducer) {
        this.requestSender = requestSender;
        this.asyncResponseProducer = asyncResponseProducer;
    }

    public Response send(Request request) {
        CompletableFuture<Response> responseCompletableFuture = asyncResponseProducer.produceAsyncResponse(request.getId());
        requestSender.send(request);
        return responseCompletableFuture.join();
    }

    public CompletableFuture<Response> sendAsync(Request request) {
        CompletableFuture<Response> responseCompletableFuture = asyncResponseProducer.produceAsyncResponse(request.getId());
        requestSender.send(request);
        return responseCompletableFuture;
    }
}
