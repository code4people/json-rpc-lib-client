package com.nullables.jsonrpclib.client;

import com.nullables.jsonrpclib.client.factories.ClientProxyFactory;
import com.nullables.jsonrpclib.client.processing.AsyncResponseProducer;
import com.nullables.jsonrpclib.client.messaging.MessageReceiver;

public class ClientContext implements AutoCloseable {

    private final MessageReceiver messageReceiver;
    private final ClientProxyFactory clientProxyFactory;
    private final AsyncResponseProducer asyncResponseProducer;

    ClientContext(MessageReceiver messageReceiver, ClientProxyFactory clientProxyFactory, AsyncResponseProducer asyncResponseProducer) {
        this.messageReceiver = messageReceiver;
        this.clientProxyFactory = clientProxyFactory;
        this.asyncResponseProducer = asyncResponseProducer;
    }

    public <T> T createProxyOf(Class<T> clazz) {
        return clientProxyFactory.create(clazz, asyncResponseProducer);
    }

    public MessageReceiver getMessageReceiver() {
        return messageReceiver;
    }

    @Override
    public void close() {
        asyncResponseProducer.close();
    }
}
