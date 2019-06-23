package com.x8.brick.okhttp3;

import android.support.annotation.NonNull;

import com.x8.brick.annotation.define.method.Headers;
import com.x8.brick.annotation.define.type.Handler;
import com.x8.brick.annotation.handler.MethodHandler;
import com.x8.brick.annotation.handler.MethodHandlerFactory;
import com.x8.brick.core.MethodModel;
import com.x8.brick.core.RequestModel;
import com.x8.brick.utils.ConvertUtils;
import com.x8.brick.utils.HandlerUtils;

import java.lang.annotation.Annotation;

public class HttpMethodHandlerFactory implements MethodHandlerFactory {

    @Override
    public MethodHandler create(Annotation annotation) {
        return HandlerUtils.findHandler(annotation, HttpMethodHandlerFactory.class, MethodHandler.class);
    }

    @SuppressWarnings("unused")
    @Handler(Headers.class)
    public static class HeadersHandler implements MethodHandler<Headers> {

        @NonNull
        @Override
        public RequestModel handle(@NonNull Headers headers,
                                   @NonNull RequestModel requestModel,
                                   @NonNull MethodModel methodModel) {
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
}
