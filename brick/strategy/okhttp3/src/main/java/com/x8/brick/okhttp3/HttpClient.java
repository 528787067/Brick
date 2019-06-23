package com.x8.brick.okhttp3;

import android.support.annotation.NonNull;

import com.x8.brick.exception.HttpException;
import com.x8.brick.executor.ExecutorFactory;
import com.x8.brick.interceptor.Interceptor;

import java.io.IOException;

import okhttp3.Connection;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpClient extends com.x8.brick.core.HttpClient<HttpRequest, HttpResponse> {

    private OkHttpClient okHttpClient;
    private boolean okhttpEnqueueStrategy;

    void setOkHttpClient(@NonNull OkHttpClient okHttpClient) {
        this.okHttpClient = okHttpClient;
    }

    void setOkhttpEnqueueStrategy(boolean okhttpEnqueueStrategy) {
        this.okhttpEnqueueStrategy = okhttpEnqueueStrategy;
    }

    void addOkHttpIntercptor(@NonNull final okhttp3.Interceptor interceptor) {
        addIntercptor(new Interceptor<HttpRequest, HttpResponse>() {

            @Override
            public HttpResponse intercept(final Chain<HttpRequest, HttpResponse> chain) throws HttpException {

                okhttp3.Interceptor.Chain okHttpChain = new okhttp3.Interceptor.Chain() {

                    @Override
                    public Request request() {
                        return chain.request().raw();
                    }

                    @Override
                    public Response proceed(Request request) throws IOException {
                        try {
                            HttpRequest httpRequest = HttpRequest.request(request, chain.request().isStreaming());
                            return chain.proceed(httpRequest).raw();
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
                    return HttpResponse.response(interceptor.intercept(okHttpChain));
                } catch (IOException e) {
                    throw new HttpException(e.getCause());
                }
            }
        });
    }

    public OkHttpClient okHttpClient() {
        return okHttpClient;
    }

    public boolean okhttpEnqueueStrategy() {
        return okhttpEnqueueStrategy;
    }

    public static class Builder extends com.x8.brick.core.HttpClient
            .Builder<HttpRequest, HttpResponse, HttpClient, Builder> {

        public Builder() {
            super();
        }

        public Builder(@NonNull ExecutorFactory<HttpRequest, HttpResponse, ?> executorFacotry) {
            super(executorFacotry);
        }

        @Override
        protected HttpClient createHttpClient() {
            return new HttpClient();
        }

        public Builder setOkHttpClient(@NonNull OkHttpClient okHttpClient) {
            httpClient().setOkHttpClient(okHttpClient);
            return this;
        }

        public Builder setOkhttpEnqueueStrategy(boolean okhttpEnqueueStrategy) {
            httpClient().setOkhttpEnqueueStrategy(okhttpEnqueueStrategy);
            return this;
        }

        public Builder addOkHttpIntercptor(@NonNull okhttp3.Interceptor interceptor) {
            httpClient().addOkHttpIntercptor(interceptor);
            return this;
        }

        @Override
        public HttpClient build() {
            HttpClient httpClient = httpClient();
            if (httpClient.executorFactory() == null) {
                ExecutorFactory<HttpRequest, HttpResponse, ?> executorFactory = new HttpExecutorFactory<>(
                        httpClient.okHttpClient(), httpClient.okhttpEnqueueStrategy());
                setExecutorFactory(executorFactory);
            }
            if (httpClient.taskFactory() == null) {
                setTaskFactory(new HttpTaskFactory<>());
            }
            if (httpClient.taskModelFactory() == null) {
                setTaskModelFactory(new HttpTaskModelFactory());
            }
            return super.build();
        }
    }
}
