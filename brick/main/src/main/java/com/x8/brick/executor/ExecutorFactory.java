package com.x8.brick.executor;

import android.support.annotation.NonNull;

import com.x8.brick.parameter.Request;
import com.x8.brick.parameter.Response;
import com.x8.brick.task.TaskModel;

public interface ExecutorFactory<REQUEST extends Request, RESPONSE extends Response, RESULT> {
    Executor<REQUEST, RESPONSE, RESULT> create(@NonNull TaskModel<REQUEST, RESPONSE> taskModel);
}
