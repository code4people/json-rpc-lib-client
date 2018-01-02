package com.code4people.jsonrpclib.client.processing;

import com.code4people.jsonrpclib.client.model.Response;

public interface ResponseReceiver {
    boolean receive(Response response);
}
