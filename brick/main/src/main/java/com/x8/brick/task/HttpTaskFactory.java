package com.x8.brick.task;

import android.support.annotation.NonNull;

import com.x8.brick.core.HttpClient;
import com.x8.brick.core.HttpManager;
import com.x8.brick.executor.Executor;
import com.x8.brick.parameter.Request;
import com.x8.brick.parameter.Response;

public class HttpTaskFactory<REQUEST extends Request, RESPONSE extends Response>
        implements TaskFactory<REQUEST, RESPONSE> {

    @NonNull
    @Override
    public <RESULT> Task<REQUEST, RESPONSE, RESULT> create(@NonNull TaskModel<REQUEST, RESPONSE> taskModel) {
        HttpManager<REQUEST, RESPONSE> httpManager = taskModel.httpManager();
        HttpClient<REQUEST, RESPONSE> httpClient = httpManager.httpClient();
        Executor<REQUEST, RESPONSE, RESULT> executor = httpClient.excutor(taskModel);
        return new Task<>(taskModel, executor);
    }
}
