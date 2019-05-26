package com.x8.brick.strategy.okhttp3;

import android.support.annotation.NonNull;

import com.x8.brick.core.HttpManager;
import com.x8.brick.core.RequestModel;
import com.x8.brick.task.TaskModel;
import com.x8.brick.task.TaskModelFactory;

import java.lang.reflect.Type;

public class OkHttp3TaskModelFactory implements TaskModelFactory<OkHttp3Request, OkHttp3Response> {

    @NonNull
    @Override
    public TaskModel<OkHttp3Request, OkHttp3Response> create(
            HttpManager<OkHttp3Request, OkHttp3Response> httpManager, RequestModel requestModel, Type taskType) {
        return new OkHttp3TaskModel(httpManager, requestModel, taskType);
    }
}
