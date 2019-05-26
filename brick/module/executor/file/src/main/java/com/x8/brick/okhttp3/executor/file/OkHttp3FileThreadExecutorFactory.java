package com.x8.brick.okhttp3.executor.file;

import android.support.annotation.NonNull;

import com.x8.brick.executor.Executor;
import com.x8.brick.executor.ExecutorFacotry;
import com.x8.brick.okhttp3.OkHttp3Request;
import com.x8.brick.okhttp3.OkHttp3Response;
import com.x8.brick.task.TaskModel;

import java.io.File;

import okhttp3.MediaType;

public class OkHttp3FileThreadExecutorFactory implements ExecutorFacotry<OkHttp3Request, OkHttp3Response> {

    private String host;
    private String indexMapper;
    private MediaType mediaType;

    public void setHost(String host) {
        this.host = host;
    }

    public void setHost(File host) {
        setHost(host == null ? null : host.getAbsolutePath());
    }

    public void setIndexMapper(String indexMapper) {
        this.indexMapper = indexMapper;
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
        return new OkHttp3FileThreadExecutor<>(host, indexMapper, mediaType);
    }

    public static class Builder {

        private OkHttp3FileThreadExecutorFactory executorFactory;

        public Builder() {
            executorFactory = new OkHttp3FileThreadExecutorFactory();
        }

        public OkHttp3FileThreadExecutorFactory.Builder setHost(String host) {
            executorFactory.setHost(host);
            return this;
        }

        public OkHttp3FileThreadExecutorFactory.Builder setHost(File host) {
            executorFactory.setHost(host);
            return this;
        }

        public OkHttp3FileThreadExecutorFactory.Builder setIndexMapper(String indexMapper) {
            executorFactory.setIndexMapper(indexMapper);
            return this;
        }

        public OkHttp3FileThreadExecutorFactory.Builder setMediaType(String mediaType) {
            executorFactory.setMediaType(mediaType);
            return this;
        }

        public OkHttp3FileThreadExecutorFactory.Builder setMediaType(MediaType mediaType) {
            executorFactory.setMediaType(mediaType);
            return this;
        }

        public OkHttp3FileThreadExecutorFactory build() {
            return executorFactory;
        }
    }
}
