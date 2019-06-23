package com.x8.brick.executor;

import android.support.annotation.NonNull;

import com.x8.brick.exception.HttpException;
import com.x8.brick.interceptor.InterceptorChain;
import com.x8.brick.okhttp3.HttpRequest;
import com.x8.brick.okhttp3.HttpResponse;
import com.x8.brick.task.TaskModel;

import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.ResponseBody;

public class StringExecutorFactory<T> implements ExecutorFactory<HttpRequest, HttpResponse, T> {
    @Override
    public Executor<HttpRequest, HttpResponse, T> create(@NonNull TaskModel<HttpRequest, HttpResponse> taskModel) {
        return new StringExecutor<>();
    }

    private static class StringExecutor<T> implements Executor<HttpRequest, HttpResponse, T> {

        private boolean isCanceled;
        private boolean isExecuted;

        @Override
        public HttpResponse execute(HttpRequest request) throws HttpException {
            isExecuted = true;
            String body;
            if ("POST".equalsIgnoreCase(request.method())) {
                body = "{\"timestamp\":1559045843783,\"code\":0,\"message\":\"success\",\"data\":{\"method\":\"post\",\"name\":\"王小二\",\"age\":22}}";
            } else {
                body = "{\"timestamp\":1559045828766,\"code\":0,\"message\":\"success\",\"data\":{\"method\":\"get\",\"name\":\"王小二\",\"age\":22}}";
            }
            ResponseBody responseBody = ResponseBody.create(MediaType.parse("application/json; charset=UTF-8"), body);
            return new HttpResponse.Builder()
                    .request(request.raw())
                    .protocol(Protocol.HTTP_1_1)
                    .headers(request.headers())
                    .code(200)
                    .body(responseBody)
                    .build();
        }

        @Override
        public void asyncExecute(final HttpRequest httpRequest, final Callback<HttpRequest, HttpResponse, T> callback) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    HttpRequest request = callback.onRequest(StringExecutor.this, httpRequest);
                    try {
                        HttpResponse response = callback.onExecute(StringExecutor.this, request,
                                new InterceptorChain.Executor<HttpRequest, HttpResponse>() {
                            @Override
                            public HttpResponse execute(HttpRequest request) throws HttpException {
                                return StringExecutor.this.execute(request);
                            }
                        });
                        T result = callback.onResponse(StringExecutor.this, response);
                        callback.onSuccess(StringExecutor.this, result);
                    } catch (HttpException e) {
                        callback.onFailure(StringExecutor.this, e);
                    }
                }
            }).start();
        }

        @Override
        public void cancel() {
            isCanceled = true;
        }

        @Override
        public boolean isExecuted() {
            return isExecuted;
        }

        @Override
        public boolean isCanceled() {
            return isCanceled;
        }
    }
}
