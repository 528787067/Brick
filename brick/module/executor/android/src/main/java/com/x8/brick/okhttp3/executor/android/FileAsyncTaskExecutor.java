package com.x8.brick.okhttp3.executor.android;

import com.x8.brick.exception.HttpException;
import com.x8.brick.executor.async.AsyncTaskExecutor;
import com.x8.brick.okhttp3.HttpRequest;
import com.x8.brick.okhttp3.HttpResponse;
import com.x8.brick.okhttp3.executor.file.FileExecutor;

import okhttp3.MediaType;

public class FileAsyncTaskExecutor<T> extends FileExecutor<T> {

    private AsyncTaskExecutor<HttpRequest, HttpResponse, T> executorProxy;

    public FileAsyncTaskExecutor(String host, String directoryMapper, MediaType mediaType) {
        super(host, directoryMapper, mediaType);
        executorProxy = new AsyncTaskExecutor<HttpRequest, HttpResponse, T>() {
            @Override
            public HttpResponse execute(HttpRequest request) throws HttpException {
                return FileAsyncTaskExecutor.this.execute(request);
            }
            @Override
            public boolean isExecuted() {
                return FileAsyncTaskExecutor.this.isExecuted();
            }
        };
    }

    @Override
    public void asyncExecute(HttpRequest request, Callback<HttpRequest, HttpResponse, T> callback) {
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
