package com.x8.brick.adapter;

import com.x8.brick.parameter.Response;

import java.lang.reflect.Type;

public interface ResponseAdapter<OBJECT extends Response, RESPONSE> extends Adapter<OBJECT, RESPONSE> {
    @Override
    RESPONSE adapt(OBJECT response, Type type);
}
