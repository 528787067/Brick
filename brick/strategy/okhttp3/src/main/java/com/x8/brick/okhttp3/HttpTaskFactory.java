package com.x8.brick.okhttp3;

import android.support.annotation.NonNull;

import com.x8.brick.core.HttpClient;
import com.x8.brick.core.HttpManager;
import com.x8.brick.executor.Executor;
import com.x8.brick.task.Task;
import com.x8.brick.task.TaskFactory;
import com.x8.brick.task.TaskModel;

public class HttpTaskFactory<T> implements TaskFactory<HttpRequest, HttpResponse, T> {

    @NonNull
    @Override
    public Task<HttpRequest, HttpResponse, T> create(@NonNull TaskModel<HttpRequest, HttpResponse> taskModel) {
        HttpManager<HttpRequest, HttpResponse> httpManager = taskModel.httpManager();
        HttpClient<HttpRequest, HttpResponse> httpClient = httpManager.httpClient();
        Executor<HttpRequest, HttpResponse, T> executor = httpClient.excutor(taskModel);
        return new HttpTask<>(taskModel, executor);
    }
}
