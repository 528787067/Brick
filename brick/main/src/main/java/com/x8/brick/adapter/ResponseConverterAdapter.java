package com.x8.brick.adapter;

import com.x8.brick.converter.ResponseConverter;
import com.x8.brick.parameter.Response;
import com.x8.brick.utils.ConvertUtils;

import java.lang.reflect.Type;
import java.util.List;

public class ResponseConverterAdapter<OBJECT extends Response, RESPONSE> implements ResponseAdapter<OBJECT, RESPONSE> {

    private List<ResponseConverter<OBJECT, RESPONSE>> responseConverters;

    public ResponseConverterAdapter(List<ResponseConverter<OBJECT, RESPONSE>> responseConverters) {
        this.responseConverters = responseConverters;
    }

    @Override
    public RESPONSE adapt(OBJECT response, Type type) {
        return ConvertUtils.convert(response, type, responseConverters);
    }
}
