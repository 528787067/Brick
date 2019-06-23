package com.x8.brick.okhttp3.executor.android;

import android.content.Context;
import android.support.annotation.NonNull;

import com.x8.brick.okhttp3.HttpRequest;
import com.x8.brick.okhttp3.HttpResponse;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import okhttp3.MediaType;

public class AssetThreadPoolExecutor<T> extends AssetExecutor<T> {

    private ExecutorService executorService;
    private volatile Future<?> future;

    public AssetThreadPoolExecutor(@NonNull Context context, String host,
            String directoryMapper, MediaType mediaType, ExecutorService executorService) {
        super(context, host, directoryMapper, mediaType);
        if (executorService == null) {
            executorService = Executors.newCachedThreadPool();
        }
        this.executorService = executorService;
    }

    @Override
    public void asyncExecute(final HttpRequest request, final Callback<HttpRequest, HttpResponse, T> callback) {
        if (future != null) {
            throw new IllegalStateException("Already Executed");
        }
        future = executorService.submit(new Runnable() {
            @Override
            public void run() {
                onAsyncExecute(request, callback);
            }
        });
    }

    @Override
    public void cancel() {
        super.cancel();
        if (future != null) {
            future.cancel(true);
        }
    }

    @Override
    public boolean isCanceled() {
        return super.isCanceled() || (future != null && future.isCancelled());
    }
}
