package com.x8.brick.annotation.handler;

import android.support.annotation.NonNull;

import com.x8.brick.core.MethodModel;
import com.x8.brick.core.RequestModel;

import java.lang.annotation.Annotation;

public interface TypeHandler<T extends Annotation> {

    @NonNull
    RequestModel handle(@NonNull T annotation,
                        @NonNull RequestModel requestModel,
                        @NonNull MethodModel methodModel);
}
