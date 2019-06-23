package com.x8.brick.okhttp3.executor.android;

import android.support.annotation.NonNull;

import com.x8.brick.executor.Executor;
import com.x8.brick.executor.ExecutorFactory;
import com.x8.brick.okhttp3.HttpRequest;
import com.x8.brick.okhttp3.HttpResponse;
import com.x8.brick.task.TaskModel;

import java.io.File;

import okhttp3.MediaType;

public class FileAsyncTaskExecutorFactory<T> implements ExecutorFactory<HttpRequest, HttpResponse, T> {

    private String host;
    private String directoryMapper;
    private MediaType mediaType;

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
        return new FileAsyncTaskExecutor<>(host, directoryMapper, mediaType);
    }

    public static class Builder {

        private FileAsyncTaskExecutorFactory executorFactory;

        public Builder() {
            executorFactory = new FileAsyncTaskExecutorFactory();
        }

        public FileAsyncTaskExecutorFactory.Builder setHost(String host) {
            executorFactory.setHost(host);
            return this;
        }

        public FileAsyncTaskExecutorFactory.Builder setHost(File host) {
            executorFactory.setHost(host);
            return this;
        }

        public FileAsyncTaskExecutorFactory.Builder setDirectoryMapper(String directoryMapper) {
            executorFactory.setDirectoryMapper(directoryMapper);
            return this;
        }

        public FileAsyncTaskExecutorFactory.Builder setMediaType(String mediaType) {
            executorFactory.setMediaType(mediaType);
            return this;
        }

        public FileAsyncTaskExecutorFactory.Builder setMediaType(MediaType mediaType) {
            executorFactory.setMediaType(mediaType);
            return this;
        }

        public FileAsyncTaskExecutorFactory build() {
            return executorFactory;
        }
    }
}
