package com.x8.brick.annotation.handler;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.x8.brick.core.HttpManager;
import com.x8.brick.core.MethodModel;
import com.x8.brick.core.RequestModel;
import com.x8.brick.utils.HandlerUtils;

import java.lang.annotation.Annotation;

public class HandlerHelper {

    private HttpManager httpManager;
    private volatile HandlerHolder<TypeHandler> typeHandlerHolder;
    private volatile HandlerHolder<MethodHandler> methodHandlerHolder;
    private volatile HandlerHolder<ParameterHandler> parameterHandlerHolder;

    public HandlerHelper(@NonNull HttpManager httpManager) {
        this.httpManager = httpManager;
    }

    public boolean cacheAble() {
        return httpManager.handlerCacheAble();
    }

    public RequestModel handleTypeAnnotation(Annotation annotation,
                                             RequestModel requestModel,
                                             MethodModel methodModel) {
        checkRequestModel(requestModel);
        TypeHandler handler = getTypeHandler(annotation);
        // noinspection unchecked
        return handler == null ? requestModel : handler.handle(annotation, requestModel, methodModel);
    }

    public RequestModel handleMethodAnnotation(Annotation annotation,
                                               RequestModel requestModel,
                                               MethodModel methodModel) {
        checkRequestModel(requestModel);
        MethodHandler handler = getMethodHandler(annotation);
        // noinspection unchecked
        return handler == null ? requestModel : handler.handle(annotation, requestModel, methodModel);
    }

    public RequestModel handleParameterAnnotation(Annotation annotation,
                                                  @Nullable Object parameter,
                                                  RequestModel requestModel,
                                                  MethodModel methodModel) {
        checkRequestModel(requestModel);
        ParameterHandler handler = getParameterHandler(annotation);
        // noinspection unchecked
        return handler == null ? requestModel : handler.handle(annotation, parameter, requestModel, methodModel);
    }

    public TypeHandler getTypeHandler(Annotation annotation) {
        HandlerHolder<TypeHandler> holder = null;
        if (cacheAble()) {
            holder = getTypeHandlerHolder();
        }
        // noinspection unchecked
        return HandlerUtils.getHandler(annotation, httpManager.typeHandlerFactories(), holder);
    }

    public MethodHandler getMethodHandler(Annotation annotation) {
        HandlerHolder<MethodHandler> holder = null;
        if (cacheAble()) {
            holder = getMethodHandlerHolder();
        }
        // noinspection unchecked
        return HandlerUtils.getHandler(annotation, httpManager.methodHandlerFactories(), holder);
    }

    public ParameterHandler getParameterHandler(Annotation annotation) {
        HandlerHolder<ParameterHandler> holder = null;
        if (cacheAble()) {
            holder = getParameterHandlerHolder();
        }
        // noinspection unchecked
        return HandlerUtils.getHandler(annotation, httpManager.parameterHandlerFactories(), holder);
    }

    private void checkRequestModel(RequestModel requestModel) {
        if (requestModel == null) {
            throw new IllegalArgumentException("Request model is null, please check your annotation handler.");
        }
    }

    private HandlerHolder<TypeHandler> getTypeHandlerHolder() {
        if (typeHandlerHolder == null) {
            synchronized (this) {
                if (typeHandlerHolder == null) {
                    typeHandlerHolder = new HandlerHolder<>();
                }
            }
        }
        return typeHandlerHolder;
    }

    private HandlerHolder<MethodHandler> getMethodHandlerHolder() {
        if (methodHandlerHolder == null) {
            synchronized (this) {
                if (methodHandlerHolder == null) {
                    methodHandlerHolder = new HandlerHolder<>();
                }
            }
        }
        return methodHandlerHolder;
    }

    private HandlerHolder<ParameterHandler> getParameterHandlerHolder() {
        if (parameterHandlerHolder == null) {
            synchronized (this) {
                if (parameterHandlerHolder == null) {
                    parameterHandlerHolder = new HandlerHolder<>();
                }
            }
        }
        return parameterHandlerHolder;
    }
}
