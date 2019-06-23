package com.x8.brick.okhttp3;

import android.support.annotation.NonNull;

import com.x8.brick.executor.Executor;
import com.x8.brick.executor.ExecutorFactory;
import com.x8.brick.task.TaskModel;

import java.util.List;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;

public class HttpExecutorFactory<T> implements ExecutorFactory<HttpRequest, HttpResponse, T> {

    private OkHttpClient httpClient;
    private HttpExecutor.AsyncExecutor asyncExecutor;

    public HttpExecutorFactory() {
        this(null, false);
    }

    public HttpExecutorFactory(OkHttpClient httpClient) {
        this(httpClient, false);
    }

    public HttpExecutorFactory(boolean okhttpEnqueueStrategy) {
        this(null, false);
    }

    public HttpExecutorFactory(OkHttpClient httpClient, boolean okhttpEnqueueStrategy) {
        if (httpClient == null) {
            httpClient = new OkHttpClient.Builder().build();
        }
        if (okhttpEnqueueStrategy) {
            List<Interceptor> interceptors = httpClient.interceptors();
            HttpExecutor.InterceptorExecutor executorInterceptor = HttpExecutor.InterceptorExecutor.getInstance();
            if (!interceptors.contains(executorInterceptor)) {
                httpClient = httpClient.newBuilder()
                        .addInterceptor(executorInterceptor)
                        .build();
            }
            this.asyncExecutor = HttpExecutor.InterceptorExecutor.getInstance();
        } else {
            this.asyncExecutor = new HttpExecutor.ThreadPoolExecutor<>(httpClient.dispatcher().executorService());
        }
        this.httpClient = httpClient;
    }

    @Override
    public Executor<HttpRequest, HttpResponse, T> create(@NonNull TaskModel<HttpRequest, HttpResponse> taskModel) {
        // noinspection unchecked
        return new HttpExecutor<>(httpClient, asyncExecutor);
    }
}
