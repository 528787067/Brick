package com.x8.brick.task;

import android.support.annotation.NonNull;

import com.x8.brick.parameter.Request;
import com.x8.brick.parameter.Response;

public interface TaskFactory<REQUEST extends Request, RESPONSE extends Response> {

    @NonNull
    <RESULT> Task<REQUEST, RESPONSE, RESULT> create(@NonNull TaskModel<REQUEST, RESPONSE> taskModel);
}
