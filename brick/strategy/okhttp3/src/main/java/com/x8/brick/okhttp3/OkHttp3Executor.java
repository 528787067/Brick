package com.x8.brick.okhttp3;

import android.support.annotation.NonNull;

import com.x8.brick.exception.HttpException;
import com.x8.brick.executor.Executor;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

public class OkHttp3Executor<T> implements Executor<OkHttp3Request, OkHttp3Response, T> {

    private OkHttpClient okHttpClient;
    private Call call;

    public OkHttp3Executor(@NonNull OkHttpClient httpClient) {
        this.okHttpClient = httpClient;
    }

    public Call call() {
        return call;
    }

    @Override
    public OkHttp3Response execute(OkHttp3Request request) throws HttpException {
        makeCall(request);
        try {
            Response response = call.execute();
            if (!request.streaming) {
                response = bufferResponse(response);
            }
            return new OkHttp3Response(response);
        } catch (IOException e) {
            throw new HttpException(e);
        }
    }

    @Override
    public void asyncExecute(OkHttp3Request request, Callback<OkHttp3Request, OkHttp3Response, T> callback) {
        makeCall(request);
        ExecutorInterceptor.instance.enqueue(this, request, callback);
    }

    @Override
    public void cancel() {
        if (call != null) {
            call.cancel();
        }
    }

    @Override
    public boolean isExecuted() {
        return call != null && call.isExecuted();
    }

    @Override
    public boolean isCanceled() {
        return call == null || call.isCanceled();
    }

    private synchronized void makeCall(OkHttp3Request request) {
        if (call != null) {
            throw new IllegalStateException("Already Executed");
        }
        call = okHttpClient.newCall(request.request);
    }

    private static Response bufferResponse(Response response) throws IOException {
        ResponseBody rawBody = response.body();
        if (rawBody != null) {
            try {
                Buffer buffer = new Buffer();
                rawBody.source().readAll(buffer);
                ResponseBody bufferBody = ResponseBody.create(
                        rawBody.contentType(), rawBody.contentLength(), buffer);
                return response.newBuilder().body(bufferBody).build();
            } finally {
                rawBody.close();
            }
        }
        return response;
    }

    public static class ExecutorInterceptor implements Interceptor {

        private static ExecutorInterceptor instance = new ExecutorInterceptor();

        public static ExecutorInterceptor getInstance() {
            return instance;
        }

        private Map<Request, Executor> executors;

        private ExecutorInterceptor() {
            executors = new ConcurrentHashMap<>();
        }

        public void enqueue(OkHttp3Executor<?> executor, OkHttp3Request request,
                            final Callback<OkHttp3Request, OkHttp3Response, ?> callback) {
            if (executor == null) {
                throw new IllegalStateException("Executor is null");
            }
            Call call = executor.call();
            if (call == null) {
                throw new IllegalStateException("Call is null");
            }
            // noinspection unchecked
            call.enqueue(executors.put(call.request(), new Executor(executor, request, callback)));
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
            Response response = null;
            boolean streaming = false;
            if (executors.containsKey(request)) {
                Executor executor = executors.remove(request);
                if (executor != null) {
                    streaming = executor.request.streaming;
                    response = executor.intercept(chain);
                }
            }
            if (response == null) {
                response = chain.proceed(request);
            }
            if (!streaming) {
                response = bufferResponse(response);
            }
            return response;
        }

        private static class Executor<T> implements okhttp3.Callback {

            private OkHttp3Executor executor;
            private OkHttp3Request request;
            private OkHttp3Response response;
            private Callback<OkHttp3Request, OkHttp3Response, T> callback;
            private T result;

            Executor(OkHttp3Executor executor, OkHttp3Request request,
                     Callback<OkHttp3Request, OkHttp3Response, T> callback) {
                this.executor = executor;
                this.request = request;
                this.callback = callback;
            }

            public Response intercept(Chain chain) throws IOException {
                if (callback != null) {
                    request = callback.onRequest(executor, request);
                }
                response = new OkHttp3Response(chain.proceed(request.request));
                if (callback != null) {
                    result = callback.onResponse(executor, response);
                }
                return response.response;
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (callback != null) {
                    callback.onSuccess(executor, result);
                }
            }

            @Override
            public void onFailure(Call call, IOException e) {
                if (callback != null) {
                    callback.onFailure(executor, new HttpException(e));
                }
            }
        }
    }
}
