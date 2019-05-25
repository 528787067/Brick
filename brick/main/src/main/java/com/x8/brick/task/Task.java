package com.x8.brick.task;

import android.support.annotation.NonNull;

import com.x8.brick.exception.HttpException;
import com.x8.brick.executor.Executor;
import com.x8.brick.interceptor.InterceptorChain;
import com.x8.brick.parameter.Request;
import com.x8.brick.parameter.Response;

public class Task<REQUEST extends Request, RESPONSE extends Response, RESULT> {

    private TaskModel<REQUEST, RESPONSE> taskModel;
    private Executor<REQUEST, RESPONSE, RESULT> executor;

    protected Task(@NonNull TaskModel<REQUEST, RESPONSE> taskModel,
            @NonNull Executor<REQUEST, RESPONSE, RESULT> executor) {
        this.taskModel = taskModel;
        this.executor = executor;
    }

    public RESULT execute() throws HttpException {
        REQUEST request = request();
        request = onRequest(request);
        RESPONSE response = onExecute(request, new InterceptorChain.Executor<REQUEST, RESPONSE>() {
            @Override
            public RESPONSE execute(REQUEST request) throws HttpException {
                return executor.execute(request);
            }
        });
        response = onResponse(response);
        return onResult(response);
    }

    public void asyncExecute(final Callback<RESULT> callback) {
        executor.asyncExecute(request(), new Executor.Callback<REQUEST, RESPONSE, RESULT>() {
            @Override
            public REQUEST onRequest(Executor executor, REQUEST request) {
                return Task.this.onRequest(request);
            }
            @Override
            public RESPONSE onExecute(Executor executor, REQUEST request,
                    @NonNull InterceptorChain.Executor<REQUEST, RESPONSE> interceptorExecutor
            ) throws HttpException {
                return Task.this.onExecute(request, interceptorExecutor);
            }
            @Override
            public RESULT onResponse(Executor executor, RESPONSE response) {
                response = Task.this.onResponse(response);
                return Task.this.onResult(response);
            }
            @Override
            public void onSuccess(Executor executor, RESULT result) {
                if (callback != null) {
                    callback.onSuccess(Task.this, result);
                }
            }
            @Override
            public void onFailure(Executor executor, HttpException exception) {
                if (callback != null) {
                    callback.onFailure(Task.this, exception);
                }
            }
        });
    }

    private REQUEST onRequest(REQUEST request) {
        return taskModel.filterRequest(request);
    }

    private RESPONSE onExecute(REQUEST request, InterceptorChain.Executor<REQUEST, RESPONSE> executor)
            throws HttpException {
        return taskModel.doInterceptor(request, executor);
    }

    private RESPONSE onResponse(RESPONSE response) {
        return taskModel.filterResponse(response);
    }

    private RESULT onResult(RESPONSE response) {
        return taskModel.adaptResponse(response);
    }

    public void cancel() {
        executor.cancel();
    }

    public boolean isExecuted() {
        return executor.isExecuted();
    }

    public boolean isCanceled() {
        return executor.isCanceled();
    }

    public REQUEST request() {
        return taskModel.request();
    }

    public interface Callback<RESPONSE> {
        void onSuccess(Task task, RESPONSE response);
        void onFailure(Task task, HttpException exception);
    }
}
