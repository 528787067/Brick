package com.x8.brick.okhttp3;

import android.support.annotation.NonNull;

import com.x8.brick.core.HttpClient;

public class HttpManager extends com.x8.brick.core.HttpManager<HttpRequest, HttpResponse> {

    protected HttpManager(@NonNull HttpClient<HttpRequest, HttpResponse> httpClient) {
        super(httpClient);
    }

    public static class Builder extends com.x8.brick.core.HttpManager
            .Builder<HttpRequest, HttpResponse, HttpManager, Builder> {

        public Builder(@NonNull HttpClient<HttpRequest, HttpResponse> httpClient) {
            super(httpClient);
        }

        @Override
        protected HttpManager createHttpManager(@NonNull HttpClient httpClient) {
            // noinspection unchecked
            return new HttpManager(httpClient);
        }

        @Override
        public HttpManager build() {
            addTypeHandlerFactory(new HttpTypeHandlerFactory())
                    .addMethodHandlerFactory(new HttpMethodHandlerFactory())
                    .addParameterHandlerFactory(new HttpParameterHandlerFactory());
            return super.build();
        }
    }
}
