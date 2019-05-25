package com.x8.brick.task;

import android.support.annotation.NonNull;

import com.x8.brick.core.HttpManager;
import com.x8.brick.core.RequestModel;
import com.x8.brick.parameter.Request;
import com.x8.brick.parameter.Response;

import java.lang.reflect.Type;

public interface TaskModelFactory<REQUEST extends Request, RESPONSE extends Response> {

    @NonNull
    TaskModel<REQUEST, RESPONSE> create(
            HttpManager<REQUEST, RESPONSE> httpManager, RequestModel requestModel, Type taskType);
}
