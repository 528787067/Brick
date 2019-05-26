package com.x8.brick.okhttp3.executor.file;

import com.x8.brick.okhttp3.OkHttp3Request;
import com.x8.brick.okhttp3.OkHttp3Response;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.MediaType;

public class OkHttp3FileThreadPoolExecutor<T> extends OkHttp3FileExecutor<T> {

    private ExecutorService executorService;

    public OkHttp3FileThreadPoolExecutor(String host, String indexMapper,
            MediaType mediaType, ExecutorService executorService) {
        super(host, indexMapper, mediaType);
        if (executorService == null) {
            executorService = Executors.newCachedThreadPool();
        }
        this.executorService = executorService;
    }

    @Override
    public void asyncExecute(final OkHttp3Request request,
            final Callback<OkHttp3Request, OkHttp3Response, T> callback) {
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                onAsyncExecute(request, callback);
            }
        });
    }
}
