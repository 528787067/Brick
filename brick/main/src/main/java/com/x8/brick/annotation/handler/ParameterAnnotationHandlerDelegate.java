package com.x8.brick.annotation.handler;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.x8.brick.annotation.handler.AnnotationHandlerHelper.MethodModel;
import com.x8.brick.core.RequestModel;

import java.lang.annotation.Annotation;

public abstract class ParameterAnnotationHandlerDelegate {

    @Nullable
    public abstract <T extends Annotation> ParameterAnnotationHandler<T> getParameterAnnotationHandler(T annotation);

    @NonNull
    public RequestModel handleAnnotation(MethodModel methodModel,
            Annotation annotation, @Nullable Object parameter, RequestModel requestModel) {
        return handleAnnotation(methodModel, annotation, parameter, requestModel, true);
    }

    @NonNull
    public RequestModel handleAnnotation(MethodModel methodModel,
            Annotation annotation, @Nullable Object parameter, RequestModel requestModel, boolean cacheAble) {
        if (requestModel == null) {
            throw new IllegalArgumentException("Request model is null, please check your annotation handler.");
        }
        ParameterAnnotationHandler handler = getParameterAnnotationHandler(annotation);
        if (handler == null) {
            handler = AnnotationHandlerHelper.getInstance()
                    .getDefaultParameterAnnotationHandler(annotation, cacheAble);
        }
        if (handler != null) {
            // noinspection unchecked
            requestModel = handler.handle(annotation, parameter, requestModel, methodModel);
        }
        return requestModel;
    }
}
