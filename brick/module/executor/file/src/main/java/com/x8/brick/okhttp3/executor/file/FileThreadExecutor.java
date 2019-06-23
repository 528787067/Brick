package com.x8.brick.okhttp3.executor.file;

import com.x8.brick.okhttp3.HttpRequest;
import com.x8.brick.okhttp3.HttpResponse;

import okhttp3.MediaType;

public class FileThreadExecutor<T> extends FileExecutor<T> {

    public FileThreadExecutor(String host, String directoryMapper, MediaType mediaType) {
        super(host, directoryMapper, mediaType);
    }

    @Override
    public void asyncExecute(final HttpRequest request, final Callback<HttpRequest, HttpResponse, T> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                onAsyncExecute(request, callback);
            }
        }).start();
    }
}
