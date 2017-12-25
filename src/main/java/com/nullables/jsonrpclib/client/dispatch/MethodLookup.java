package com.nullables.jsonrpclib.client.dispatch;

import java.util.Map;
import java.util.Optional;

public class MethodLookup {
    private final Map<java.lang.reflect.Method, Method> clientMethodMap;

    public MethodLookup(Map<java.lang.reflect.Method, Method> clientMethodMap) {
        this.clientMethodMap = clientMethodMap;
    }

    public Optional<Method> lookup(java.lang.reflect.Method method) {
        if (clientMethodMap.containsKey(method)) {
            return Optional.of(clientMethodMap.get(method));
        }
        return Optional.empty();
    }
}
