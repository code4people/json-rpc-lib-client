package com.code4people.jsonrpclib.client;

import com.code4people.jsonrpclib.client.model.ResponseError;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Supplier;

public class ClientContextFactoryBuilder {
    private Duration timeout = Duration.ofSeconds(30);
    private Supplier<? extends Object> idGenerator = () -> UUID.randomUUID().toString();
    private Map<Class<? extends Throwable>, Function<ResponseError, ? extends Throwable>> exceptionFactories = new HashMap<>();

    public ClientContextFactoryBuilder idGenerator(Supplier<String> idGenerator) {
        Objects.requireNonNull(idGenerator, "'idGenerator' cannot be null");
        this.idGenerator = idGenerator;
        return this;
    }

    public ClientContextFactoryBuilder timeout(Duration timeout) {
        Objects.requireNonNull(idGenerator, "'timeout' cannot be null");
        this.timeout = timeout;
        return this;
    }

    public <T extends Throwable> ClientContextFactoryBuilder addExceptionFactory(Class<T> throwableClass, Function<ResponseError, T> factory) {
        Objects.requireNonNull(throwableClass, "'throwableClass' cannot be null");
        Objects.requireNonNull(factory, "'factory' cannot be null");
        exceptionFactories.put(throwableClass, factory);
        return this;
    }

    public ClientContextFactory build() {
        return new ClientContextFactory(timeout, idGenerator, exceptionFactories);
    }

}
