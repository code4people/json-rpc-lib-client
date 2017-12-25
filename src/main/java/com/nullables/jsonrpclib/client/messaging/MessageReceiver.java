package com.nullables.jsonrpclib.client.messaging;

import com.nullables.jsonrpclib.client.exceptions.ReceiveException;
import com.nullables.jsonrpclib.client.exceptions.SerializationException;
import com.nullables.jsonrpclib.client.model.Response;
import com.nullables.jsonrpclib.client.processing.ResponseReceiver;
import com.nullables.jsonrpclib.client.serialization.MessageSerializer;

public class MessageReceiver {
    private final MessageSerializer messageSerializer;
    private final ResponseReceiver responseReceiver;

    public MessageReceiver(MessageSerializer messageSerializer, ResponseReceiver responseReceiver) {
        this.messageSerializer = messageSerializer;
        this.responseReceiver = responseReceiver;
    }

    public void receive(String message) throws ReceiveException {
        Response response;
        try {
            response = messageSerializer.deserialize(message);
        } catch (SerializationException e) {
            throw new ReceiveException("Cannot deserialize message.", e);
        }

        if (!responseReceiver.receive(response)) {
            throw new ReceiveException(
                    "Unable to process response. Possible reasons:\n" +
                            " - response has unknown correlation id\n" +
                            " - response was already processed before\n" +
                            " - remote invocation expired.");
        }
    }
}
