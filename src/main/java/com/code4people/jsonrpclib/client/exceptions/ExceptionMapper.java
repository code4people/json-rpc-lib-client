package com.code4people.jsonrpclib.client.exceptions;

import com.code4people.jsonrpclib.client.model.ResponseError;

import java.util.Map;
import java.util.function.Function;

public class ExceptionMapper {
    private final Map<Integer, Function<ResponseError, ? extends Throwable>> exceptionMap;

    public ExceptionMapper(Map<Integer, Function<ResponseError, ? extends Throwable>> exceptionMap) {
        this.exceptionMap = exceptionMap;
    }

    public Throwable resolveException(ResponseError responseError) {
        int code = responseError.getCode();
        if (!exceptionMap.containsKey(code)) {
            return new ServerException(
                    responseError.getCode(),
                    responseError.getMessage(),
                    responseError.getData(),
                    responseError.getData());
        }
        return exceptionMap.get(code).apply(responseError);
    }
}
