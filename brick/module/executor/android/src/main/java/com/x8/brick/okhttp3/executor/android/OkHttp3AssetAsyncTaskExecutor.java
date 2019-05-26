package com.x8.brick.okhttp3.executor.android;

import android.content.Context;
import android.support.annotation.NonNull;

import com.x8.brick.exception.HttpException;
import com.x8.brick.executor.async.AsyncTaskExecutor;
import com.x8.brick.okhttp3.OkHttp3Request;
import com.x8.brick.okhttp3.OkHttp3Response;

import okhttp3.MediaType;

public class OkHttp3AssetAsyncTaskExecutor<T> extends OkHttp3AssetExecutor<T> {

    private AsyncTaskExecutor<OkHttp3Request, OkHttp3Response, T> executorProxy;

    public OkHttp3AssetAsyncTaskExecutor(@NonNull Context context,
            String host, String directoryMapper, MediaType mediaType) {
        super(context, host, directoryMapper, mediaType);
        executorProxy = new AsyncTaskExecutor<OkHttp3Request, OkHttp3Response, T>() {
            @Override
            public OkHttp3Response execute(OkHttp3Request request) throws HttpException {
                return OkHttp3AssetAsyncTaskExecutor.this.execute(request);
            }
            @Override
            public boolean isExecuted() {
                return OkHttp3AssetAsyncTaskExecutor.this.isExecuted();
            }
        };
    }

    @Override
    public void asyncExecute(OkHttp3Request request, Callback<OkHttp3Request, OkHttp3Response, T> callback) {
        executorProxy.asyncExecute(request, callback);
    }

    @Override
    public void cancel() {
        super.cancel();
        executorProxy.cancel();
    }

    @Override
    public boolean isCanceled() {
        return super.isCanceled() || executorProxy.isCanceled();
    }
}
