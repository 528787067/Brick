package com.x8.brick.annotation.define.method;

import com.x8.brick.annotation.define.type.Checkable;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Checkable
@Documented
@Target(METHOD)
@Retention(RUNTIME)
public @interface Headers {
    String[] value();
}
