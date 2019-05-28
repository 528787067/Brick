package com.x8.brick.executor.async;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.x8.brick.exception.HttpException;
import com.x8.brick.executor.Executor;
import com.x8.brick.interceptor.InterceptorChain;
import com.x8.brick.parameter.Request;
import com.x8.brick.parameter.Response;

public abstract class AsyncTaskExecutor<REQUEST extends Request, RESPONSE extends Response, RESULT>
        implements Executor<REQUEST, RESPONSE, RESULT> {

    private volatile ExecutorAsyncTask<REQUEST, RESPONSE, RESULT> executorAsyncTask;

    @Override
    public void asyncExecute(REQUEST request, Callback<REQUEST, RESPONSE, RESULT> callback) {
        synchronized (this) {
            if (executorAsyncTask != null) {
                throw new IllegalStateException("Already Executed");
            }
            executorAsyncTask = new ExecutorAsyncTask<>(this, callback);
        }
        // noinspection unchecked
        executorAsyncTask.execute(request);
    }

    @Override
    public void cancel() {
        if (executorAsyncTask != null) {
            executorAsyncTask.cancel(true);
        }
    }

    @Override
    public boolean isCanceled() {
        return executorAsyncTask != null && executorAsyncTask.isCancelled();
    }

    private static class ExecutorAsyncTask<REQUEST extends Request, RESPONSE extends Response, RESULT>
            extends AsyncTask<REQUEST, Void, RESULT> {

        private Executor<REQUEST, RESPONSE, RESULT> executor;
        private Callback<REQUEST, RESPONSE, RESULT> callback;
        private HttpException exception;

        ExecutorAsyncTask(@NonNull Executor<REQUEST, RESPONSE, RESULT> executor,
                @Nullable Callback<REQUEST, RESPONSE, RESULT> callback) {
            this.executor = executor;
            this.callback = callback;
        }

        @SafeVarargs
        @Override
        protected final RESULT doInBackground(REQUEST... requests) {
            if (executor.isCanceled()) {
                return null;
            }
            try {
                if (requests.length != 1) {
                    throw new HttpException(new IllegalArgumentException("Request parameter error."));
                }
                if (callback == null) {
                    executor.execute(requests[0]);
                    return null;
                }
                REQUEST request = callback.onRequest(executor, requests[0]);
                RESPONSE response = callback.onExecute(executor, request,
                        new InterceptorChain.Executor<REQUEST, RESPONSE>() {
                            @Override
                            public RESPONSE execute(REQUEST request) throws HttpException {
                                return executor.execute(request);
                            }
                        });
                return callback.onResponse(executor, response);
            } catch (HttpException e) {
                this.exception = e;
                return null;
            }
        }

        @Override
        protected void onPostExecute(RESULT result) {
            if (callback != null && !executor.isCanceled()) {
                if (exception == null) {
                    callback.onSuccess(executor, result);
                } else {
                    callback.onFailure(executor, exception);
                }
            }
        }
    }
}
