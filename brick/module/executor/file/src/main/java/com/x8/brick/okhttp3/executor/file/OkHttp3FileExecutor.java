package com.x8.brick.okhttp3.executor.file;

import com.x8.brick.exception.HttpException;
import com.x8.brick.executor.Executor;
import com.x8.brick.interceptor.InterceptorChain;
import com.x8.brick.okhttp3.OkHttp3Request;
import com.x8.brick.okhttp3.OkHttp3Response;

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
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public abstract class OkHttp3FileExecutor<T> implements Executor<OkHttp3Request, OkHttp3Response, T> {

    private static final String INDEX_MAPPER = "index";
    private static final String MEDIA_TYPE = "text/plain; charset=UTF-8";

    private String host;
    private String indexMapper;
    private MediaType mediaType;

    private volatile boolean isExcuted;
    private volatile boolean isCanceled;

    public OkHttp3FileExecutor(String host, String indexMapper, MediaType mediaType) {
        if (host == null) {
            host = "";
        }
        if (indexMapper == null) {
            indexMapper = INDEX_MAPPER;
        }
        if (mediaType == null) {
            mediaType = MediaType.parse(MEDIA_TYPE);
        }
        if (host.endsWith(File.separator)) {
            host = host.substring(0, host.lastIndexOf(File.separator));
        }
        if (indexMapper.endsWith(File.separator)) {
            indexMapper = indexMapper.substring(0, indexMapper.lastIndexOf(File.separator));
        }
        this.host = host;
        this.indexMapper = indexMapper;
        this.mediaType = mediaType;
    }

    @Override
    public OkHttp3Response execute(OkHttp3Request okHttp3Request) throws HttpException {
        isExcuted = true;
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
        File file = new File(pathBuilder.toString());
        InputStream inputStream = null;
        InputStreamReader inputReader = null;
        BufferedReader bufferedReader = null;
        try {
            while (file.isDirectory() && !indexMapper.isEmpty()) {
                file = new File(file, indexMapper);
            }
            if (!file.exists()) {
                throw new FileNotFoundException(file.getAbsolutePath());
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
            Response response = responseBuilder.body(body).code(200).message("OK").build();
            return new OkHttp3Response(response);
        } catch (FileNotFoundException e) {
            return new OkHttp3Response(responseBuilder.code(404).message(e.toString()).build());
        } catch (IOException e) {
            return new OkHttp3Response(responseBuilder.code(500).message(e.toString()).build());
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

    protected void onAsyncExecute(final OkHttp3Request okHttp3Request,
            Callback<OkHttp3Request, OkHttp3Response, T> callback) {
        if (callback == null) {
            try {
                OkHttp3FileExecutor.this.execute(okHttp3Request);
            } catch (HttpException e) {
                e.printStackTrace();
            }
            return;
        }
        try {
            OkHttp3Request request = callback.onRequest(OkHttp3FileExecutor.this, okHttp3Request);
            OkHttp3Response response = callback.onExecute(OkHttp3FileExecutor.this, request,
                    new InterceptorChain.Executor<OkHttp3Request, OkHttp3Response>() {
                        @Override
                        public OkHttp3Response execute(OkHttp3Request request) throws HttpException {
                            return OkHttp3FileExecutor.this.execute(okHttp3Request);
                        }
                    });
            T result = callback.onResponse(OkHttp3FileExecutor.this, response);
            callback.onSuccess(OkHttp3FileExecutor.this, result);
        } catch (HttpException e) {
            callback.onFailure(OkHttp3FileExecutor.this, e);
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
}
