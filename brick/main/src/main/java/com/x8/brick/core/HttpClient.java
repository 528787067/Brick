package com.x8.brick.core;

import android.support.annotation.NonNull;

import com.x8.brick.executor.Executor;
import com.x8.brick.executor.ExecutorFacotry;
import com.x8.brick.filter.RequestFilter;
import com.x8.brick.filter.ResponseFilter;
import com.x8.brick.interceptor.Interceptor;
import com.x8.brick.parameter.Request;
import com.x8.brick.parameter.Response;
import com.x8.brick.task.HttpTaskFactory;
import com.x8.brick.task.TaskFactory;
import com.x8.brick.task.TaskModel;
import com.x8.brick.task.TaskModelFactory;

import java.util.LinkedList;
import java.util.List;

public class HttpClient<REQUEST extends Request, RESPONSE extends Response> {

    private ExecutorFacotry<REQUEST, RESPONSE> executorFacotry;
    private TaskFactory<REQUEST, RESPONSE> taskFactory;
    private TaskModelFactory<REQUEST, RESPONSE> taskModelFactory;
    private List<Interceptor<REQUEST, RESPONSE>> interceptors;
    private List<RequestFilter<REQUEST>> requestFilters;
    private List<ResponseFilter<RESPONSE>> responseFilters;

    protected HttpClient() {
    }

    protected HttpClient(@NonNull ExecutorFacotry<REQUEST, RESPONSE> executorFacotry) {
        this.executorFacotry = executorFacotry;
    }

    public <RESULT> Executor<REQUEST, RESPONSE, RESULT> excutor(@NonNull TaskModel<REQUEST, RESPONSE> taskModel) {
        return executorFacotry.create(taskModel);
    }

    public List<Interceptor<REQUEST, RESPONSE>> interceptors() {
        return interceptors;
    }

    public List<RequestFilter<REQUEST>> requestFilters() {
        return requestFilters;
    }

    public List<ResponseFilter<RESPONSE>> responseFilters() {
        return responseFilters;
    }

    public ExecutorFacotry<REQUEST, RESPONSE> executorFacotry() {
        return executorFacotry;
    }

    public TaskFactory<REQUEST, RESPONSE> taskFactory() {
        return taskFactory;
    }

    public TaskModelFactory<REQUEST, RESPONSE> taskModelFactory() {
        return taskModelFactory;
    }

    protected void setExecutorFacotry(@NonNull ExecutorFacotry<REQUEST, RESPONSE> executorFacotry) {
        this.executorFacotry = executorFacotry;
    }

    protected void setTaskFactory(@NonNull TaskFactory<REQUEST, RESPONSE> taskFactory) {
        this.taskFactory = taskFactory;
    }

    protected void setTaskModelFactory(@NonNull TaskModelFactory<REQUEST, RESPONSE> taskModelFactory) {
        this.taskModelFactory = taskModelFactory;
    }

    protected void addIntercptor(@NonNull Interceptor<REQUEST, RESPONSE> intercptor) {
        if (interceptors == null) {
            interceptors = new LinkedList<>();
        }
        interceptors.add(intercptor);
    }

    protected void addRequestFilter(@NonNull RequestFilter<REQUEST> requestFilter) {
        if (requestFilters == null) {
            requestFilters = new LinkedList<>();
        }
        requestFilters.add(requestFilter);
    }

    protected void addResponseFilter(@NonNull ResponseFilter<RESPONSE> responseFilter) {
        if (responseFilters == null) {
            responseFilters = new LinkedList<>();
        }
        responseFilters.add(responseFilter);
    }

    public static class Builder<REQUEST extends Request, RESPONSE extends Response,
            CLIENT extends HttpClient<REQUEST, RESPONSE>,
            BUILDER extends Builder<REQUEST, RESPONSE, CLIENT, BUILDER>> {

        private CLIENT httpClient;

        public Builder() {
            this.httpClient = createHttpClient();
        }

        public Builder(@NonNull ExecutorFacotry<REQUEST, RESPONSE> executorFacotry) {
            this();
            setExecutorFacotry(executorFacotry);
        }

        protected CLIENT createHttpClient() {
            // noinspection unchecked
            return (CLIENT) new HttpClient<>();
        }

        protected BUILDER builder() {
            // noinspection unchecked
            return (BUILDER) this;
        }

        protected CLIENT httpClient() {
            return this.httpClient;
        }

        public BUILDER setExecutorFacotry(@NonNull ExecutorFacotry<REQUEST, RESPONSE> executorFacotry) {
            httpClient.setExecutorFacotry(executorFacotry);
            return builder();
        }

        public BUILDER setTaskFactory(@NonNull TaskFactory<REQUEST, RESPONSE> taskFactory) {
            httpClient.setTaskFactory(taskFactory);
            return builder();
        }

        public BUILDER setTaskModelFactory(@NonNull TaskModelFactory<REQUEST, RESPONSE> taskModelFactory) {
            httpClient.setTaskModelFactory(taskModelFactory);
            return builder();
        }

        public BUILDER addIntercptor(@NonNull Interceptor<REQUEST, RESPONSE> intercptor) {
            httpClient.addIntercptor(intercptor);
            return builder();
        }

        public BUILDER addRequestFilter(@NonNull RequestFilter<REQUEST> requestFilter) {
            httpClient.addRequestFilter(requestFilter);
            return builder();
        }

        public BUILDER addResponseFilter(@NonNull ResponseFilter<RESPONSE> responseFilter) {
            httpClient.addResponseFilter(responseFilter);
            return builder();
        }

        public CLIENT build() {
            if (httpClient.executorFacotry() == null) {
                throw new IllegalArgumentException("You should first set up an ExecutorFacotry");
            }
            if (httpClient.taskFactory() == null) {
                setTaskFactory(new HttpTaskFactory<REQUEST, RESPONSE>());
            }
            return httpClient;
        }
    }
}
