package com.x8.brick.okhttp3.executor.android;

import android.content.Context;
import android.content.res.AssetManager;
import android.support.annotation.NonNull;

import com.x8.brick.exception.HttpException;
import com.x8.brick.executor.Executor;
import com.x8.brick.interceptor.InterceptorChain;
import com.x8.brick.okhttp3.OkHttp3Request;
import com.x8.brick.okhttp3.OkHttp3Response;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public abstract class OkHttp3AssetExecutor<T> implements Executor<OkHttp3Request, OkHttp3Response, T> {

    private static final String DIRECTORY_MAPPER = "index";
    private static final String MEDIA_TYPE = "text/plain; charset=UTF-8";

    private Context context;

    private String host;
    private String directoryMapper;
    private MediaType mediaType;

    private volatile boolean isExcuted;
    private volatile boolean isCanceled;

    public OkHttp3AssetExecutor(@NonNull Context context, String host, String directoryMapper, MediaType mediaType) {
        this.context = context;
        if (host == null) {
            host = "";
        }
        if (directoryMapper == null) {
            directoryMapper = DIRECTORY_MAPPER;
        }
        if (mediaType == null) {
            mediaType = MediaType.parse(MEDIA_TYPE);
        }
        if (host.endsWith(File.separator)) {
            host = host.substring(0, host.lastIndexOf(File.separator));
        }
        if (directoryMapper.endsWith(File.separator)) {
            directoryMapper = directoryMapper.substring(0, directoryMapper.lastIndexOf(File.separator));
        }
        this.host = host;
        this.directoryMapper = directoryMapper;
        this.mediaType = mediaType;
    }

    @Override
    public OkHttp3Response execute(OkHttp3Request okHttp3Request) throws HttpException {
        synchronized (this) {
            if (isExcuted) {
                throw new IllegalStateException("Already Executed");
            }
            isExcuted = true;
        }
        Request request = okHttp3Request.request;
        Response.Builder responseBuilder = new Response.Builder()
                .request(request)
                .protocol(Protocol.HTTP_1_1)
                .headers(request.headers());
        StringBuilder pathBuilder = new StringBuilder(host);
        HttpUrl httpUrl = request.url();
        List<String> paths = httpUrl.pathSegments();
        if (paths != null) {
            for (int i = 0; i < paths.size(); i++) {
                pathBuilder.append(paths.get(i));
                if (i < paths.size() - 1) {
                    pathBuilder.append(File.separator);
                }
            }
        }
        InputStream inputStream = null;
        InputStreamReader inputReader = null;
        BufferedReader bufferedReader = null;
        try {
            String path = pathBuilder.toString();
            AssetManager assetManager = context.getAssets();
            String[] fileNames = assetManager.list(path);
            if (fileNames == null) {
                throw new FileNotFoundException(path);
            }
            if (fileNames.length > 0 && !directoryMapper.isEmpty()) {
                path += File.separator + directoryMapper;
            }
            StringBuilder resultBuilder = new StringBuilder();
            inputStream = assetManager.open(path);
            inputReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(inputReader);
            while (true) {
                String line = bufferedReader.readLine();
                if (line != null) {
                    resultBuilder.append(line);
                } else {
                    break;
                }
            }
            String result = resultBuilder.toString();
            ResponseBody body = ResponseBody.create(mediaType, result);
            Response response = responseBuilder.body(body).code(200).message("OK").build();
            return new OkHttp3Response(response);
        } catch (FileNotFoundException e) {
            return new OkHttp3Response(responseBuilder.code(404).message(e.toString()).build());
        } catch (IOException e) {
            return new OkHttp3Response(responseBuilder.code(500).message(e.toString()).build());
        } catch (Exception e) {
            throw new HttpException(e);
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputReader != null) {
                try {
                    inputReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void cancel() {
        isCanceled = true;
    }

    @Override
    public boolean isExecuted() {
        return isExcuted;
    }

    @Override
    public boolean isCanceled() {
        return isCanceled;
    }

    void onAsyncExecute(final OkHttp3Request okHttp3Request, Callback<OkHttp3Request, OkHttp3Response, T> callback) {
        if (isCanceled()) {
            return;
        }
        if (callback == null) {
            try {
                execute(okHttp3Request);
            } catch (HttpException e) {
                e.printStackTrace();
            }
            return;
        }
        try {
            OkHttp3Request request = callback.onRequest(this, okHttp3Request);
            OkHttp3Response response = callback.onExecute(this, request,
                    new InterceptorChain.Executor<OkHttp3Request, OkHttp3Response>() {
                        @Override
                        public OkHttp3Response execute(OkHttp3Request request) throws HttpException {
                            return OkHttp3AssetExecutor.this.execute(okHttp3Request);
                        }
                    });
            T result = callback.onResponse(this, response);
            callback.onSuccess(this, result);
        } catch (HttpException e) {
            callback.onFailure(this, e);
        }
    }
}
