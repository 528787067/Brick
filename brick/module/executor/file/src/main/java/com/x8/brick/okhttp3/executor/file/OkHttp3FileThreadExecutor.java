package com.x8.brick.okhttp3.executor.file;

import com.x8.brick.okhttp3.OkHttp3Request;
import com.x8.brick.okhttp3.OkHttp3Response;

import okhttp3.MediaType;

public class OkHttp3FileThreadExecutor<T> extends OkHttp3FileExecutor<T> {

    public OkHttp3FileThreadExecutor(String host, String directoryMapper, MediaType mediaType) {
        super(host, directoryMapper, mediaType);
    }

    @Override
    public void asyncExecute(final OkHttp3Request request,
            final Callback<OkHttp3Request, OkHttp3Response, T> callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                onAsyncExecute(request, callback);
            }
        }).start();
    }
}
