package com.code4people.jsonrpclib.client.processing;

import com.code4people.jsonrpclib.client.exceptions.ClientException;
import com.code4people.jsonrpclib.client.model.Request;
import com.code4people.jsonrpclib.client.exceptions.SerializationException;
import com.code4people.jsonrpclib.client.messaging.MessageSender;
import com.code4people.jsonrpclib.client.serialization.MessageSerializer;

public class RequestSender {
    private final MessageSerializer messageSerializer;
    private final MessageSender messageSender;

    public RequestSender(MessageSerializer messageSerializer, MessageSender messageSender) {
        this.messageSerializer = messageSerializer;
        this.messageSender = messageSender;
    }

    public void send(Request request) {
        String message;
        try {
            message = messageSerializer.serializeToString(request);
        } catch (SerializationException e) {
            throw new ClientException("Cannot serialize request", e);
        }

        messageSender.send(message);
    }
}
