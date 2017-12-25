package com.nullables.jsonrpclib.client.dispatch;

import org.junit.Test;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

public class InvocationHandlerImplTest {
    @Test
    public void invoke_shouldReturnCompletableFuture() throws Throwable {
        CompletableFuture<Object> expectedResult = new CompletableFuture<>();
        Method method = TestingInterface.class.getDeclaredMethod("methodAsync");
        MethodDispatcher methodDispatcher = Mockito.mock(MethodDispatcher.class);
        when(methodDispatcher.dispatch(method, new Object[1])).thenReturn(expectedResult);
        InvocationHandlerImpl invocationHandler = new InvocationHandlerImpl(methodDispatcher);

        @SuppressWarnings("unchecked")
        CompletableFuture<Object> result = (CompletableFuture<Object>) invocationHandler.invoke(null, method, new Object[1]);

        assertEquals(expectedResult, result);
    }

    @Test
    public void invoke_shouldReturnResult() throws Throwable {
        Object expectedResult = new Object();
        CompletableFuture<Object> cf = new CompletableFuture<>();
        cf.complete(expectedResult);
        Method method = TestingInterface.class.getDeclaredMethod("method");
        MethodDispatcher methodDispatcher = Mockito.mock(MethodDispatcher.class);
        when(methodDispatcher.dispatch(method, new Object[1])).thenReturn(cf);
        InvocationHandlerImpl invocationHandler = new InvocationHandlerImpl(methodDispatcher);

        Object result = invocationHandler.invoke(null, method, new Object[1]);

        assertEquals(expectedResult, result);
    }

    @Test
    public void invoke_shouldThrow_whenExceptionWasThrown() throws Throwable {
        RuntimeException expectedException = new RuntimeException();
        CompletableFuture<Object> cf = new CompletableFuture<>();
        cf.completeExceptionally(expectedException);
        Method method = TestingInterface.class.getDeclaredMethod("method");
        MethodDispatcher methodDispatcher = Mockito.mock(MethodDispatcher.class);
        when(methodDispatcher.dispatch(method, new Object[1])).thenReturn(cf);
        InvocationHandlerImpl invocationHandler = new InvocationHandlerImpl(methodDispatcher);

        try {
            invocationHandler.invoke(null, method, new Object[1]);
        }
        catch (RuntimeException e) {
            assertEquals(expectedException, e);
        }
    }

    interface TestingInterface {
        CompletableFuture<Object> methodAsync();
        Object method();
    }

}