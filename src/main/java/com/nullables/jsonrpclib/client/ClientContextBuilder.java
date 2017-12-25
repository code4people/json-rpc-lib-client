package com.nullables.jsonrpclib.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nullables.jsonrpclib.client.factories.ClientProxyFactory;
import com.nullables.jsonrpclib.client.factories.MethodDispatcherFactory;
import com.nullables.jsonrpclib.client.factories.MethodFactory;
import com.nullables.jsonrpclib.client.factories.ParamsSerializerFactory;
import com.nullables.jsonrpclib.client.factories.ResultDeserializerFactory;
import com.nullables.jsonrpclib.client.messaging.MessageReceiver;
import com.nullables.jsonrpclib.client.messaging.MessageSender;
import com.nullables.jsonrpclib.client.processing.RequestSender;
import com.nullables.jsonrpclib.client.processing.ResponseBuffer;
import com.nullables.jsonrpclib.client.serialization.MessageSerializer;

import java.time.Duration;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class ClientContextBuilder {
    private MessageSender messageSender;
    private Duration timeout = Duration.ofSeconds(30);
    private Supplier<? extends Object> idGenerator = () -> UUID.randomUUID().toString();

    public ClientContextBuilder messageSender(MessageSender messageSender) {
        Objects.requireNonNull(messageSender, "'messageSender' cannot be null");
        this.messageSender = messageSender;
        return this;
    }

    public ClientContextBuilder idGenerator(Supplier<String> idGenerator) {
        Objects.requireNonNull(idGenerator, "'idGenerator' cannot be null");
        this.idGenerator = idGenerator;
        return this;
    }

    public ClientContextBuilder timeout(Duration timeout) {
        Objects.requireNonNull(idGenerator, "'timeout' cannot be null");
        this.timeout = timeout;
        return this;
    }

    public ClientContext build() {
        if (messageSender == null) {
            throw new IllegalStateException("'messageSender' is not set");
        }

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
                idGenerator);
        MethodDispatcherFactory methodDispatcherFactory = new MethodDispatcherFactory(methodFactory);
        ClientProxyFactory clientProxyFactory = new ClientProxyFactory(methodDispatcherFactory);
        return new ClientContext(messageReceiver, clientProxyFactory, responseBuffer);
    }

}
