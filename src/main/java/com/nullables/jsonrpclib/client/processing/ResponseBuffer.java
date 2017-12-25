package com.nullables.jsonrpclib.client.processing;

import com.nullables.jsonrpclib.client.exceptions.ClientException;
import com.nullables.jsonrpclib.client.model.Response;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.BiConsumer;

public class ResponseBuffer implements AsyncResponseProducer, ResponseReceiver {
    private final Map<Object, CompletableFuture<Response>> futureMap = new ConcurrentHashMap<>();
    private final Duration timeout;
    private volatile boolean isClosed;

    public ResponseBuffer(Duration timeout) {
        this.timeout = timeout;
    }

    @Override
    public boolean receive(Response response) {

        CompletableFuture<Response> future = futureMap.get(response.getId());
        return future != null && future.complete(response);
    }

    @Override
    public CompletableFuture<Response> produceAsyncResponse(Object id) {
        if (isClosed) {
            CompletableFuture<Response> failedCf = new CompletableFuture<>();
            failedCf.completeExceptionally(new IllegalStateException("ResponseBuffer already closed"));
            return failedCf;
        }

        CompletableFuture<Response> responseCf = futureMap.computeIfAbsent(
                id,
                (key) -> {
                    CompletableFuture<Response> cf = new CompletableFuture<>();
                    cf.whenComplete(new Canceller(Delayer.delay(new Timeout(cf), timeout.toMillis(), TimeUnit.MILLISECONDS)));
                    return cf.whenComplete((response, throwable) -> futureMap.remove(key));
                });

        if (isClosed) {
            responseCf.cancel(true);
        }

        return responseCf;
    }

    public void close() {
        if (isClosed) {
            throw new IllegalStateException("ResponseBuffer already closed");
        }

        isClosed = true;
        futureMap.values().forEach(x -> x.cancel(true));
    }

    static final class Timeout implements Runnable {
        final CompletableFuture<?> f;
        Timeout(CompletableFuture<?> f) { this.f = f; }
        public void run() {
            if (f != null && !f.isDone())
                f.completeExceptionally(new ClientException("The invocation expired.", new TimeoutException()));
        }
    }

    static final class Canceller implements BiConsumer<Object, Throwable> {
        final Future<?> f;
        Canceller(Future<?> f) { this.f = f; }
        public void accept(Object ignore, Throwable ex) {
            if (ex == null && f != null && !f.isDone())
                f.cancel(false);
        }
    }

    static final class Delayer {
        static ScheduledFuture<?> delay(Runnable command, long delay,
                                        TimeUnit unit) {
            return delayer.schedule(command, delay, unit);
        }

        static final class DaemonThreadFactory implements ThreadFactory {
            public Thread newThread(Runnable r) {
                Thread t = new Thread(r);
                t.setDaemon(true);
                t.setName("CompletableFutureDelayScheduler");
                return t;
            }
        }

        static final ScheduledThreadPoolExecutor delayer;
        static {
            (delayer = new ScheduledThreadPoolExecutor(
                    1, new Delayer.DaemonThreadFactory())).
                    setRemoveOnCancelPolicy(true);
        }
    }
}
