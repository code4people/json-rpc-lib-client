package com.code4people.jsonrpclib.client.messaging;

import java.io.Closeable;

public interface MessageSender extends Closeable {
    void send(String message);
    void close();
}
