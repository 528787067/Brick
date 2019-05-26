package com.x8.brick.okhttp3.executor.android;

import com.x8.brick.exception.HttpException;
import com.x8.brick.executor.async.AsyncTaskExecutor;
import com.x8.brick.okhttp3.OkHttp3Request;
import com.x8.brick.okhttp3.OkHttp3Response;
import com.x8.brick.okhttp3.executor.file.OkHttp3FileExecutor;

import okhttp3.MediaType;

public class OkHttp3FileAsyncTaskExecutor<T> extends OkHttp3FileExecutor<T> {

    private AsyncTaskExecutor<OkHttp3Request, OkHttp3Response, T> executorProxy;

    public OkHttp3FileAsyncTaskExecutor(String host, String indexMapper, MediaType mediaType) {
        super(host, indexMapper, mediaType);
        executorProxy = new AsyncTaskExecutor<OkHttp3Request, OkHttp3Response, T>() {
            @Override
            public OkHttp3Response execute(OkHttp3Request request) throws HttpException {
                return OkHttp3FileAsyncTaskExecutor.this.execute(request);
            }
            @Override
            public boolean isExecuted() {
                return OkHttp3FileAsyncTaskExecutor.this.isExecuted();
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
