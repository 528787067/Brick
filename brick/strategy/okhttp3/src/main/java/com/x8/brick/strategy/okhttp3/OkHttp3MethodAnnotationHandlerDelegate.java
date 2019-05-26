package com.x8.brick.strategy.okhttp3;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.x8.brick.annotation.define.method.Headers;
import com.x8.brick.annotation.define.type.Handler;
import com.x8.brick.annotation.handler.AnnotationHandlerHelper;
import com.x8.brick.annotation.handler.AnnotationHandlerHelper.MethodModel;
import com.x8.brick.annotation.handler.MethodAnnotationHandler;
import com.x8.brick.annotation.handler.MethodAnnotationHandlerDelegate;
import com.x8.brick.core.RequestModel;
import com.x8.brick.utils.ConvertUtils;

import java.lang.annotation.Annotation;

public class OkHttp3MethodAnnotationHandlerDelegate extends MethodAnnotationHandlerDelegate {

    private boolean cacheAble;

    public OkHttp3MethodAnnotationHandlerDelegate() {
        this(true);
    }

    public OkHttp3MethodAnnotationHandlerDelegate(boolean cacheAble) {
        this.cacheAble = cacheAble;
    }

    @Nullable
    @Override
    public <T extends Annotation> MethodAnnotationHandler<T> getMethodAnnotationHandler(T annotation) {
        // noinspection unchecked
        return AnnotationHandlerHelper.getInstance().getAnnotationHandler(
                annotation,
                OkHttp3MethodAnnotationHandlerDelegate.class,
                MethodAnnotationHandler.class,
                cacheAble ? OkHttpMethodAnnotationHandlerHolder.instance : null
        );
    }

    @SuppressWarnings("unused")
    @Handler(Headers.class)
    public static class HeadersHandler extends MethodAnnotationHandler<Headers> {

        @NonNull
        @Override
        public RequestModel handle(@NonNull Headers headers,
                @NonNull RequestModel requestModel, @NonNull MethodModel methodModel) {
            String[] headerArray = headers.value();
            if (headerArray.length == 0) {
                throw new IllegalArgumentException("@Headers annotation is empty.");
            }
            for (String header : headerArray) {
                if (header == null) {
                    throw new IllegalArgumentException("@Headers value could not be null.");
                }
                int colon = header.indexOf(':');
                if (colon == -1 || colon == 0 || colon == header.length() - 1) {
                    throw new IllegalArgumentException(String.format(
                            "@Headers value must be in the form \"Name: Value\". Found: \"%s\"", header));
                }
                String headerName = header.substring(0, colon).trim();
                String headerValue = header.substring(colon + 1).trim();
                headerName = ConvertUtils.convertString(headerName);
                headerValue = ConvertUtils.convertString(headerValue);
                requestModel.addHeader(headerName, headerValue);
            }
            return requestModel;
        }
    }

    private static class OkHttpMethodAnnotationHandlerHolder
            extends AnnotationHandlerHelper.AnnotationHandlerHolder<MethodAnnotationHandler> {
        static OkHttpMethodAnnotationHandlerHolder instance = new OkHttpMethodAnnotationHandlerHolder();
    }
}
