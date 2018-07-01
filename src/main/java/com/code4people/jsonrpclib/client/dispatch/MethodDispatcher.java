package com.code4people.jsonrpclib.client.dispatch;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class MethodDispatcher {
    private final MethodLookup methodLookup;

    public MethodDispatcher(MethodLookup methodLookup) {
        this.methodLookup = methodLookup;
    }

    public CompletableFuture<Object> dispatch(java.lang.reflect.Method method, Object[] args) throws Throwable {
        Optional<Method> clientMethodOptional = methodLookup.lookup(method);
        CompletableFuture<Object> cf = new CompletableFuture<>();
        if (!clientMethodOptional.isPresent()) {
            String message = String.format("Method '%s' was not found.", method);
            cf.completeExceptionally(new IllegalStateException(message));
        }
        else {
            clientMethodOptional.get().invoke(args)
                .whenComplete((o, throwable) -> {
                    if (throwable == null) {
                        cf.complete(o);
                    }
                    else if (throwable instanceof CompletionException && throwable.getCause() != null) {
                        cf.completeExceptionally(throwable.getCause());
                    }
                    else {
                        cf.completeExceptionally(throwable);
                    }
                });
        }

        return cf;
    }
}
