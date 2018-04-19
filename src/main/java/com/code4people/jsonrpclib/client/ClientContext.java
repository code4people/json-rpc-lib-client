package com.code4people.jsonrpclib.client;

import com.code4people.jsonrpclib.client.factories.ClientProxyFactory;
import com.code4people.jsonrpclib.client.messaging.MessageReceiver;
import com.code4people.jsonrpclib.client.messaging.MessageSender;
import com.code4people.jsonrpclib.client.processing.AsyncResponseProducer;

public class ClientContext implements AutoCloseable {

    private final MessageReceiver messageReceiver;
    private final ClientProxyFactory clientProxyFactory;
    private final AsyncResponseProducer asyncResponseProducer;
    private final MessageSender messageSender;

    ClientContext(MessageReceiver messageReceiver, ClientProxyFactory clientProxyFactory, AsyncResponseProducer asyncResponseProducer, MessageSender messageSender) {
        this.messageReceiver = messageReceiver;
        this.clientProxyFactory = clientProxyFactory;
        this.asyncResponseProducer = asyncResponseProducer;
        this.messageSender = messageSender;
    }

    public <T> T createProxyOf(Class<T> clazz) {
        return clientProxyFactory.create(clazz, asyncResponseProducer);
    }

    public MessageReceiver getMessageReceiver() {
        return messageReceiver;
    }

    @Override
    public void close() {
        messageSender.close();
        asyncResponseProducer.close();
    }
}
