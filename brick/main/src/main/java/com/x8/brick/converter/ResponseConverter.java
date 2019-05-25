package com.x8.brick.converter;

import com.x8.brick.parameter.Response;

import java.lang.reflect.Type;

public interface ResponseConverter<OBJECT extends Response, RESPONSE> extends Converter<OBJECT, RESPONSE> {
    @Override
    RESPONSE convert(OBJECT response, Type type);
}
