package com.x8.brick.annotation.handler;

import android.support.annotation.Nullable;

import com.x8.brick.annotation.define.type.Handler;
import com.x8.brick.core.RequestModel;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AnnotationHandlerHelper {

    public static AnnotationHandlerHelper getInstance() {
        return InstanceHolder.instance;
    }

    private AnnotationHandlerHelper() {
    }

    public RequestModel handleDefaultTypeAnnotation(MethodModel methodModel,
            Annotation annotation, RequestModel requestModel) {
        return handleDefaultTypeAnnotation(methodModel, annotation, requestModel, true);
    }

    public RequestModel handleDefaultTypeAnnotation(MethodModel methodModel,
            Annotation annotation, RequestModel requestModel, boolean cacheAble) {
        if (requestModel == null) {
            throw new IllegalArgumentException("Request model is null, please check your annotation handler.");
        }
        TypeAnnotationHandler handler = getDefaultTypeAnnotationHandler(annotation, cacheAble);
        if (handler != null) {
            // noinspection unchecked
            requestModel = handler.handle(annotation, requestModel, methodModel);
        }
        return requestModel;
    }

    public RequestModel handleDefaultMethodAnnotation(MethodModel methodModel,
            Annotation annotation, RequestModel requestModel) {
        return handleDefaultMethodAnnotation(methodModel, annotation, requestModel, true);
    }

    public RequestModel handleDefaultMethodAnnotation(MethodModel methodModel,
            Annotation annotation, RequestModel requestModel, boolean cacheAble) {
        if (requestModel == null) {
            throw new IllegalArgumentException("Request model is null, please check your annotation handler.");
        }
        MethodAnnotationHandler handler = getDefaultMethodAnnotationHandler(annotation, cacheAble);
        if (handler != null) {
            // noinspection unchecked
            requestModel = handler.handle(annotation, requestModel, methodModel);
        }
        return requestModel;
    }

    public RequestModel handleDefaultParameterAnnotation(MethodModel methodModel,
            Annotation annotation, @Nullable Object param, RequestModel requestModel) {
        return handleDefaultParameterAnnotation(methodModel, annotation, param, requestModel, true);
    }

    public RequestModel handleDefaultParameterAnnotation(MethodModel methodModel,
            Annotation annotation, @Nullable Object param, RequestModel requestModel, boolean cacheAble) {
        if (requestModel == null) {
            throw new IllegalArgumentException("Request model is null, please check your annotation handler.");
        }
        ParameterAnnotationHandler handler = getDefaultParameterAnnotationHandler(annotation, cacheAble);
        if (handler != null) {
            // noinspection unchecked
            requestModel = handler.handle(annotation, param, requestModel, methodModel);
        }
        return requestModel;
    }

    public TypeAnnotationHandler getDefaultTypeAnnotationHandler(Annotation annotation) {
        return getDefaultTypeAnnotationHandler(annotation, true);
    }

    public TypeAnnotationHandler getDefaultTypeAnnotationHandler(Annotation annotation, boolean cacheAble) {
        return getAnnotationHandler(
                annotation,
                TypeAnnotationHandler.class,
                TypeAnnotationHandler.class,
                cacheAble ? TypeAnnotationHandlerHolder.instance : null
        );
    }

    public MethodAnnotationHandler getDefaultMethodAnnotationHandler(Annotation annotation) {
        return getDefaultMethodAnnotationHandler(annotation, true);
    }

    public MethodAnnotationHandler getDefaultMethodAnnotationHandler(Annotation annotation, boolean cacheAble) {
        return getAnnotationHandler(
                annotation,
                MethodAnnotationHandler.class,
                MethodAnnotationHandler.class,
                cacheAble ? MethodAnnotationHandlerHolder.instance : null
        );
    }

    public ParameterAnnotationHandler getDefaultParameterAnnotationHandler(Annotation annotation) {
        return getDefaultParameterAnnotationHandler(annotation, true);
    }

    public ParameterAnnotationHandler getDefaultParameterAnnotationHandler(Annotation annotation, boolean cacheAble) {
        return getAnnotationHandler(
                annotation,
                ParameterAnnotationHandler.class,
                ParameterAnnotationHandler.class,
                cacheAble ? ParameterAnnotationHandlerHolder.instance : null
        );
    }

    public <T> T getAnnotationHandler(Annotation annotation, Class<?> targetClass, Type targetType) {
        if (annotation == null || targetClass == null || targetType == null) {
            return null;
        }
        return getAnnotationHandler(annotation.annotationType(), targetClass, targetType);
    }

    public <T> T getAnnotationHandler(
            Class<? extends Annotation> annotationType, Class<?> targetClass, Type targetType) {
        if (annotationType == null || targetClass == null || targetType == null) {
            return null;
        }
        T handler = createAnnotationHandler(annotationType, targetClass, targetType);
        if (handler == null && annotationType != Annotation.class) {
            handler = createAnnotationHandler(Annotation.class, targetClass, targetType);
        }
        return handler;
    }

    public <T, H extends AnnotationHandlerHolder<T>> T getAnnotationHandler(
            Annotation annotation, Class<?> targetClass, Type targetType, H holder) {
        if (annotation == null || targetClass == null || targetType == null) {
            return null;
        }
        return getAnnotationHandler(annotation.annotationType(), targetClass, targetType, holder);
    }

    public <T, H extends AnnotationHandlerHolder<T>> T getAnnotationHandler(
            Class<? extends Annotation> annotationType, Class<?> targetClass, Type targetType, H holder) {
        if (annotationType == null || targetClass == null || targetType == null) {
            return null;
        }
        if (holder == null) {
            return getAnnotationHandler(annotationType, targetClass, targetType);
        }
        if (holder.hasHandler(annotationType)) {
            return holder.get(annotationType);
        }
        T handlerHolder = getAnnotationHandler(annotationType, targetClass, targetType);
        holder.put(annotationType, handlerHolder);
        return handlerHolder;
    }

    public <T> T createAnnotationHandler(Annotation annotation, Class<?> targetClass, Type targetType) {
        if (annotation == null || targetClass == null || targetType == null) {
            return null;
        }
        return createAnnotationHandler(annotation.annotationType(), targetClass, targetType);
    }

    public <T> T createAnnotationHandler(
            Class<? extends Annotation> annotationType, Class<?> targetClass, Type targetType) {
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
                    T annotationHandler = createAnnotationHandler(annotationType, clazz, targetType);
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

    public static class MethodModel {
        public final Object object;
        public final Method method;
        public final Object[] parameters;

        public MethodModel(Object object, Method method, Object[] parameters) {
            this.object = object;
            this.method = method;
            this.parameters = parameters;
        }

        @Override
        public String toString() {
            return "MethodModel{" +
                    "object=" + object +
                    ", method=" + method +
                    ", parameters=" + Arrays.toString(parameters) +
                    '}';
        }
    }

    public abstract static class AnnotationHandlerHolder<T> {

        private Map<Class<? extends Annotation>, T> handlers;

        public AnnotationHandlerHolder() {
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

    private static class TypeAnnotationHandlerHolder extends AnnotationHandlerHolder<TypeAnnotationHandler> {
        static TypeAnnotationHandlerHolder instance = new TypeAnnotationHandlerHolder();
    }

    private static class MethodAnnotationHandlerHolder extends AnnotationHandlerHolder<MethodAnnotationHandler> {
        static MethodAnnotationHandlerHolder instance = new MethodAnnotationHandlerHolder();
    }

    private static class ParameterAnnotationHandlerHolder extends AnnotationHandlerHolder<ParameterAnnotationHandler> {
        static ParameterAnnotationHandlerHolder instance = new ParameterAnnotationHandlerHolder();
    }

    private static class InstanceHolder {
        static AnnotationHandlerHelper instance = new AnnotationHandlerHelper();
    }
}
