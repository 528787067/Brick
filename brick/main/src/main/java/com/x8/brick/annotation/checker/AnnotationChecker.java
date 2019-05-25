package com.x8.brick.annotation.checker;

import com.x8.brick.annotation.define.type.Checkable;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

public class AnnotationChecker implements Checker {
    @Override
    public ElementType[] effectiveTarget(Annotation annotation) {
        if (annotation != null) {
            Class<? extends Annotation> annotationType = annotation.annotationType();
            if (annotationType.getAnnotation(Checkable.class) != null) {
                Target target = annotationType.getAnnotation(Target.class);
                if (target != null) {
                    return target.value();
                }
            }
        }
        return null;
    }
}
