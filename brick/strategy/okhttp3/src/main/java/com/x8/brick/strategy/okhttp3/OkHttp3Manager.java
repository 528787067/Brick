package com.x8.brick.strategy.okhttp3;

import android.support.annotation.NonNull;

import com.x8.brick.core.HttpClient;
import com.x8.brick.core.HttpManager;

public class OkHttp3Manager extends HttpManager<OkHttp3Request, OkHttp3Response> {

    protected OkHttp3Manager(@NonNull HttpClient<OkHttp3Request, OkHttp3Response> httpClient) {
        super(httpClient);
    }

    public static class Builder extends HttpManager.Builder<OkHttp3Request, OkHttp3Response, OkHttp3Manager, Builder> {

        public Builder(@NonNull HttpClient<OkHttp3Request, OkHttp3Response> httpClient) {
            super(httpClient);
        }

        @Override
        protected OkHttp3Manager createHttpManager(@NonNull HttpClient httpClient) {
            // noinspection unchecked
            return new OkHttp3Manager(httpClient);
        }

        @Override
        public OkHttp3Manager build() {
            HttpManager httpManager = httpManager();
            boolean cacheAble = httpManager.handlerCacheAble();
            if (httpManager.typeAnnotationHandlerDelegate() == null) {
                setTypeAnnotationHandlerDelegate(new OkHttp3TypeAnnotationHandlerDelegate(cacheAble));
            }
            if (httpManager.methodAnnotationHandlerDelegate() == null) {
                setMethodAnnotationHandlerDelegate(new OkHttp3MethodAnnotationHandlerDelegate(cacheAble));
            }
            if (httpManager.parameterAnnotationHandlerDelegate() == null) {
                setParameterAnnotationHandlerDelegate(new OkHttp3ParameterAnnotationHandlerDelegate(cacheAble));
            }
            addResponseConverter(new OkHttp3ResponseConverter());
            return super.build();
        }
    }
}
