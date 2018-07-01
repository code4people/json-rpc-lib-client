package com.code4people.jsonrpclib.client.factories;

import com.code4people.jsonrpclib.client.dispatch.Client;
import com.code4people.jsonrpclib.client.dispatch.Method;
import com.code4people.jsonrpclib.client.exceptions.ExceptionMapper;
import com.code4people.jsonrpclib.client.model.ResponseError;
import com.code4people.jsonrpclib.client.processing.AsyncResponseProducer;
import com.code4people.jsonrpclib.client.processing.RequestSender;
import com.code4people.jsonrpclib.client.serialization.ParamsSerializer;
import com.code4people.jsonrpclib.client.serialization.ResultDeserializer;
import com.code4people.jsonrpclib.binding.info.GranularParamsMethodInfo;
import com.code4people.jsonrpclib.binding.info.MethodInfo;
import com.code4people.jsonrpclib.binding.info.SingleArgumentMethodInfo;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MethodFactory {
    private final RequestSender requestSender;
    private final ParamsSerializerFactory paramsSerializerFactory;
    private final ResultDeserializerFactory resultDeserializerFactory;
    private final Supplier<? extends Object> idSupplier;
    private final Map<Class<? extends Throwable>, Function<ResponseError, ? extends Throwable>> exceptionFactories;

    public MethodFactory(RequestSender requestSender,
                         ParamsSerializerFactory paramsSerializerFactory,
                         ResultDeserializerFactory resultDeserializerFactory,
                         Supplier<? extends Object> idSupplier,
                         Map<Class<? extends Throwable>, Function<ResponseError, ? extends Throwable>> exceptionFactories) {
        this.requestSender = requestSender;
        this.paramsSerializerFactory = paramsSerializerFactory;
        this.resultDeserializerFactory = resultDeserializerFactory;
        this.idSupplier = idSupplier;
        this.exceptionFactories = exceptionFactories;
    }

    public Method createMethod(MethodInfo methodInfo, AsyncResponseProducer asyncResponseProducer) {
        Type returnType = methodInfo.getMethod().getGenericReturnType();
        if (CompletableFuture.class.isAssignableFrom(methodInfo.getMethod().getReturnType())
                && returnType instanceof ParameterizedType) {
            ParameterizedType parameterizedReturnType = (ParameterizedType) returnType;
            returnType = parameterizedReturnType.getActualTypeArguments()[0];
        }

        Set<String> missingExceptionFactories = methodInfo.getErrorInfos()
                .entrySet()
                .stream()
                .filter(x -> !exceptionFactories.containsKey(x.getKey()))
                .map(x -> x.getKey().getName())
                .collect(Collectors.toSet());

        if (!missingExceptionFactories.isEmpty()) {
            throw new IllegalStateException("Missing exception factories for following exception types: " + missingExceptionFactories);
        }

        Map<Integer, Function<ResponseError, ? extends Throwable>> exceptionMap = methodInfo.getErrorInfos()
                .entrySet()
                .stream()
                .collect(Collectors.toMap(o -> o.getValue().code, o -> exceptionFactories.get(o.getKey())));
        ExceptionMapper exceptionMapper = new ExceptionMapper(exceptionMap);

        ParamsSerializer paramsSerializer = getParamsSerializer(methodInfo);
        ResultDeserializer resultDeserializer = resultDeserializerFactory.create();
        Client client = new Client(requestSender, asyncResponseProducer);
        return new Method(methodInfo.getPublicName(),
                returnType,
                paramsSerializer,
                resultDeserializer,
                client,
                exceptionMapper,
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
