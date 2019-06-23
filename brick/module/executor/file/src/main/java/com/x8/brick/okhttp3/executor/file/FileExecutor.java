package com.x8.brick.okhttp3.executor.file;

import com.x8.brick.exception.HttpException;
import com.x8.brick.executor.Executor;
import com.x8.brick.interceptor.InterceptorChain;
import com.x8.brick.okhttp3.HttpRequest;
import com.x8.brick.okhttp3.HttpResponse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.ResponseBody;

public abstract class FileExecutor<T> implements Executor<HttpRequest, HttpResponse, T> {

    private static final String DIRECTORY_MAPPER = "index";
    private static final String MEDIA_TYPE = "text/plain; charset=UTF-8";

    private String host;
    private String directoryMapper;
    private MediaType mediaType;

    private volatile boolean isExcuted;
    private volatile boolean isCanceled;

    public FileExecutor(String host, String directoryMapper, MediaType mediaType) {
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
    public HttpResponse execute(HttpRequest request) throws HttpException {
        synchronized (this) {
            if (isExcuted) {
                throw new IllegalStateException("Already Executed");
            }
            isExcuted = true;
        }
        HttpResponse.Builder responseBuilder = new HttpResponse.Builder()
                .request(request.raw())
                .protocol(Protocol.HTTP_1_1)
                .headers(request.headers());
        StringBuilder pathBuilder = new StringBuilder(host);
        HttpUrl httpUrl = request.url();
        List<String> paths = httpUrl.pathSegments();
        if (paths != null) {
            for (int i = 0; i < paths.size(); i++) {
                if (pathBuilder.length() > 0) {
                    pathBuilder.append(File.separator);
                }
                pathBuilder.append(paths.get(i));
            }
        }
        InputStream inputStream = null;
        InputStreamReader inputReader = null;
        BufferedReader bufferedReader = null;
        try {
            File file = new File(pathBuilder.toString());
            if (!file.exists()) {
                throw new FileNotFoundException(file.getAbsolutePath());
            }
            if (file.isDirectory() && !directoryMapper.isEmpty()) {
                file = new File(file, directoryMapper);
            }
            StringBuilder resultBuilder = new StringBuilder();
            inputStream = new FileInputStream(file);
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
            return responseBuilder.body(body).code(200).message("success").build();
        } catch (FileNotFoundException e) {
            ResponseBody body = ResponseBody.create(mediaType, e.toString());
            return responseBuilder.code(404).message(e.getMessage()).body(body).build();
        } catch (IOException e) {
            ResponseBody body = ResponseBody.create(mediaType, e.toString());
            return responseBuilder.code(500).message(e.getMessage()).body(body).build();
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

    void onAsyncExecute(HttpRequest request, Callback<HttpRequest, HttpResponse, T> callback) {
        if (isCanceled()) {
            return;
        }
        if (callback == null) {
            try {
                execute(request);
            } catch (HttpException e) {
                e.printStackTrace();
            }
            return;
        }
        try {
            request = callback.onRequest(this, request);
            HttpResponse response = callback.onExecute(this, request,
                    new InterceptorChain.Executor<HttpRequest, HttpResponse>() {
                        @Override
                        public HttpResponse execute(HttpRequest request) throws HttpException {
                            return FileExecutor.this.execute(request);
                        }
                    });
            T result = callback.onResponse(this, response);
            callback.onSuccess(this, result);
        } catch (HttpException e) {
            callback.onFailure(this, e);
        }
    }
}
