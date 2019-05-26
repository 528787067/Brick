package com.x8.brick.module.converter.gson;

import android.support.annotation.NonNull;

import com.x8.brick.converter.Converter;
import com.x8.brick.converter.ResponseConverter;
import com.x8.brick.parameter.Response;
import com.google.gson.Gson;

import java.lang.reflect.Type;

public class GsonResponseConverter<OBJECT extends Response, RESPONSE> implements ResponseConverter<OBJECT, RESPONSE> {

    private Gson gson;
    private Converter<OBJECT, String> bodyConverter;

    public GsonResponseConverter() {
        this(new Gson());
    }

    public GsonResponseConverter(@NonNull Gson gson) {
        this(gson, new Converter<OBJECT, String>() {
            @Override
            public String convert(OBJECT value, Type type) {
                return String.valueOf(value);
            }
        });
    }

    public GsonResponseConverter(@NonNull Gson gson, @NonNull Converter<OBJECT, String> bodyConverter) {
        this.gson = gson;
        this.bodyConverter = bodyConverter;
    }

    protected String convertBody(OBJECT responseBody, Type type) {
        return bodyConverter.convert(responseBody, type);
    }

    @Override
    public RESPONSE convert(OBJECT response, Type type) {
        return gson.fromJson(convertBody(response, type), type);
    }
}
