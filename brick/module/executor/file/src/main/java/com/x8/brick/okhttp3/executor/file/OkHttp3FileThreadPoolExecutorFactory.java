package com.x8.brick.okhttp3.executor.file;

import android.support.annotation.NonNull;

import com.x8.brick.executor.Executor;
import com.x8.brick.executor.ExecutorFacotry;
import com.x8.brick.okhttp3.OkHttp3Request;
import com.x8.brick.okhttp3.OkHttp3Response;
import com.x8.brick.task.TaskModel;

import java.io.File;
import java.util.concurrent.ExecutorService;

import okhttp3.MediaType;

public class OkHttp3FileThreadPoolExecutorFactory implements ExecutorFacotry<OkHttp3Request, OkHttp3Response> {

    private String host;
    private String indexMapper;
    private MediaType mediaType;
    private ExecutorService executorService;

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

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public <RESULT> Executor<OkHttp3Request, OkHttp3Response, RESULT> create(
            @NonNull TaskModel<OkHttp3Request, OkHttp3Response> taskModel) {
        return new OkHttp3FileThreadPoolExecutor<>(host, indexMapper, mediaType, executorService);
    }

    public static class Builder {

        private OkHttp3FileThreadPoolExecutorFactory executorFactory;

        public Builder() {
            executorFactory = new OkHttp3FileThreadPoolExecutorFactory();
        }

        public OkHttp3FileThreadPoolExecutorFactory.Builder setHost(String host) {
            executorFactory.setHost(host);
            return this;
        }

        public OkHttp3FileThreadPoolExecutorFactory.Builder setHost(File host) {
            executorFactory.setHost(host);
            return this;
        }

        public OkHttp3FileThreadPoolExecutorFactory.Builder setIndexMapper(String indexMapper) {
            executorFactory.setIndexMapper(indexMapper);
            return this;
        }

        public OkHttp3FileThreadPoolExecutorFactory.Builder setMediaType(String mediaType) {
            executorFactory.setMediaType(mediaType);
            return this;
        }

        public OkHttp3FileThreadPoolExecutorFactory.Builder setMediaType(MediaType mediaType) {
            executorFactory.setMediaType(mediaType);
            return this;
        }

        public OkHttp3FileThreadPoolExecutorFactory.Builder setExecutorService(ExecutorService executorService) {
            executorFactory.setExecutorService(executorService);
            return this;
        }

        public OkHttp3FileThreadPoolExecutorFactory build() {
            return executorFactory;
        }
    }
}
