package com.x8.brick.annotation.checker;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;

public interface Checker {
    ElementType[] effectiveTarget(Annotation annotation);
}
