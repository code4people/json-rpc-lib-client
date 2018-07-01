package com.code4people.jsonrpclib.client;

import com.code4people.jsonrpclib.client.factories.*;
import com.code4people.jsonrpclib.client.messaging.MessageReceiver;
import com.code4people.jsonrpclib.client.messaging.MessageSender;
import com.code4people.jsonrpclib.client.model.ResponseError;
import com.code4people.jsonrpclib.client.processing.RequestSender;
import com.code4people.jsonrpclib.client.processing.ResponseBuffer;
import com.code4people.jsonrpclib.client.serialization.MessageSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;

public class ClientContextFactory {
    private final Duration timeout;
    private final Supplier<? extends Object> idGenerator;
    private final Map<Class<? extends Throwable>, Function<ResponseError, ? extends Throwable>> exceptionFactories;

    public ClientContextFactory(Duration timeout, Supplier<? extends Object> idGenerator, Map<Class<? extends Throwable>, Function<ResponseError, ? extends Throwable>> exceptionFactories) {
        Objects.requireNonNull(timeout);
        Objects.requireNonNull(idGenerator);
        Objects.requireNonNull(exceptionFactories);

        this.timeout = timeout;
        this.idGenerator = idGenerator;
        this.exceptionFactories = exceptionFactories;
    }

    public ClientContext create(MessageSender messageSender) {
        Objects.requireNonNull(messageSender, "'messageSender' cannot be null");

        ResponseBuffer responseBuffer = new ResponseBuffer(timeout);
        MessageSerializer messageSerializer = new MessageSerializer(new ObjectMapper());
        MessageReceiver messageReceiver = new MessageReceiver(messageSerializer, responseBuffer);
        RequestSender requestSender = new RequestSender(messageSerializer, messageSender);
        ParamsSerializerFactory paramsSerializerFactory = new ParamsSerializerFactory(new ObjectMapper());
        ResultDeserializerFactory resultDeserializerFactory = new ResultDeserializerFactory(new ObjectMapper());
        MethodFactory methodFactory = new MethodFactory(
                requestSender,
                paramsSerializerFactory,
                resultDeserializerFactory,
                idGenerator,
                exceptionFactories);
        MethodDispatcherFactory methodDispatcherFactory = new MethodDispatcherFactory(methodFactory);
        ClientProxyFactory clientProxyFactory = new ClientProxyFactory(methodDispatcherFactory);
        return new ClientContext(messageReceiver, clientProxyFactory, responseBuffer, messageSender);
    }
}
