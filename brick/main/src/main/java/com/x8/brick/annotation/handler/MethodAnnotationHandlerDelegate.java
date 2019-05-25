package com.x8.brick.annotation.handler;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.x8.brick.annotation.handler.AnnotationHandlerHelper.MethodModel;
import com.x8.brick.core.RequestModel;

import java.lang.annotation.Annotation;

public abstract class MethodAnnotationHandlerDelegate {

    @Nullable
    public abstract <T extends Annotation> MethodAnnotationHandler<T> getMethodAnnotationHandler(T annotation);

    @NonNull
    public RequestModel handleAnnotation(MethodModel methodModel,
            Annotation annotation, RequestModel requestModel) {
        return handleAnnotation(methodModel, annotation, requestModel, true);
    }

    @NonNull
    public RequestModel handleAnnotation(MethodModel methodModel,
            Annotation annotation, RequestModel requestModel, boolean cacheAble) {
        if (requestModel == null) {
            throw new IllegalArgumentException("Request model is null, please check your annotation handler.");
        }
        MethodAnnotationHandler handler = getMethodAnnotationHandler(annotation);
        if (handler == null) {
            handler = AnnotationHandlerHelper.getInstance()
                    .getDefaultMethodAnnotationHandler(annotation, cacheAble);
        }
        if (handler != null) {
            // noinspection unchecked
            requestModel = handler.handle(annotation, requestModel, methodModel);
        }
        return requestModel;
    }
}
