package com.x8.brick.utils;

import com.x8.brick.converter.Converter;

import java.lang.reflect.Type;
import java.util.List;

public final class ConvertUtils {

    private ConvertUtils() {
        throw new UnsupportedOperationException("u can't instantiate me...");
    }

    public static <F, T, C extends Converter<F, T>> T convert(F value, Type type, List<C> converters) {
        if (converters != null) {
            for (Converter<F, T> converter : converters) {
                T result = converter.convert(value, type);
                if (result != null) {
                    return result;
                }
            }
        }
        // noinspection unchecked
        return (T) value;
    }

    public static String convertString(Object value) {
        return (value == null) ? "" : String.valueOf(value);
    }
}
