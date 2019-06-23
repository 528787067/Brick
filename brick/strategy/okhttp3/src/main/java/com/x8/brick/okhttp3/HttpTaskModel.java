package com.x8.brick.okhttp3;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.x8.brick.core.HttpManager;
import com.x8.brick.core.RequestModel;
import com.x8.brick.task.TaskModel;

import java.lang.reflect.Type;

public class HttpTaskModel extends TaskModel<HttpRequest, HttpResponse> {

    private RequestGenerator<HttpRequest, HttpResponse> requestGenerator;
    private RequestModelChecker requestModelChecker;

    protected HttpTaskModel(HttpManager<HttpRequest, HttpResponse> httpManager,
                            RequestModel requestModel,
                            Type taskType,
                            RequestGenerator<HttpRequest, HttpResponse> requestGenerator,
                            RequestModelChecker requestModelChecker) {
        super(httpManager, requestModel, taskType);
        this.requestGenerator = requestGenerator;
        this.requestModelChecker = requestModelChecker;
    }

    @NonNull
    @Override
    protected RequestGenerator<HttpRequest, HttpResponse> requestGenerator() {
        return requestGenerator;
    }

    @Nullable
    @Override
    protected RequestModelChecker requestModelChecker() {
        return requestModelChecker;
    }
}
