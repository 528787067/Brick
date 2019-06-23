package com.x8.brick.annotation.handler;

import java.lang.annotation.Annotation;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HandlerHolder<T> {

    private Map<Class<? extends Annotation>, T> handlers;

    public HandlerHolder() {
        handlers = new ConcurrentHashMap<>();
    }

    public T get(Annotation annotation) {
        return annotation == null ? null : get(annotation.annotationType());
    }

    public T get(Class<? extends Annotation> annotationType) {
        return annotationType == null ? null : handlers.get(annotationType);
    }

    public void put(Annotation annotation, T handler) {
        if (annotation != null && handler != null) {
            put(annotation.annotationType(), handler);
        }
    }

    public void put(Class<? extends Annotation> annotationType, T handler) {
        if (annotationType != null && handler != null) {
            handlers.put(annotationType, handler);
        }
    }

    public boolean hasHandler(Annotation annotation) {
        return annotation != null && hasHandler(annotation.annotationType());
    }

    public boolean hasHandler(Class<? extends Annotation> annotationType) {
        return annotationType != null && handlers.containsKey(annotationType);
    }
}
