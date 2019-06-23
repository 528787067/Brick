package com.x8.brick.annotation.handler;

import java.lang.annotation.Annotation;

public interface HandlerFactory<T> {
    T create(Annotation annotation);
}
