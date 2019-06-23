package com.x8.brick.okhttp3;

import android.support.annotation.NonNull;

import com.x8.brick.executor.Executor;
import com.x8.brick.task.Task;
import com.x8.brick.task.TaskModel;

public class HttpTask<T> extends Task<HttpRequest, HttpResponse, T> {
    protected HttpTask(@NonNull TaskModel<HttpRequest, HttpResponse> taskModel,
                       @NonNull Executor<HttpRequest, HttpResponse, T> executor) {
        super(taskModel, executor);
    }
}
