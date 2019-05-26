package com.x8.brick.executor;

import com.x8.brick.exception.HttpException;
import com.x8.brick.interceptor.InterceptorChain;
import com.x8.brick.parameter.Request;
import com.x8.brick.parameter.Response;

public interface Executor<REQUEST extends Request, RESPONSE extends Response, RESULT> {

    RESPONSE execute(REQUEST request) throws HttpException;

    void asyncExecute(REQUEST request, Callback<REQUEST, RESPONSE, RESULT> callback);

    void cancel();

    boolean isExecuted();

    boolean isCanceled();

    interface Callback<REQUEST extends Request, RESPONSE extends Response, RESULT> {
        REQUEST onRequest(Executor executor, REQUEST request);
        RESPONSE onExecute(Executor executor, REQUEST request,
                InterceptorChain.Executor<REQUEST, RESPONSE> interceptorExecutor
        ) throws HttpException;
        RESULT onResponse(Executor executor, RESPONSE response);
        void onSuccess(Executor executor, RESULT result);
        void onFailure(Executor executor, HttpException exception);
    }
}
