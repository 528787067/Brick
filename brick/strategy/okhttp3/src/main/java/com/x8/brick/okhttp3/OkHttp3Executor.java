package com.x8.brick.okhttp3;

import android.support.annotation.NonNull;

import com.x8.brick.exception.HttpException;
import com.x8.brick.executor.Executor;
import com.x8.brick.interceptor.InterceptorChain;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private AsyncExecutor<T> executor;

    public OkHttp3Executor(@NonNull OkHttpClient httpClient) {
        this(httpClient, null);
    }

    public OkHttp3Executor(@NonNull OkHttpClient httpClient, AsyncExecutor<T> executor) {
        if (executor == null) {
            executor = new ThreadPoolExecutor<>(httpClient.dispatcher().executorService());
        }
        this.okHttpClient = httpClient;
        this.executor = executor;
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
        executor.asyncExecute(this, request, callback);
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
        return call != null && call.isCanceled();
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

    public interface AsyncExecutor<T> {
        void asyncExecute(OkHttp3Executor<T> executor, OkHttp3Request request,
                Callback<OkHttp3Request, OkHttp3Response, T> callback);
    }

    public abstract static class AsyncExecutorStrategy<T> implements AsyncExecutor<T> {
        protected void onAsyncExecute(final OkHttp3Executor<T> executor, final OkHttp3Request okHttp3Request,
                Callback<OkHttp3Request, OkHttp3Response, T> callback) {
            if (callback == null) {
                try {
                    executor.execute(okHttp3Request);
                } catch (HttpException e) {
                    e.printStackTrace();
                }
                return;
            }
            try {
                OkHttp3Request request = callback.onRequest(executor, okHttp3Request);
                OkHttp3Response response = callback.onExecute(executor, request,
                        new InterceptorChain.Executor<OkHttp3Request, OkHttp3Response>() {
                            @Override
                            public OkHttp3Response execute(OkHttp3Request request) throws HttpException {
                                return executor.execute(okHttp3Request);
                            }
                        });
                T result = callback.onResponse(executor, response);
                callback.onSuccess(executor, result);
            } catch (HttpException e) {
                callback.onFailure(executor, e);
            }
        }
    }

    public static class ThreadExecutor<T> extends AsyncExecutorStrategy<T> {
        @Override
        public void asyncExecute(final OkHttp3Executor<T> executor, final OkHttp3Request request,
                final Callback<OkHttp3Request, OkHttp3Response, T> callback) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    onAsyncExecute(executor, request, callback);
                }
            }).start();
        }
    }

    public static class ThreadPoolExecutor<T> extends AsyncExecutorStrategy<T> {

        private ExecutorService executorService;

        public ThreadPoolExecutor() {
            this(null);
        }

        public ThreadPoolExecutor(ExecutorService executorService) {
            if (executorService == null) {
                executorService = Executors.newCachedThreadPool();
            }
            this.executorService = executorService;
        }

        @Override
        public void asyncExecute(final OkHttp3Executor<T> executor, final OkHttp3Request request,
                final Callback<OkHttp3Request, OkHttp3Response, T> callback) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    onAsyncExecute(executor, request, callback);
                }
            });
        }
    }

    public static class InterceptorExecutor<T> implements AsyncExecutor<T>, Interceptor {

        private static InterceptorExecutor instance = new InterceptorExecutor();

        public static InterceptorExecutor getInstance() {
            return instance;
        }

        private Map<Request, Executor> executors;

        private InterceptorExecutor() {
            executors = new ConcurrentHashMap<>();
        }

        @Override
        public void asyncExecute(OkHttp3Executor<T> executor, OkHttp3Request request,
                Callback<OkHttp3Request, OkHttp3Response, T> callback) {
            if (executor == null) {
                throw new IllegalArgumentException("Executor is null");
            }
            executor.makeCall(request);
            Call call = executor.call();
            if (call == null) {
                throw new IllegalArgumentException("Call is null");
            }
            Executor<?> executorCallback = new Executor<>(executor, request, callback);
            executors.put(call.request(), executorCallback);
            call.enqueue(executorCallback);
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
                if (!streaming) {
                    response = bufferResponse(response);
                }
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

            Response intercept(Chain chain) throws IOException {
                if (callback != null) {
                    request = callback.onRequest(executor, request);
                }
                okhttp3.Response rawResponse = chain.proceed(request.request);
                if (!request.streaming) {
                    rawResponse = bufferResponse(rawResponse);
                }
                response = new OkHttp3Response(rawResponse);
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
