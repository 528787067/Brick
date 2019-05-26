package com.x8.brick.module.converter.gson;

import android.support.annotation.NonNull;

import com.x8.brick.converter.Converter;
import com.x8.brick.converter.RequestConverter;
import com.google.gson.Gson;

import java.lang.reflect.Type;

public class GsonRequestConverter<OBJECT, REQUEST> implements RequestConverter<OBJECT, REQUEST> {

    private Gson gson;
    private Converter<String, REQUEST> bodyConverter;

    public GsonRequestConverter() {
        this(new Gson());
    }

    public GsonRequestConverter(@NonNull Gson gson) {
        this(gson, new Converter<String, REQUEST>() {
            @Override
            public REQUEST convert(String value, Type type) {
                return (REQUEST) value;
            }
        });
    }

    public GsonRequestConverter(@NonNull Gson gson, @NonNull Converter<String, REQUEST> bodyConverter) {
        this.gson = gson;
        this.bodyConverter = bodyConverter;
    }

    protected REQUEST convertBody(String requestBody, Type type) {
        return bodyConverter.convert(requestBody, type);
    }

    @Override
    public REQUEST convert(OBJECT request, Type type) {
        return convertBody(gson.toJson(request, type), type);
    }
}
