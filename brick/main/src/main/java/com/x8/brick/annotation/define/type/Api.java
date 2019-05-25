package com.x8.brick.annotation.define.type;

import com.x8.brick.core.RequestModel;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Checkable
@Documented
@Target(TYPE)
@Retention(RUNTIME)
public @interface Api {
    String value() default "";
    String release() default "";
    String debug() default "";
    String online() default "";
    String dev() default "";
    String test() default "";
    String sandbox() default "";
    String product() default "";
    String preview() default "";
    String[] hosts() default {};
    String hostName() default RequestModel.HostName.DEFAULT;
}
