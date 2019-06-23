package com.x8.brick.utils;

import com.x8.brick.annotation.define.type.Handler;
import com.x8.brick.annotation.handler.HandlerFactory;
import com.x8.brick.annotation.handler.HandlerHolder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

public final class HandlerUtils {

    private HandlerUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static <T> T getHandler(Annotation annotation,
                                   List<HandlerFactory<T>> factories,
                                   HandlerHolder<T> holder) {
        T handler = null;
        if (holder != null) {
            handler = holder.get(annotation);
        }
        if (handler == null && factories != null) {
            for (HandlerFactory<T> factory : factories) {
                handler = factory.create(annotation);
                if (handler != null) {
                    if (holder != null) {
                        holder.put(annotation, handler);
                    }
                    break;
                }
            }
        }
        return handler;
    }

    public static <T> T findHandler(Annotation annotation, Class<?> targetClass, Type targetType) {
        if (annotation == null || targetClass == null || targetType == null) {
            return null;
        }
        return findHandler(annotation.annotationType(), targetClass, targetType);
    }

    public static <T> T findHandler(Class<? extends Annotation> annotationType,
                                    Class<?> targetClass,
                                    Type targetType) {
        if (annotationType != null && targetClass != null && targetType != null) {
            try {
                Handler handler = targetClass.getAnnotation(Handler.class);
                // noinspection unchecked
                if (handler != null && annotationType == handler.value()
                        && ((Class) targetType).isAssignableFrom(targetClass)) {
                    // noinspection unchecked
                    return (T) targetClass.newInstance();
                }
                for (Class<?> clazz : targetClass.getDeclaredClasses()) {
                    T annotationHandler = findHandler(annotationType, clazz, targetType);
                    if (annotationHandler != null) {
                        return annotationHandler;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static <T> T findHandler(Annotation annotation,
                                     Class<?> targetClass,
                                     Type targetType,
                                     boolean findDefault) {
        if ((annotation == null && !findDefault) || targetClass == null || targetType == null) {
            return null;
        }
        Class<? extends Annotation> annotationType = (annotation == null)
                ? Annotation.class
                : annotation.annotationType();
        return findHandler(annotationType, targetClass, targetType, findDefault);
    }

    public static <T> T findHandler(Class<? extends Annotation> annotationType,
                                     Class<?> targetClass,
                                     Type targetType,
                                     boolean findDefault) {
        T handler = findHandler(annotationType, targetClass, targetType);
        if (handler == null && findDefault && annotationType != Annotation.class) {
            handler = findHandler(Annotation.class, targetClass, targetType);
        }
        return handler;
    }
}
