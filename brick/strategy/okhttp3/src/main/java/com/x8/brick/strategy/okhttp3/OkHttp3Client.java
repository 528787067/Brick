package com.x8.brick.strategy.okhttp3;

import android.support.annotation.NonNull;

import com.x8.brick.core.HttpClient;
import com.x8.brick.exception.HttpException;
import com.x8.brick.executor.ExecutorFacotry;
import com.x8.brick.interceptor.Interceptor;

import java.io.IOException;

import okhttp3.Connection;
import okhttp3.Request;
import okhttp3.Response;

public class OkHttp3Client extends HttpClient<OkHttp3Request, OkHttp3Response> {

    private okhttp3.OkHttpClient okHttpClient;

    void setOkHttpClient(@NonNull okhttp3.OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    void addOkHttpIntercptor(@NonNull final okhttp3.Interceptor interceptor) {
        addIntercptor(new Interceptor<OkHttp3Request, OkHttp3Response>() {

            @Override
            public OkHttp3Response intercept(final Chain<OkHttp3Request, OkHttp3Response> chain) throws HttpException {

                okhttp3.Interceptor.Chain okHttpChain = new okhttp3.Interceptor.Chain() {

                    @Override
                    public Request request() {
                        return chain.request().request;
                    }

                    @Override
                    public Response proceed(Request request) throws IOException {
                        try {
                            return chain.proceed(new OkHttp3Request(request)).response;
                        } catch (HttpException e) {
                            throw new IOException(e.getCause());
                        }
                    }

                    @Override
                    public Connection connection() {
                        return null;
                    }
                };

                try {
                    return new OkHttp3Response(interceptor.intercept(okHttpChain));
                } catch (IOException e) {
                    throw new HttpException(e.getCause());
                }
            }
        });
    }

    public okhttp3.OkHttpClient okHttpClient() {
        return okHttpClient;
    }

    public static class Builder extends HttpClient.Builder<OkHttp3Request, OkHttp3Response, OkHttp3Client, Builder> {

        public Builder() {
            super();
        }

        public Builder(@NonNull ExecutorFacotry<OkHttp3Request, OkHttp3Response> executorFacotry) {
            super(executorFacotry);
        }

        @Override
        protected OkHttp3Client createHttpClient() {
            return new OkHttp3Client();
        }

        public Builder setOkHttpClient(@NonNull okhttp3.OkHttpClient okHttpClient) {
            httpClient().setOkHttpClient(okHttpClient);
            return this;
        }

        public Builder addOkHttpIntercptor(@NonNull okhttp3.Interceptor interceptor) {
            httpClient().addOkHttpIntercptor(interceptor);
            return this;
        }

        @Override
        public OkHttp3Client build() {
            OkHttp3Client httpClient = httpClient();
            if (httpClient.executorFacotry() == null) {
                okhttp3.OkHttpClient okHttpClient = httpClient.okHttpClient();
                if (okHttpClient != null) {
                    setExecutorFacotry(new OkHttp3ExecutorFactory(okHttpClient));
                } else {
                    setExecutorFacotry(new OkHttp3ExecutorFactory());
                }
            }
            if (httpClient.taskFactory() == null) {
                setTaskFactory(new OkHttp3TaskFactory());
            }
            if (httpClient.taskModelFactory() == null) {
                setTaskModelFactory(new OkHttp3TaskModelFactory());
            }
            return super.build();
        }
    }
}
