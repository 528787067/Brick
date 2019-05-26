package com.x8.brick.strategy.okhttp3;

import android.support.annotation.NonNull;

import com.x8.brick.core.HttpClient;
import com.x8.brick.core.HttpManager;
import com.x8.brick.executor.Executor;
import com.x8.brick.task.Task;
import com.x8.brick.task.TaskFactory;
import com.x8.brick.task.TaskModel;

public class OkHttp3TaskFactory implements TaskFactory<OkHttp3Request, OkHttp3Response> {

    @NonNull
    @Override
    public <RESULT> Task<OkHttp3Request, OkHttp3Response, RESULT> create(
            @NonNull TaskModel<OkHttp3Request, OkHttp3Response> taskModel) {
        HttpManager<OkHttp3Request, OkHttp3Response> httpManager = taskModel.httpManager();
        HttpClient<OkHttp3Request, OkHttp3Response> httpClient = httpManager.httpClient();
        Executor<OkHttp3Request, OkHttp3Response, RESULT> executor = httpClient.excutor(taskModel);
        return new OkHttp3Task<>(taskModel, executor);
    }
}
