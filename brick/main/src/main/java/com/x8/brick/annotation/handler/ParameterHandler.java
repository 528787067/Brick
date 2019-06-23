package com.x8.brick.annotation.handler;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.x8.brick.core.MethodModel;
import com.x8.brick.core.RequestModel;

import java.lang.annotation.Annotation;

public interface ParameterHandler<T extends Annotation> {

    @NonNull
    RequestModel handle(@NonNull T annotation,
                        @Nullable Object parameter,
                        @NonNull RequestModel requestModel,
                        @NonNull MethodModel methodModel);
}
