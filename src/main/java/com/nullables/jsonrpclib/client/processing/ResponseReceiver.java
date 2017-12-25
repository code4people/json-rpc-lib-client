package com.nullables.jsonrpclib.client.processing;

import com.nullables.jsonrpclib.client.model.Response;

public interface ResponseReceiver {
    boolean receive(Response response);
}
