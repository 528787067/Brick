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

    public OkHttp3ExecutorFactory() {
        this(new OkHttpClient.Builder().build());
    }

    public OkHttp3ExecutorFactory(@NonNull OkHttpClient httpClient) {
        this.httpClient = httpClient;
        List<Interceptor> interceptors = this.httpClient.interceptors();
        OkHttp3Executor.ExecutorInterceptor interceptor = OkHttp3Executor.ExecutorInterceptor.getInstance();
        interceptors.remove(interceptor);
        interceptors.add(0, interceptor);
    }

    @Override
    public <RESULT> Executor<OkHttp3Request, OkHttp3Response, RESULT> create(
            @NonNull TaskModel<OkHttp3Request, OkHttp3Response> taskModel) {
        return new OkHttp3Executor<>(httpClient);
    }
}
