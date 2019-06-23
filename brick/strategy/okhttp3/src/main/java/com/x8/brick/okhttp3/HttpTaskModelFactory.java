package com.x8.brick.okhttp3;

import android.support.annotation.NonNull;

import com.x8.brick.core.HttpManager;
import com.x8.brick.core.RequestModel;
import com.x8.brick.task.TaskModel;
import com.x8.brick.task.TaskModelFactory;

import java.lang.reflect.Type;

public class HttpTaskModelFactory implements TaskModelFactory<HttpRequest, HttpResponse> {

    private TaskModel.RequestGenerator<HttpRequest, HttpResponse> requestGenerator;
    private TaskModel.RequestModelChecker requestModelChecker;

    public HttpTaskModelFactory() {
        requestGenerator = new HttpRequestGenerator();
        requestModelChecker = new HttpRequestModelChecker();
    }

    @NonNull
    @Override
    public TaskModel<HttpRequest, HttpResponse> create(HttpManager<HttpRequest, HttpResponse> httpManager,
                                                       RequestModel requestModel,
                                                       Type taskType) {
        return new HttpTaskModel(httpManager, requestModel, taskType, requestGenerator, requestModelChecker);
    }
}
