package com.x8.brick.okhttp3.executor.android;

import android.support.annotation.NonNull;

import com.x8.brick.executor.Executor;
import com.x8.brick.executor.ExecutorFacotry;
import com.x8.brick.okhttp3.OkHttp3Request;
import com.x8.brick.okhttp3.OkHttp3Response;
import com.x8.brick.task.TaskModel;

import java.io.File;

import okhttp3.MediaType;

public class OkHttp3FileAsyncTaskExecutorFactory implements ExecutorFacotry<OkHttp3Request, OkHttp3Response> {

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
    public <RESULT> Executor<OkHttp3Request, OkHttp3Response, RESULT> create(
            @NonNull TaskModel<OkHttp3Request, OkHttp3Response> taskModel) {
        return new OkHttp3FileAsyncTaskExecutor<>(host, directoryMapper, mediaType);
    }

    public static class Builder {

        private OkHttp3FileAsyncTaskExecutorFactory executorFactory;

        public Builder() {
            executorFactory = new OkHttp3FileAsyncTaskExecutorFactory();
        }

        public OkHttp3FileAsyncTaskExecutorFactory.Builder setHost(String host) {
            executorFactory.setHost(host);
            return this;
        }

        public OkHttp3FileAsyncTaskExecutorFactory.Builder setHost(File host) {
            executorFactory.setHost(host);
            return this;
        }

        public OkHttp3FileAsyncTaskExecutorFactory.Builder setDirectoryMapper(String directoryMapper) {
            executorFactory.setDirectoryMapper(directoryMapper);
            return this;
        }

        public OkHttp3FileAsyncTaskExecutorFactory.Builder setMediaType(String mediaType) {
            executorFactory.setMediaType(mediaType);
            return this;
        }

        public OkHttp3FileAsyncTaskExecutorFactory.Builder setMediaType(MediaType mediaType) {
            executorFactory.setMediaType(mediaType);
            return this;
        }

        public OkHttp3FileAsyncTaskExecutorFactory build() {
            return executorFactory;
        }
    }
}
