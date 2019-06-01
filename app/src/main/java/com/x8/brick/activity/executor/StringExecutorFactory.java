package com.x8.brick.activity.executor;

import android.support.annotation.NonNull;

import com.x8.brick.exception.HttpException;
import com.x8.brick.executor.Executor;
import com.x8.brick.executor.ExecutorFacotry;
import com.x8.brick.interceptor.InterceptorChain;
import com.x8.brick.okhttp3.OkHttp3Request;
import com.x8.brick.okhttp3.OkHttp3Response;
import com.x8.brick.task.TaskModel;

import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class StringExecutorFactory implements ExecutorFacotry<OkHttp3Request, OkHttp3Response> {
    @Override
    public <RESULT> Executor<OkHttp3Request, OkHttp3Response, RESULT> create(@NonNull TaskModel<OkHttp3Request, OkHttp3Response> taskModel) {
        return new StringExecutor<>();
    }

    private static class StringExecutor<T> implements Executor<OkHttp3Request, OkHttp3Response, T> {

        private boolean isCanceled;
        private boolean isExecuted;

        @Override
        public OkHttp3Response execute(OkHttp3Request okHttp3Request) throws HttpException {
            isExecuted = true;
            Request request = okHttp3Request.request;
            String body;
            if ("POST".equalsIgnoreCase(request.method())) {
                body = "{\"timestamp\":1559045843783,\"code\":0,\"message\":\"success\",\"data\":{\"method\":\"post\",\"name\":\"王小二\",\"age\":22}}";
            } else {
                body = "{\"timestamp\":1559045828766,\"code\":0,\"message\":\"success\",\"data\":{\"method\":\"get\",\"name\":\"王小二\",\"age\":22}}";
            }
            ResponseBody responseBody = ResponseBody.create(MediaType.parse("application/json; charset=UTF-8"), body);
            Response response = new Response.Builder()
                    .request(request)
                    .protocol(Protocol.HTTP_1_1)
                    .headers(request.headers())
                    .code(200)
                    .body(responseBody)
                    .build();
            return new OkHttp3Response(response);
        }

        @Override
        public void asyncExecute(final OkHttp3Request okHttp3Request, final Callback<OkHttp3Request, OkHttp3Response, T> callback) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    OkHttp3Request request = callback.onRequest(StringExecutor.this, okHttp3Request);
                    try {
                        OkHttp3Response response = callback.onExecute(StringExecutor.this, request,
                                new InterceptorChain.Executor<OkHttp3Request, OkHttp3Response>() {
                            @Override
                            public OkHttp3Response execute(OkHttp3Request request) throws HttpException {
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
