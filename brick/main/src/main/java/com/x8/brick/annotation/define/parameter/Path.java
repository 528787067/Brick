package com.x8.brick.annotation.define.parameter;

import com.x8.brick.annotation.define.type.Checkable;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Checkable
@Documented
@Target(PARAMETER)
@Retention(RUNTIME)
public @interface Path {
    String value();
    boolean encoded() default false;
}
