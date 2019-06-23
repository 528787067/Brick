package com.x8.brick.core;

import android.support.annotation.NonNull;

import com.x8.brick.executor.Executor;
import com.x8.brick.executor.ExecutorFactory;
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

    private ExecutorFactory<REQUEST, RESPONSE, ?> executorFactory;
    private TaskFactory<REQUEST, RESPONSE, ?> taskFactory;
    private TaskModelFactory<REQUEST, RESPONSE> taskModelFactory;
    private List<Interceptor<REQUEST, RESPONSE>> interceptors;
    private List<RequestFilter<REQUEST>> requestFilters;
    private List<ResponseFilter<RESPONSE>> responseFilters;

    protected HttpClient() {
    }

    protected HttpClient(@NonNull ExecutorFactory<REQUEST, RESPONSE, ?> executorFactory) {
        this.executorFactory = executorFactory;
    }

    public <RESULT> Executor<REQUEST, RESPONSE, RESULT> excutor(@NonNull TaskModel<REQUEST, RESPONSE> taskModel) {
        // noinspection unchecked
        return (Executor<REQUEST, RESPONSE, RESULT>) executorFactory.create(taskModel);
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

    public <RESULT> ExecutorFactory<REQUEST, RESPONSE, RESULT> executorFactory() {
        // noinspection unchecked
        return (ExecutorFactory<REQUEST, RESPONSE, RESULT>) executorFactory;
    }

    public <RESULT> TaskFactory<REQUEST, RESPONSE, RESULT> taskFactory() {
        // noinspection unchecked
        return (TaskFactory<REQUEST, RESPONSE, RESULT>) taskFactory;
    }

    public TaskModelFactory<REQUEST, RESPONSE> taskModelFactory() {
        return taskModelFactory;
    }

    protected void setExecutorFactory(@NonNull ExecutorFactory<REQUEST, RESPONSE, ?> executorFactory) {
        this.executorFactory = executorFactory;
    }

    protected void setTaskFactory(@NonNull TaskFactory<REQUEST, RESPONSE, ?> taskFactory) {
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

        public Builder(@NonNull ExecutorFactory<REQUEST, RESPONSE, ?> executorFactory) {
            this();
            setExecutorFactory(executorFactory);
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

        public BUILDER setExecutorFactory(@NonNull ExecutorFactory<REQUEST, RESPONSE, ?> executorFactory) {
            httpClient.setExecutorFactory(executorFactory);
            return builder();
        }

        public BUILDER setTaskFactory(@NonNull TaskFactory<REQUEST, RESPONSE, ?> taskFactory) {
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
            if (httpClient.executorFactory() == null) {
                throw new IllegalArgumentException("You should first set up an ExecutorFactory");
            }
            if (httpClient.taskFactory() == null) {
                HttpTaskFactory<REQUEST, RESPONSE, ?> taskFactory = new HttpTaskFactory<>();
                setTaskFactory(taskFactory);
            }
            return httpClient;
        }
    }
}
