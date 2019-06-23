package com.x8.brick.okhttp3.executor.android;

import android.content.Context;
import android.support.annotation.NonNull;

import com.x8.brick.executor.Executor;
import com.x8.brick.executor.ExecutorFactory;
import com.x8.brick.okhttp3.HttpRequest;
import com.x8.brick.okhttp3.HttpResponse;
import com.x8.brick.task.TaskModel;

import java.io.File;

import okhttp3.MediaType;

public class AssetAsyncTaskExecutorFactory<T> implements ExecutorFactory<HttpRequest, HttpResponse, T> {

    private Context context;
    private String host;
    private String directoryMapper;
    private MediaType mediaType;

    public AssetAsyncTaskExecutorFactory(@NonNull Context context) {
        this.context = context;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setHost(File host) {
        setHost(host == null ? null : host.getAbsolutePath());
    }

    public void setDirectoryMapper(String directoryMapper) {
        this.directoryMapper = directoryMapper;
    }

    public void setMediaType(String mediaType) {
        setMediaType(mediaType == null ? null : MediaType.parse(mediaType));
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    @Override
    public Executor<HttpRequest, HttpResponse, T> create(@NonNull TaskModel<HttpRequest, HttpResponse> taskModel) {
        return new AssetAsyncTaskExecutor<>(context, host, directoryMapper, mediaType);
    }

    public static class Builder {

        private AssetAsyncTaskExecutorFactory executorFactory;

        public Builder(@NonNull Context context) {
            executorFactory = new AssetAsyncTaskExecutorFactory(context);
        }

        public AssetAsyncTaskExecutorFactory.Builder setHost(String host) {
            executorFactory.setHost(host);
            return this;
        }

        public AssetAsyncTaskExecutorFactory.Builder setHost(File host) {
            executorFactory.setHost(host);
            return this;
        }

        public AssetAsyncTaskExecutorFactory.Builder setDirectoryMapper(String directoryMapper) {
            executorFactory.setDirectoryMapper(directoryMapper);
            return this;
        }

        public AssetAsyncTaskExecutorFactory.Builder setMediaType(String mediaType) {
            executorFactory.setMediaType(mediaType);
            return this;
        }

        public AssetAsyncTaskExecutorFactory.Builder setMediaType(MediaType mediaType) {
            executorFactory.setMediaType(mediaType);
            return this;
        }

        public AssetAsyncTaskExecutorFactory build() {
            return executorFactory;
        }
    }
}
