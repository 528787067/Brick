package com.x8.brick.core;

import java.lang.reflect.Method;
import java.util.Arrays;

public class MethodModel {

    private final Object object;
    private final Method method;
    private final Object[] parameters;

    public MethodModel(Object object, Method method, Object[] parameters) {
        this.object = object;
        this.method = method;
        this.parameters = parameters;
    }

    public Object object() {
        return object;
    }

    public Method method() {
        return method;
    }

    public Object[] parameters() {
        return parameters;
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
