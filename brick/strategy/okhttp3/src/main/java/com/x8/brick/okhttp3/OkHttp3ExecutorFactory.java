package com.x8.brick.okhttp3;

import android.support.annotation.NonNull;

import com.x8.brick.executor.Executor;
import com.x8.brick.executor.ExecutorFacotry;
import com.x8.brick.task.TaskModel;

import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

public class OkHttp3ExecutorFactory implements ExecutorFacotry<OkHttp3Request, OkHttp3Response> {

    private OkHttpClient httpClient;
    private OkHttp3Executor.AsyncExecutor asyncExecutor;

    public OkHttp3ExecutorFactory() {
        this(null, false);
    }

    public OkHttp3ExecutorFactory(OkHttpClient httpClient) {
        this(httpClient, false);
    }

    public OkHttp3ExecutorFactory(boolean okhttpEnqueueStrategy) {
        this(null, false);
    }

    public OkHttp3ExecutorFactory(OkHttpClient httpClient, boolean okhttpEnqueueStrategy) {
        if (httpClient == null) {
            httpClient = new OkHttpClient.Builder().build();
        }
        if (okhttpEnqueueStrategy) {
            List<Interceptor> interceptors = httpClient.interceptors();
            OkHttp3Executor.InterceptorExecutor executorInterceptor = OkHttp3Executor.InterceptorExecutor.getInstance();
            if (!interceptors.contains(executorInterceptor)) {
                httpClient = httpClient.newBuilder()
                        .addInterceptor(executorInterceptor)
                        .build();
            }
            this.asyncExecutor = OkHttp3Executor.InterceptorExecutor.getInstance();
        } else {
            this.asyncExecutor = new OkHttp3Executor.ThreadPoolExecutor<>(httpClient.dispatcher().executorService());
        }
        this.httpClient = httpClient;
    }

    @Override
    public <RESULT> Executor<OkHttp3Request, OkHttp3Response, RESULT> create(
            @NonNull TaskModel<OkHttp3Request, OkHttp3Response> taskModel) {
        // noinspection unchecked
        return new OkHttp3Executor<>(httpClient, asyncExecutor);
    }
}
