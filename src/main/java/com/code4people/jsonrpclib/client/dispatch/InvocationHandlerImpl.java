package com.code4people.jsonrpclib.client.dispatch;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class InvocationHandlerImpl implements InvocationHandler {

    private final MethodDispatcher methodDispatcher;

    public InvocationHandlerImpl(MethodDispatcher methodDispatcher) {
        this.methodDispatcher = methodDispatcher;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        CompletableFuture<Object> cf = methodDispatcher.dispatch(method, args);

        if (CompletableFuture.class.isAssignableFrom(method.getReturnType())) {
            return cf;
        }

        try {
            return cf.join();
        }
        catch (CompletionException e) {
            if (e.getCause() != null) {
                throw e.getCause();
            }

            throw e;
        }
    }
}
