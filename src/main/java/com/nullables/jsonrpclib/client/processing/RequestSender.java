package com.nullables.jsonrpclib.client.processing;

import com.nullables.jsonrpclib.client.exceptions.ClientException;
import com.nullables.jsonrpclib.client.exceptions.SerializationException;
import com.nullables.jsonrpclib.client.messaging.MessageSender;
import com.nullables.jsonrpclib.client.model.Request;
import com.nullables.jsonrpclib.client.serialization.MessageSerializer;

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
