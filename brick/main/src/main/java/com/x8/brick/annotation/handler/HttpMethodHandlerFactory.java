package com.x8.brick.annotation.handler;

import android.support.annotation.NonNull;

import com.x8.brick.annotation.define.method.DELETE;
import com.x8.brick.annotation.define.method.FormUrlEncoded;
import com.x8.brick.annotation.define.method.GET;
import com.x8.brick.annotation.define.method.HEAD;
import com.x8.brick.annotation.define.method.HTTP;
import com.x8.brick.annotation.define.method.Headers;
import com.x8.brick.annotation.define.method.Multipart;
import com.x8.brick.annotation.define.method.OPTIONS;
import com.x8.brick.annotation.define.method.PATCH;
import com.x8.brick.annotation.define.method.POST;
import com.x8.brick.annotation.define.method.PUT;
import com.x8.brick.annotation.define.method.Streaming;
import com.x8.brick.annotation.define.type.Handler;
import com.x8.brick.core.MethodModel;
import com.x8.brick.core.RequestModel;
import com.x8.brick.utils.ConvertUtils;
import com.x8.brick.utils.HandlerUtils;

import java.lang.annotation.Annotation;

public class HttpMethodHandlerFactory implements MethodHandlerFactory {

    @Override
    public MethodHandler create(Annotation annotation) {
        return HandlerUtils.findHandler(annotation, HttpMethodHandlerFactory.class, MethodHandler.class, true);
    }

    @SuppressWarnings("unused")
    @Handler(GET.class)
    public static class GetHandler implements MethodHandler<GET> {
        @NonNull
        @Override
        public RequestModel handle(@NonNull GET get,
                                   @NonNull RequestModel requestModel,
                                   @NonNull MethodModel methodModel) {
            return requestModel.addMethodPath(RequestModel.HttpMethod.GET,
                    ConvertUtils.convertString(get.value()), false);
        }
    }

    @SuppressWarnings("unused")
    @Handler(POST.class)
    public static class PostHandler implements MethodHandler<POST> {
        @NonNull
        @Override
        public RequestModel handle(@NonNull POST post,
                                   @NonNull RequestModel requestModel,
                                   @NonNull MethodModel methodModel) {
            return requestModel.addMethodPath(RequestModel.HttpMethod.POST,
                    ConvertUtils.convertString(post.value()), true);
        }
    }

    @SuppressWarnings("unused")
    @Handler(PUT.class)
    public static class PutHandler implements MethodHandler<PUT> {
        @NonNull
        @Override
        public RequestModel handle(@NonNull PUT put,
                                   @NonNull RequestModel requestModel,
                                   @NonNull MethodModel methodModel) {
            return requestModel.addMethodPath(RequestModel.HttpMethod.PUT,
                    ConvertUtils.convertString(put.value()), true);
        }
    }

    @SuppressWarnings("unused")
    @Handler(HEAD.class)
    public static class HeadHandler implements MethodHandler<HEAD> {
        @NonNull
        @Override
        public RequestModel handle(@NonNull HEAD head,
                                   @NonNull RequestModel requestModel,
                                   @NonNull MethodModel methodModel) {
            return requestModel.addMethodPath(RequestModel.HttpMethod.HEAD,
                    ConvertUtils.convertString(head.value()), false);
        }
    }

    @SuppressWarnings("unused")
    @Handler(DELETE.class)
    public static class DeleteHandler implements MethodHandler<DELETE> {
        @NonNull
        @Override
        public RequestModel handle(@NonNull DELETE delete,
                                   @NonNull RequestModel requestModel,
                                   @NonNull MethodModel methodModel) {
            return requestModel.addMethodPath(RequestModel.HttpMethod.DELETE,
                    ConvertUtils.convertString(delete.value()), false);
        }
    }

    @SuppressWarnings("unused")
    @Handler(OPTIONS.class)
    public static class OptionsHandler implements MethodHandler<OPTIONS> {
        @NonNull
        @Override
        public RequestModel handle(@NonNull OPTIONS options,
                                   @NonNull RequestModel requestModel,
                                   @NonNull MethodModel methodModel) {
            return requestModel.addMethodPath(RequestModel.HttpMethod.OPTIONS,
                    ConvertUtils.convertString(options.value()), false);
        }
    }

    @SuppressWarnings("unused")
    @Handler(PATCH.class)
    public static class PatchHandler implements MethodHandler<PATCH> {
        @NonNull
        @Override
        public RequestModel handle(@NonNull PATCH patch,
                                   @NonNull RequestModel requestModel,
                                   @NonNull MethodModel methodModel) {
            return requestModel.addMethodPath(RequestModel.HttpMethod.PATCH,
                    ConvertUtils.convertString(patch.value()), true);
        }
    }

    @SuppressWarnings("unused")
    @Handler(HTTP.class)
    public static class HttpHandler implements MethodHandler<HTTP> {
        @NonNull
        @Override
        public RequestModel handle(@NonNull HTTP http,
                                   @NonNull RequestModel requestModel,
                                   @NonNull MethodModel methodModel) {
            return requestModel.addMethodPath(http.method(),
                    ConvertUtils.convertString(http.path()), http.hasBody());
        }
    }

    @SuppressWarnings("unused")
    @Handler(Headers.class)
    public static class HeadersHandler implements MethodHandler<Headers> {
        @NonNull
        @Override
        public RequestModel handle(@NonNull Headers headers,
                                   @NonNull RequestModel requestModel,
                                   @NonNull MethodModel methodModel) {
            for (String header : headers.value()) {
                header = ConvertUtils.convertString(header);
                String key;
                String value;
                int colon = header.indexOf(':');
                if (header.equals(":")) {
                    key = "";
                    value = "";
                } else if (colon == -1 || colon == header.length() - 1) {
                    key = header;
                    value = "";
                } else if (colon == 0) {
                    key = "";
                    value = header;
                } else {
                    key = header.substring(0, colon).trim();
                    value = header.substring(colon + 1).trim();
                }
                key = ConvertUtils.convertString(key);
                value = ConvertUtils.convertString(value);
                requestModel.addHeader(key, value);
            }
            return requestModel;
        }
    }

    @SuppressWarnings("unused")
    @Handler(FormUrlEncoded.class)
    public static class FormUrlEncodedHandler implements MethodHandler<FormUrlEncoded> {
        @NonNull
        @Override
        public RequestModel handle(@NonNull FormUrlEncoded formUrlEncoded,
                                   @NonNull RequestModel requestModel,
                                   @NonNull MethodModel methodModel) {
            return requestModel.addFormUrlEncoded(true);
        }
    }

    @SuppressWarnings("unused")
    @Handler(Multipart.class)
    public static class MultipartHandler implements MethodHandler<Multipart> {
        @NonNull
        @Override
        public RequestModel handle(@NonNull Multipart multipart,
                                   @NonNull RequestModel requestModel,
                                   @NonNull MethodModel methodModel) {
            return requestModel.addMultipart(true);
        }
    }

    @SuppressWarnings("unused")
    @Handler(Streaming.class)
    public static class StreamingHandler implements MethodHandler<Streaming> {
        @NonNull
        @Override
        public RequestModel handle(@NonNull Streaming streaming,
                                   @NonNull RequestModel requestModel,
                                   @NonNull MethodModel methodModel) {
            return requestModel.addStreaming(true);
        }
    }

    @SuppressWarnings("unused")
    @Handler(Annotation.class)
    public static class AnnotationHandler implements MethodHandler {
        @NonNull
        @Override
        public RequestModel handle(@NonNull Annotation annotation,
                                   @NonNull RequestModel requestModel,
                                   @NonNull MethodModel methodModel) {
            return requestModel;
        }
    }
}
