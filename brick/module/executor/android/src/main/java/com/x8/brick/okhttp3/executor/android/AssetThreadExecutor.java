package com.x8.brick.okhttp3.executor.android;

import android.content.Context;
import android.support.annotation.NonNull;

import com.x8.brick.okhttp3.HttpRequest;
import com.x8.brick.okhttp3.HttpResponse;

import okhttp3.MediaType;

public class AssetThreadExecutor<T> extends AssetExecutor<T> {

    public AssetThreadExecutor(@NonNull Context context, String host, String directoryMapper, MediaType mediaType) {
        super(context, host, directoryMapper, mediaType);
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
