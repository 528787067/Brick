package com.x8.brick.converter;

import java.lang.reflect.Type;

public interface Converter<F, T> {
    T convert(F value, Type type);
}
