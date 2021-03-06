package com.x8.brick.okhttp3.executor.android;

import android.content.Context;
import android.support.annotation.NonNull;

import com.x8.brick.executor.Executor;
import com.x8.brick.executor.ExecutorFacotry;
import com.x8.brick.okhttp3.OkHttp3Request;
import com.x8.brick.okhttp3.OkHttp3Response;
import com.x8.brick.task.TaskModel;

import java.io.File;

import okhttp3.MediaType;

public class OkHttp3AssetThreadExecutorFactory implements ExecutorFacotry<OkHttp3Request, OkHttp3Response> {

    private Context context;
    private String host;
    private String directoryMapper;
    private MediaType mediaType;

    public OkHttp3AssetThreadExecutorFactory(@NonNull Context context) {
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
    public <RESULT> Executor<OkHttp3Request, OkHttp3Response, RESULT> create(
            @NonNull TaskModel<OkHttp3Request, OkHttp3Response> taskModel) {
        return new OkHttp3AssetThreadExecutor<>(context, host, directoryMapper, mediaType);
    }

    public static class Builder {

        private OkHttp3AssetThreadExecutorFactory executorFactory;

        public Builder(@NonNull Context context) {
            executorFactory = new OkHttp3AssetThreadExecutorFactory(context);
        }

        public OkHttp3AssetThreadExecutorFactory.Builder setHost(String host) {
            executorFactory.setHost(host);
            return this;
        }

        public OkHttp3AssetThreadExecutorFactory.Builder setHost(File host) {
            executorFactory.setHost(host);
            return this;
        }

        public OkHttp3AssetThreadExecutorFactory.Builder setDirectoryMapper(String directoryMapper) {
            executorFactory.setDirectoryMapper(directoryMapper);
            return this;
        }

        public OkHttp3AssetThreadExecutorFactory.Builder setMediaType(String mediaType) {
            executorFactory.setMediaType(mediaType);
            return this;
        }

        public OkHttp3AssetThreadExecutorFactory.Builder setMediaType(MediaType mediaType) {
            executorFactory.setMediaType(mediaType);
            return this;
        }

        public OkHttp3AssetThreadExecutorFactory build() {
            return executorFactory;
        }
    }
}
