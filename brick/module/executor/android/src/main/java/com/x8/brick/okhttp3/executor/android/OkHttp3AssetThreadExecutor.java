package com.x8.brick.okhttp3.executor.android;

import android.content.Context;
import android.support.annotation.NonNull;

import com.x8.brick.okhttp3.OkHttp3Request;
import com.x8.brick.okhttp3.OkHttp3Response;

import okhttp3.MediaType;

public class OkHttp3AssetThreadExecutor<T> extends OkHttp3AssetExecutor<T> {

    public OkHttp3AssetThreadExecutor(@NonNull Context context,
            String host, String directoryMapper, MediaType mediaType) {
        super(context, host, directoryMapper, mediaType);
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
