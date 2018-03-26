package com.code4people.jsonrpclib.client.factories;

import com.code4people.jsonrpclib.binding.factories.GranularParamsMethodInfoFactory;
import com.code4people.jsonrpclib.binding.factories.MethodInfoFactory;
import com.code4people.jsonrpclib.binding.factories.SingleArgumentMethodInfoFactory;
import com.code4people.jsonrpclib.client.dispatch.MethodDispatcher;
import com.code4people.jsonrpclib.binding.info.MethodInfo;
import com.code4people.jsonrpclib.client.dispatch.InvocationHandlerImpl;
import com.code4people.jsonrpclib.client.processing.AsyncResponseProducer;

import java.lang.reflect.Proxy;
import java.util.List;

public class ClientProxyFactory {

    private final MethodDispatcherFactory methodDispatcherFactory;

    public ClientProxyFactory(MethodDispatcherFactory methodDispatcherFactory) {
        this.methodDispatcherFactory = methodDispatcherFactory;
    }

    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> clazz, AsyncResponseProducer asyncResponseProducer) {
        GranularParamsMethodInfoFactory granularParamsMethodInfoFactory = new GranularParamsMethodInfoFactory();
        SingleArgumentMethodInfoFactory singleArgumentMethodInfoFactory = new SingleArgumentMethodInfoFactory();
        MethodInfoFactory methodInfoFactory = new MethodInfoFactory(granularParamsMethodInfoFactory, singleArgumentMethodInfoFactory);
        List<? extends MethodInfo> methodInfos = methodInfoFactory.createFromClass(clazz);
        MethodDispatcher methodDispatcher = methodDispatcherFactory.createMethodDispatcher(methodInfos, asyncResponseProducer);
        InvocationHandlerImpl invocationHandler = new InvocationHandlerImpl(methodDispatcher);

        return (T) Proxy.newProxyInstance(
                clazz.getClassLoader(),
                new Class<?>[] { clazz },
                invocationHandler);
    }
}
