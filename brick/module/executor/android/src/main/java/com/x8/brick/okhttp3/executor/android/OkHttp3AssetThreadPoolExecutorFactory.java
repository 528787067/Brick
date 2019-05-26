package com.x8.brick.okhttp3.executor.android;

import android.content.Context;
import android.support.annotation.NonNull;

import com.x8.brick.executor.Executor;
import com.x8.brick.executor.ExecutorFacotry;
import com.x8.brick.okhttp3.OkHttp3Request;
import com.x8.brick.okhttp3.OkHttp3Response;
import com.x8.brick.task.TaskModel;

import java.io.File;
import java.util.concurrent.ExecutorService;

import okhttp3.MediaType;

public class OkHttp3AssetThreadPoolExecutorFactory implements ExecutorFacotry<OkHttp3Request, OkHttp3Response> {

    private Context context;
    private String host;
    private String directoryMapper;
    private MediaType mediaType;
    private ExecutorService executorService;

    public OkHttp3AssetThreadPoolExecutorFactory(@NonNull Context context) {
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

    public void setExecutorService(ExecutorService executorService) {
        this.executorService = executorService;
    }

    @Override
    public <RESULT> Executor<OkHttp3Request, OkHttp3Response, RESULT> create(
            @NonNull TaskModel<OkHttp3Request, OkHttp3Response> taskModel) {
        return new OkHttp3AssetThreadPoolExecutor<>(context, host, directoryMapper, mediaType, executorService);
    }

    public static class Builder {

        private OkHttp3AssetThreadPoolExecutorFactory executorFactory;

        public Builder(@NonNull Context context) {
            executorFactory = new OkHttp3AssetThreadPoolExecutorFactory(context);
        }

        public OkHttp3AssetThreadPoolExecutorFactory.Builder setHost(String host) {
            executorFactory.setHost(host);
            return this;
        }

        public OkHttp3AssetThreadPoolExecutorFactory.Builder setHost(File host) {
            executorFactory.setHost(host);
            return this;
        }

        public OkHttp3AssetThreadPoolExecutorFactory.Builder setDirectoryMapper(String directoryMapper) {
            executorFactory.setDirectoryMapper(directoryMapper);
            return this;
        }

        public OkHttp3AssetThreadPoolExecutorFactory.Builder setMediaType(String mediaType) {
            executorFactory.setMediaType(mediaType);
            return this;
        }

        public OkHttp3AssetThreadPoolExecutorFactory.Builder setMediaType(MediaType mediaType) {
            executorFactory.setMediaType(mediaType);
            return this;
        }

        public OkHttp3AssetThreadPoolExecutorFactory.Builder setExecutorService(ExecutorService executorService) {
            executorFactory.setExecutorService(executorService);
            return this;
        }

        public OkHttp3AssetThreadPoolExecutorFactory build() {
            return executorFactory;
        }
    }
}
