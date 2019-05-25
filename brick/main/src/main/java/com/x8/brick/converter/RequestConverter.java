package com.x8.brick.converter;

import java.lang.reflect.Type;

public interface RequestConverter<OBJECT, REQUEST> extends Converter<OBJECT, REQUEST> {
    @Override
    REQUEST convert(OBJECT request, Type type);
}
