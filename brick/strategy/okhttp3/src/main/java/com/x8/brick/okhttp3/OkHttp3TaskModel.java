package com.x8.brick.okhttp3;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.x8.brick.core.HttpManager;
import com.x8.brick.core.RequestModel;
import com.x8.brick.task.TaskModel;

import java.lang.reflect.Type;

import okhttp3.Response;

public class OkHttp3TaskModel extends TaskModel<OkHttp3Request, OkHttp3Response> {

    protected OkHttp3TaskModel(
            HttpManager<OkHttp3Request, OkHttp3Response> httpManager, RequestModel requestModel, Type taskType) {
        super(httpManager, requestModel, taskType);
    }

    @NonNull
    @Override
    protected RequestGenerator<OkHttp3Request> requestGenerator() {
        return new OkHttp3RequestGenerator(httpManager());
    }

    @Override
    public Type responseType() {
        Type type = super.responseType();
        if (type == OkHttp3Response.class) {
            throw new IllegalArgumentException(
                    "'" + type + "' is not a valid response body type. Did you mean Response?");
        }
        return type;
    }

    @Nullable
    @Override
    protected RequestModelChecker requestModelChecker() {
        return new OkHttp3RequestModelChecker();
    }
}
