package com.nullables.jsonrpclib.client;

import com.fasterxml.jackson.databind.node.TextNode;
import com.pushpopsoft.jsonrpclib.binding.annotations.Bind;
import com.pushpopsoft.jsonrpclib.binding.annotations.Param;
import com.pushpopsoft.jsonrpclib.binding.annotations.ParamsType;
import com.pushpopsoft.jsonrpclib.client.exceptions.ClientException;
import com.pushpopsoft.jsonrpclib.client.exceptions.ReceiveException;
import com.pushpopsoft.jsonrpclib.client.exceptions.ServerException;
import com.pushpopsoft.jsonrpclib.client.messaging.MessageReceiver;
import org.junit.Test;

import java.time.Duration;
import java.util.concurrent.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class IntegrationTest {

    @Test
    public void proxyCallAsync_shouldReturnResult_whenCalledWithNamedParams() {
        ClientContextBuilder builder = new ClientContextBuilder();
        builder.messageSender(m -> {
            assertEquals("{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"method\":\"methodWithNamedParamsAsync\",\"params\":{\"param\":\"value\"}}", m);
        });
        builder.idGenerator(() -> "1");
        ClientContext clientContext = builder.build();
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
        ClientContextBuilder builder = new ClientContextBuilder();
        builder.messageSender(m -> {
            assertEquals("{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"method\":\"methodWithPositionalParamsAsync\",\"params\":[\"value\"]}", m);
        });
        builder.idGenerator(() -> "1");
        ClientContext clientContext = builder.build();
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
    public void proxyCall_shouldReturnResult() {
        ClientContextBuilder builder = new ClientContextBuilder();
        builder.messageSender(m -> { });
        builder.idGenerator(() -> "1");
        ClientContext clientContext = builder.build();
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
    public void proxyCallAsync_shouldThrowTimeout_whenResponseIsNotReceived() throws InterruptedException {
        ClientContextBuilder builder = new ClientContextBuilder()
            .messageSender(m -> { })
            .timeout(Duration.ofMillis(50));
        ClientContext clientContext = builder.build();
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
        ClientContextBuilder builder = new ClientContextBuilder()
                .messageSender(m -> { })
                .timeout(Duration.ofMillis(50));
        ClientContext clientContext = builder.build();
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
        ClientContextBuilder builder = new ClientContextBuilder()
                .messageSender(m -> { });
        ClientContext clientContext = builder.build();
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
        ClientContextBuilder builder = new ClientContextBuilder();
        builder.messageSender(m -> { });
        builder.idGenerator(() -> "1");
        ClientContext clientContext = builder.build();
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
        ClientContextBuilder builder = new ClientContextBuilder();
        builder.messageSender(m -> { });
        builder.idGenerator(() -> "1");
        ClientContext clientContext = builder.build();
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
    public void proxyCallAsync_shouldThrow_whenResponseResultFailsToDeserialize() {
        ClientContextBuilder builder = new ClientContextBuilder();
        builder.messageSender(m -> { });
        builder.idGenerator(() -> "1");
        ClientContext clientContext = builder.build();
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
        ClientContextBuilder builder = new ClientContextBuilder();
        builder.messageSender(m -> { });
        ClientContext clientContext = builder.build();
        MessageReceiver messageReceiver = clientContext.getMessageReceiver();

        messageReceiver.receive("invalid json []][");
    }

    @Test(expected = ReceiveException.class)
    public void messageReceiver_shouldThrow_whenResponseHAsUnknownCorrelationId() throws ReceiveException {
        ClientContextBuilder builder = new ClientContextBuilder();
        builder.messageSender(m -> { });
        ClientContext clientContext = builder.build();
        MessageReceiver messageReceiver = clientContext.getMessageReceiver();

        messageReceiver.receive("{\"jsonrpc\":\"2.0\",\"id\":\"1\",\"result\":{\"field\":\"result value\"}}");
    }

    @Test
    public void close_shouldCancelPendingInvocations() throws InterruptedException {
        ClientContextBuilder builder = new ClientContextBuilder();
        builder.messageSender(m -> { });
        ClientContext clientContext = builder.build();
        Contract proxy = clientContext.createProxyOf(Contract.class);

        CompletableFuture<String> result = proxy.methodWithPositionalParamsAsync("value");

        clientContext.close();

        assertTrue(result.isCancelled());
    }

    public interface Contract {
        @Bind(paramsTypes = ParamsType.POSITIONAL)
        String method(String param);

        @Bind(paramsTypes = ParamsType.POSITIONAL)
        CompletableFuture<String> methodWithPositionalParamsAsync(String param);

        @Bind(paramsTypes = ParamsType.NAMED)
        CompletableFuture<String> methodWithNamedParamsAsync(@Param("param") String param);

        CompletableFuture<String> notBoundMethodAsync(String param);
    }
}
