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

public class HttpExecutor<T> implements Executor<HttpRequest, HttpResponse, T> {

    private OkHttpClient okHttpClient;
    private Call call;
    private AsyncExecutor<T> executor;

    public HttpExecutor(@NonNull OkHttpClient httpClient) {
        this(httpClient, null);
    }

    public HttpExecutor(@NonNull OkHttpClient httpClient, AsyncExecutor<T> executor) {
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
    public HttpResponse execute(HttpRequest request) throws HttpException {
        makeCall(request);
        try {
            Response response = call.execute();
            if (!request.isStreaming()) {
                response = bufferResponse(response);
            }
            return HttpResponse.response(response);
        } catch (IOException e) {
            throw new HttpException(e);
        }
    }

    @Override
    public void asyncExecute(HttpRequest request, Callback<HttpRequest, HttpResponse, T> callback) {
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

    private synchronized void makeCall(HttpRequest request) {
        if (call != null) {
            throw new IllegalStateException("Already Executed");
        }
        call = okHttpClient.newCall(request.raw());
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
        void asyncExecute(HttpExecutor<T> executor, HttpRequest request,
                          Callback<HttpRequest, HttpResponse, T> callback);
    }

    public abstract static class AsyncExecutorStrategy<T> implements AsyncExecutor<T> {
        protected void onAsyncExecute(final HttpExecutor<T> executor, HttpRequest request,
                                      Callback<HttpRequest, HttpResponse, T> callback) {
            if (callback == null) {
                try {
                    executor.execute(request);
                } catch (HttpException e) {
                    e.printStackTrace();
                }
                return;
            }
            try {
                request = callback.onRequest(executor, request);
                HttpResponse response = callback.onExecute(executor, request,
                        new InterceptorChain.Executor<HttpRequest, HttpResponse>() {
                            @Override
                            public HttpResponse execute(HttpRequest request) throws HttpException {
                                return executor.execute(request);
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
        public void asyncExecute(final HttpExecutor<T> executor, final HttpRequest request,
                                 final Callback<HttpRequest, HttpResponse, T> callback) {
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
        public void asyncExecute(final HttpExecutor<T> executor, final HttpRequest request,
                                 final Callback<HttpRequest, HttpResponse, T> callback) {
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
        public void asyncExecute(HttpExecutor<T> executor, HttpRequest request,
                                 Callback<HttpRequest, HttpResponse, T> callback) {
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
                    streaming = executor.request.isStreaming();
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

            private HttpExecutor executor;
            private HttpRequest request;
            private HttpResponse response;
            private Callback<HttpRequest, HttpResponse, T> callback;
            private T result;

            Executor(HttpExecutor executor, HttpRequest request, Callback<HttpRequest, HttpResponse, T> callback) {
                this.executor = executor;
                this.request = request;
                this.callback = callback;
            }

            Response intercept(Chain chain) throws IOException {
                if (callback != null) {
                    request = callback.onRequest(executor, request);
                }
                Response rawResponse = chain.proceed(request.raw());
                if (!request.isStreaming()) {
                    rawResponse = bufferResponse(rawResponse);
                }
                response = HttpResponse.response(rawResponse);
                if (callback != null) {
                    result = callback.onResponse(executor, response);
                }
                return rawResponse;
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
