package com.x8.brick.adapter;

import com.x8.brick.converter.RequestConverter;
import com.x8.brick.utils.ConvertUtils;

import java.lang.reflect.Type;
import java.util.List;

public class RequestConverterAdapter<OBJECT, REQUEST> implements RequestAdapter<OBJECT, REQUEST> {

    private List<RequestConverter<OBJECT, REQUEST>> requestConverters;

    public RequestConverterAdapter(List<RequestConverter<OBJECT, REQUEST>> requestConverters) {
        this.requestConverters = requestConverters;
    }

    @Override
    public REQUEST adapt(OBJECT request, Type type) {
        return ConvertUtils.convert(request, type, requestConverters);
    }
}
