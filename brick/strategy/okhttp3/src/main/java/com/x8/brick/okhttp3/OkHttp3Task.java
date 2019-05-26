package com.x8.brick.okhttp3;

import android.support.annotation.NonNull;

import com.x8.brick.executor.Executor;
import com.x8.brick.task.Task;
import com.x8.brick.task.TaskModel;

public class OkHttp3Task<T> extends Task<OkHttp3Request, OkHttp3Response, T> {
    protected OkHttp3Task(@NonNull TaskModel<OkHttp3Request, OkHttp3Response> taskModel,
                          @NonNull Executor<OkHttp3Request, OkHttp3Response, T> executor) {
        super(taskModel, executor);
    }
}
