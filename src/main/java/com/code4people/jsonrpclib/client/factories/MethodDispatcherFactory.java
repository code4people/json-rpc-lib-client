package com.code4people.jsonrpclib.client.factories;

import com.code4people.jsonrpclib.client.dispatch.MethodDispatcher;
import com.code4people.jsonrpclib.client.dispatch.Method;
import com.code4people.jsonrpclib.client.dispatch.MethodLookup;
import com.code4people.jsonrpclib.client.processing.AsyncResponseProducer;
import com.code4people.jsonrpclib.binding.info.MethodInfo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MethodDispatcherFactory {

    private final MethodFactory methodFactory;

    public MethodDispatcherFactory(MethodFactory methodFactory) {
        this.methodFactory = methodFactory;
    }

    public MethodDispatcher createMethodDispatcher(List<? extends MethodInfo> methodInfos, AsyncResponseProducer asyncResponseProducer) {
        Map<java.lang.reflect.Method, Method> methodMap = methodInfos
                .stream()
                .collect(Collectors.toMap(MethodInfo::getMethod, methodInfo -> methodFactory.createMethod(methodInfo, asyncResponseProducer)));
        MethodLookup methodLookup = new MethodLookup(methodMap);
        return new MethodDispatcher(methodLookup);
    }
}
