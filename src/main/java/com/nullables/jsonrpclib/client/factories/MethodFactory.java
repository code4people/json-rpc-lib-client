package com.nullables.jsonrpclib.client.factories;

import com.nullables.jsonrpclib.client.dispatch.Client;
import com.nullables.jsonrpclib.client.dispatch.Method;
import com.nullables.jsonrpclib.client.processing.AsyncResponseProducer;
import com.nullables.jsonrpclib.client.processing.RequestSender;
import com.nullables.jsonrpclib.client.serialization.ParamsSerializer;
import com.nullables.jsonrpclib.client.serialization.ResultDeserializer;
import com.pushpopsoft.jsonrpclib.binding.info.GranularParamsMethodInfo;
import com.pushpopsoft.jsonrpclib.binding.info.MethodInfo;
import com.pushpopsoft.jsonrpclib.binding.info.SingleArgumentMethodInfo;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class MethodFactory {
    private final RequestSender requestSender;
    private final ParamsSerializerFactory paramsSerializerFactory;
    private final ResultDeserializerFactory resultDeserializerFactory;
    private final Supplier<? extends Object> idSupplier;

    public MethodFactory(RequestSender requestSender,
                         ParamsSerializerFactory paramsSerializerFactory,
                         ResultDeserializerFactory resultDeserializerFactory,
                         Supplier<? extends Object> idSupplier) {
        this.requestSender = requestSender;
        this.paramsSerializerFactory = paramsSerializerFactory;
        this.resultDeserializerFactory = resultDeserializerFactory;
        this.idSupplier = idSupplier;
    }

    public Method createMethod(MethodInfo methodInfo, AsyncResponseProducer asyncResponseProducer) {
        Type returnType = methodInfo.getMethod().getGenericReturnType();
        if (CompletableFuture.class.isAssignableFrom(methodInfo.getMethod().getReturnType())
                && returnType instanceof ParameterizedType) {
            ParameterizedType parameterizedReturnType = (ParameterizedType) returnType;
            returnType = parameterizedReturnType.getActualTypeArguments()[0];
        }

        ParamsSerializer paramsSerializer = getParamsSerializer(methodInfo);
        ResultDeserializer resultDeserializer = resultDeserializerFactory.create();
        Client client = new Client(requestSender, asyncResponseProducer);
        return new Method(methodInfo.getPublicName(),
                returnType,
                paramsSerializer,
                resultDeserializer,
                client,
                idSupplier);
    }

    private ParamsSerializer getParamsSerializer(MethodInfo methodInfo) {
        ParamsSerializer paramsSerializer;
        if (methodInfo instanceof GranularParamsMethodInfo) {
            GranularParamsMethodInfo granularParamsMethodInfo = (GranularParamsMethodInfo) methodInfo;
            if (granularParamsMethodInfo.getPositionalParamsInfo() != null) {
                paramsSerializer = paramsSerializerFactory.createPositionalParamsSerializer();
            }
            else if (granularParamsMethodInfo.getNamedParamsInfo() != null) {
                paramsSerializer = paramsSerializerFactory.createNamedParamsSerializer(granularParamsMethodInfo.getNamedParamsInfo());
            }
            else if (granularParamsMethodInfo.getMissingParamsInfo() != null) {
                paramsSerializer = paramsSerializerFactory.createMissingParamsSerializer();
            }
            else {
                throw new IllegalArgumentException("");
            }
        }
        else if (methodInfo instanceof SingleArgumentMethodInfo) {
            paramsSerializer = paramsSerializerFactory.createSingleArgumentParamsSerializer();
        }
        else {
            throw new IllegalArgumentException("");
        }
        return paramsSerializer;
    }
}
