package com.x8.brick.utils;

import com.x8.brick.annotation.checker.Checker;
import com.x8.brick.annotation.define.type.Checkable;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.List;

public final class AnnotationUtils {

    public static boolean hasTypeAnnotation(Method method) {
        return hasTargetAnnotation(method, ElementType.TYPE);
    }

    public static boolean hasTypeAnnotation(Method method, List<Checker> checkers) {
        return hasTargetAnnotation(method, ElementType.TYPE, checkers);
    }

    public static boolean hasMethodAnnotation(Method method) {
        return hasTargetAnnotation(method, ElementType.METHOD);
    }

    public static boolean hasMethodAnnotation(Method method, List<Checker> checkers) {
        return hasTargetAnnotation(method, ElementType.METHOD, checkers);
    }

    public static boolean hasParameterAnnotation(Method method) {
        return hasTargetAnnotation(method, ElementType.PARAMETER);
    }

    public static boolean hasParameterAnnotation(Method method, List<Checker> checkers) {
        return hasTargetAnnotation(method, ElementType.PARAMETER, checkers);
    }

    public static boolean hasEffectiveAnnotation(Method method) {
        return hasTypeAnnotation(method)
                || hasMethodAnnotation(method)
                || hasParameterAnnotation(method);
    }

    public static boolean hasEffectiveAnnotation(Method method, List<Checker> checkers) {
        return hasTypeAnnotation(method, checkers)
                || hasMethodAnnotation(method, checkers)
                || hasParameterAnnotation(method, checkers);
    }

    public static boolean isTypeAnnotation(Annotation annotation) {
        return isTargetAnnotation(annotation, ElementType.TYPE);
    }

    public static boolean isTypeAnnotation(Annotation annotation, List<Checker> checkers) {
        return isTargetAnnotation(annotation, ElementType.TYPE, checkers);
    }

    public static boolean isMethodAnnotation(Annotation annotation) {
        return isTargetAnnotation(annotation, ElementType.METHOD);
    }

    public static boolean isMethodAnnotation(Annotation annotation, List<Checker> checkers) {
        return isTargetAnnotation(annotation, ElementType.METHOD, checkers);
    }

    public static boolean isParameterAnnotation(Annotation annotation) {
        return isTargetAnnotation(annotation, ElementType.PARAMETER);
    }

    public static boolean isParameterAnnotation(Annotation annotation, List<Checker> checkers) {
        return isTargetAnnotation(annotation, ElementType.PARAMETER, checkers);
    }

    public static boolean isEffectiveAnnotation(Annotation annotation) {
        return isTypeAnnotation(annotation)
                || isMethodAnnotation(annotation)
                || isParameterAnnotation(annotation);
    }

    public static boolean isEffectiveAnnotation(Annotation annotation, List<Checker> checkers) {
        return isTypeAnnotation(annotation, checkers)
                || isMethodAnnotation(annotation, checkers)
                || isParameterAnnotation(annotation, checkers);
    }

    private static boolean hasTargetAnnotation(Method method, ElementType targetType) {
        if (method != null && targetType != null) {
            if (ElementType.TYPE.equals(targetType)) {
                return hasTargetAnnotation(method.getDeclaringClass().getAnnotations(), targetType);
            }
            if (ElementType.METHOD.equals(targetType)) {
                return hasTargetAnnotation(method.getAnnotations(), targetType);
            }
            if (ElementType.PARAMETER.equals(targetType)) {
                for (Annotation[] annotations : method.getParameterAnnotations()) {
                    if (hasTargetAnnotation(annotations, targetType)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean hasTargetAnnotation(Annotation[] annotations, ElementType targetType) {
        if (annotations != null && targetType != null) {
            for (Annotation annotation : annotations) {
                if (isTargetAnnotation(annotation, targetType)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean hasTargetAnnotation(Method method, ElementType targetType, List<Checker> checkers) {
        if (method != null && checkers != null) {
            if (ElementType.TYPE.equals(targetType)) {
                return hasTargetAnnotation(method.getDeclaringClass().getAnnotations(), targetType, checkers);
            }
            if (ElementType.METHOD.equals(targetType)) {
                return hasTargetAnnotation(method.getAnnotations(), targetType, checkers);
            }
            if (ElementType.PARAMETER.equals(targetType)) {
                for (Annotation[] annotations : method.getParameterAnnotations()) {
                    if (hasTargetAnnotation(annotations, targetType, checkers)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean hasTargetAnnotation(
            Annotation[] annotations, ElementType targetType,  List<Checker> checkers) {
        if (annotations != null && targetType != null && checkers != null) {
            for (Annotation annotation : annotations) {
                for (Checker checker : checkers) {
                    if (contains(targetType, checker.effectiveTarget(annotation))) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private static boolean isTargetAnnotation(Annotation annotation, ElementType targetType) {
        if (annotation != null && targetType != null) {
            Class<? extends Annotation> annotationType = annotation.annotationType();
            Target target = annotationType.getAnnotation(Target.class);
            Checkable checkable = annotationType.getAnnotation(Checkable.class);
            if (target != null && checkable != null) {
                return contains(targetType, target.value());
            }
        }
        return false;
    }

    private static boolean isTargetAnnotation(Annotation annotation, ElementType targetType, List<Checker> checkers) {
        if (annotation != null && checkers != null) {
            for (Checker checker : checkers) {
                if (contains(targetType, checker.effectiveTarget(annotation))) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean contains(ElementType targetType, ElementType[] targetTypes) {
        if (targetType != null && targetTypes != null) {
            for (ElementType type : targetTypes) {
                if (targetType.equals(type)) {
                    return true;
                }
            }
        }
        return false;
    }
}
