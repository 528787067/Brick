package com.x8.brick.okhttp3.executor.file;

import android.support.annotation.NonNull;

import com.x8.brick.executor.Executor;
import com.x8.brick.executor.ExecutorFactory;
import com.x8.brick.okhttp3.HttpRequest;
import com.x8.brick.okhttp3.HttpResponse;
import com.x8.brick.task.TaskModel;

import java.io.File;
import java.util.concurrent.ExecutorService;

import okhttp3.MediaType;

public class FileThreadPoolExecutorFactory<T> implements ExecutorFactory<HttpRequest, HttpResponse, T> {

    private String host;
    private String directoryMapper;
    private MediaType mediaType;
    private ExecutorService executorService;

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

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public Executor<HttpRequest, HttpResponse, T> create(@NonNull TaskModel<HttpRequest, HttpResponse> taskModel) {
        return new FileThreadPoolExecutor<>(host, directoryMapper, mediaType, executorService);
    }

    public static class Builder {

        private FileThreadPoolExecutorFactory executorFactory;

        public Builder() {
            executorFactory = new FileThreadPoolExecutorFactory();
        }

        public FileThreadPoolExecutorFactory.Builder setHost(String host) {
            executorFactory.setHost(host);
            return this;
        }

        public FileThreadPoolExecutorFactory.Builder setHost(File host) {
            executorFactory.setHost(host);
            return this;
        }

        public FileThreadPoolExecutorFactory.Builder setDirectoryMapper(String directoryMapper) {
            executorFactory.setDirectoryMapper(directoryMapper);
            return this;
        }

        public FileThreadPoolExecutorFactory.Builder setMediaType(String mediaType) {
            executorFactory.setMediaType(mediaType);
            return this;
        }

        public FileThreadPoolExecutorFactory.Builder setMediaType(MediaType mediaType) {
            executorFactory.setMediaType(mediaType);
            return this;
        }

        public FileThreadPoolExecutorFactory.Builder setExecutorService(ExecutorService executorService) {
            executorFactory.setExecutorService(executorService);
            return this;
        }

        public FileThreadPoolExecutorFactory build() {
            return executorFactory;
        }
    }
}
