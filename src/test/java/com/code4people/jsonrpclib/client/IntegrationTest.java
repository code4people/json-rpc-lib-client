package com.code4people.jsonrpclib.client;

import com.code4people.jsonrpclib.binding.annotations.*;
import com.code4people.jsonrpclib.binding.annotations.Error;
import com.code4people.jsonrpclib.client.exceptions.ClientException;
import com.fasterxml.jackson.databind.node.TextNode;
import com.code4people.jsonrpclib.client.exceptions.ReceiveException;
import com.code4people.jsonrpclib.client.exceptions.ServerException;
import com.code4people.jsonrpclib.client.messaging.MessageReceiver;
import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class IntegrationTest {
    @Test
    public void proxyCallAsync_shouldReturnResult_whenCalledWithNamedParams() {
        ClientContext clientContext = new ClientContextFactoryBuilder()
            .idGenerator(() -> "1")
            .build()
            .create(m -> {
                assertEquals("{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"method\":\"methodWithNamedParamsAsync\",\"params\":{\"param\":\"value\"}}", m);
            });
        Contract proxy = clientContext.createProxyOf(Contract.class);
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                Thread.sleep(10);
                clientContext.getMessageReceiver().receive("{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"result\":\"result value\"}");
            } catch (Exception ignored) { }
        });

        CompletableFuture<String> result = proxy.methodWithNamedParamsAsync("value");

        assertEquals("result value", result.join());
    }

    @Test
    public void proxyCallAsync_shouldReturnResult_whenCalledWithPositionalParams() {
        ClientContext clientContext = new ClientContextFactoryBuilder()
                .idGenerator(() -> "1")
                .build()
                .create(m -> {
                    assertEquals("{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"method\":\"methodWithPositionalParamsAsync\",\"params\":[\"value\"]}", m);
                });
        Contract proxy = clientContext.createProxyOf(Contract.class);
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                Thread.sleep(10);
                clientContext.getMessageReceiver().receive("{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"result\":\"result value\"}");
            } catch (Exception ignored) { }
        });

        CompletableFuture<String> result = proxy.methodWithPositionalParamsAsync("value");

        assertEquals("result value", result.join());
    }

    @Test
    public void proxyCallAsync_shouldReturnVoid_whenCallingMethodReturningVoid() {
        ClientContext clientContext = new ClientContextFactoryBuilder()
                .idGenerator(() -> "1")
                .build()
                .create(m -> {
                    assertEquals("{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"method\":\"voidMethodAsync\",\"params\":[\"value\"]}", m);
                });
        Contract proxy = clientContext.createProxyOf(Contract.class);
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                Thread.sleep(10);
                clientContext.getMessageReceiver().receive("{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"result\":\"result value\"}");
            } catch (Exception ignored) { }
        });

        CompletableFuture<Void> result = proxy.voidMethodAsync("value");

        assertEquals(null, result.join());
    }

    @Test
    public void proxyCallAsync_shouldReturnResult_whenCalledWithMissingParams() {
        ClientContext clientContext = new ClientContextFactoryBuilder()
                .idGenerator(() -> "1")
                .build()
                .create(m -> {
                    assertEquals("{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"method\":\"methodWithMissingParamsAsync\"}", m);
                });
        Contract proxy = clientContext.createProxyOf(Contract.class);
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                Thread.sleep(10);
                clientContext.getMessageReceiver().receive("{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"result\":\"result value\"}");
            } catch (Exception ignored) { }
        });

        CompletableFuture<String> result = proxy.methodWithMissingParamsAsync();

        assertEquals("result value", result.join());
    }


    @Test
    public void proxyCall_shouldReturnResult() {
        ClientContext clientContext = new ClientContextFactoryBuilder()
                .idGenerator(() -> "1")
                .build()
                .create(m -> {});
        Contract proxy = clientContext.createProxyOf(Contract.class);
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                Thread.sleep(100);
                clientContext.getMessageReceiver().receive("{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"result\":\"result value\"}");
            } catch (Exception ignored) { }
        });

        String result = proxy.method("value");

        assertEquals("result value", result);
    }

    @Test
    public void proxyCall_shouldReturnVoid() {
        ClientContext clientContext = new ClientContextFactoryBuilder()
                .idGenerator(() -> "1")
                .build()
                .create(m -> {});
        Contract proxy = clientContext.createProxyOf(Contract.class);
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                Thread.sleep(100);
                clientContext.getMessageReceiver().receive("{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"result\":\"result value\"}");
            } catch (Exception ignored) { }
        });

        proxy.voidMethod("value");
    }

    @Test
    public void proxyCallAsync_shouldThrowTimeout_whenResponseIsNotReceived() throws InterruptedException {
        ClientContext clientContext = new ClientContextFactoryBuilder()
                .timeout(Duration.ofMillis(50))
                .build()
                .create(m -> {});
        Contract proxy = clientContext.createProxyOf(Contract.class);

        proxy.methodWithPositionalParamsAsync("value")
                .exceptionally(throwable -> {
                    assertEquals(ClientException.class, throwable.getClass());
                    assertTrue(throwable.getCause() instanceof TimeoutException);
                    return null;
                })
                .join();
    }

    @Test
    public void proxyCall_shouldThrowTimeout_whenResponseIsNotReceived() throws InterruptedException {
        ClientContext clientContext = new ClientContextFactoryBuilder()
                .timeout(Duration.ofMillis(50))
                .build()
                .create(m -> {});
        Contract proxy = clientContext.createProxyOf(Contract.class);

        try {
            proxy.method("value");
        }
        catch (ClientException e) {
            assertNotNull(e.getMessage());
            assertTrue(e.getCause() instanceof TimeoutException);
        }
    }

    @Test
    public void proxyCallAsync_shouldThrow_whenMethodHasNoBinding() throws InterruptedException {
        ClientContext clientContext = new ClientContextFactoryBuilder()
                .build()
                .create(m -> {});
        Contract proxy = clientContext.createProxyOf(Contract.class);

        proxy.notBoundMethodAsync("value")
                .exceptionally(throwable -> {
                    assertEquals(IllegalStateException.class, throwable.getClass());
                    return null;
                })
                .join();
    }

    @Test
    public void proxyCallAsync_shouldThrow_whenErrorResponseIsReceived() {
        ClientContext clientContext = new ClientContextFactoryBuilder()
                .idGenerator(() -> "1")
                .build()
                .create(m -> {});
        Contract proxy = clientContext.createProxyOf(Contract.class);
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                Thread.sleep(10);
                MessageReceiver messageReceiver = clientContext.getMessageReceiver();
                messageReceiver.receive("{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"error\":{\"code\":-32050,\"message\":\"Error message\",\"data\":\"data\"}}");
            } catch (Exception ignored) { }
        });

        proxy.methodWithPositionalParamsAsync("value")
            .exceptionally(throwable -> {
                ServerException e = (ServerException) throwable;
                assertEquals(-32050, e.getCode());
                assertEquals("Error message", e.getMessage());
                assertEquals(TextNode.valueOf("data"), e.getData());
                return null;
            })
            .join();
    }

    @Test
    public void proxyCall_shouldThrow_whenErrorResponseIsReceived() {
        ClientContext clientContext = new ClientContextFactoryBuilder()
                .idGenerator(() -> "1")
                .build()
                .create(m -> {});
        Contract proxy = clientContext.createProxyOf(Contract.class);
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                Thread.sleep(10);
                MessageReceiver messageReceiver = clientContext.getMessageReceiver();
                messageReceiver.receive("{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"error\":{\"code\":-32050,\"message\":\"Error message\",\"data\":\"data\"}}");
            } catch (Exception ignored) { }
        });

        try {
            proxy.method("value");
        }
        catch (ServerException e) {
            assertEquals(-32050, e.getCode());
            assertEquals("Error message", e.getMessage());
            assertEquals(TextNode.valueOf("data"), e.getData());
        }
    }

    @Test
    public void proxyCall_shouldThrowsCustomException_whenErrorResponseIsReceived() {
        ClientContext clientContext = new ClientContextFactoryBuilder()
                .idGenerator(() -> "1")
                .addExceptionFactory(CustomException.class, re -> new CustomException(re.getMessage()))
                .build()
                .create(m -> {});
        ContractThrowingException proxy = clientContext.createProxyOf(ContractThrowingException.class);
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                Thread.sleep(10);
                MessageReceiver messageReceiver = clientContext.getMessageReceiver();
                messageReceiver.receive("{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"error\":{\"code\":-32050,\"message\":\"Error message\",\"data\":\"data\"}}");
            } catch (Exception ignored) { }
        });

        try {
            proxy.methodThrowingCustomException("value");
        } catch (CustomException e) {
            assertEquals("Error message", e.getMessage());
        }
    }

    @Test
    public void proxyCallAsync_shouldThrow_whenResponseResultFailsToDeserialize() {
        ClientContext clientContext = new ClientContextFactoryBuilder()
                .idGenerator(() -> "1")
                .build()
                .create(m -> {});
        Contract proxy = clientContext.createProxyOf(Contract.class);
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                Thread.sleep(10);
                MessageReceiver messageReceiver = clientContext.getMessageReceiver();
                messageReceiver.receive("{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"result\":{\"field\":\"result value\"}}");
            } catch (Exception ignored) { }
        });


        proxy.methodWithPositionalParamsAsync("value")
                .exceptionally(throwable -> {
                    assertEquals(ClientException.class, throwable.getClass());
                    assertEquals("Cannot deserialize result.", throwable.getMessage());
                    return null;
                })
                .join();
    }

    @Test(expected = ReceiveException.class)
    public void messageReceiver_shouldThrow_whenResponseFailsToDeserialize() throws ReceiveException {
        ClientContext clientContext = new ClientContextFactoryBuilder()
                .build()
                .create(m -> {});
        MessageReceiver messageReceiver = clientContext.getMessageReceiver();

        messageReceiver.receive("invalid json []][");
    }

    @Test(expected = ReceiveException.class)
    public void messageReceiver_shouldThrow_whenResponseHAsUnknownCorrelationId() throws ReceiveException {
        ClientContext clientContext = new ClientContextFactoryBuilder()
                .build()
                .create(m -> {});
        MessageReceiver messageReceiver = clientContext.getMessageReceiver();

        messageReceiver.receive("{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"result\":{\"field\":\"result value\"}}");
    }

    @Test
    public void close_shouldCancelPendingInvocations() throws InterruptedException {
        ClientContext clientContext = new ClientContextFactoryBuilder()
                .build()
                .create(m -> {});
        Contract proxy = clientContext.createProxyOf(Contract.class);

        CompletableFuture<String> result = proxy.methodWithPositionalParamsAsync("value");

        clientContext.close();

        assertTrue(result.isCancelled());
    }

    public interface Contract {
        @Bind(paramsType = ParamsType.POSITIONAL)
        String method(String param);

        @Bind(paramsType = ParamsType.POSITIONAL)
        CompletableFuture<Void> voidMethodAsync(String param);

        @Bind(paramsType = ParamsType.POSITIONAL)
        void voidMethod(String param);

        @Bind(paramsType = ParamsType.POSITIONAL)
        CompletableFuture<String> methodWithPositionalParamsAsync(String param);

        @Bind(paramsType = ParamsType.NAMED)
        CompletableFuture<String> methodWithNamedParamsAsync(@Param("param") String param);

        @Bind
        CompletableFuture<String> methodWithMissingParamsAsync();

        CompletableFuture<String> notBoundMethodAsync(String param);
    }

    public interface ContractThrowingException {
        @Bind(paramsType = ParamsType.POSITIONAL)
        @ErrorMapping({
                @Error(code = -32050, exception = CustomException.class)
        })
        String methodThrowingCustomException(String param) throws CustomException;
    }

    public class CustomException extends Exception {
        public CustomException(String message) {
            super(message);
        }
    }
}
