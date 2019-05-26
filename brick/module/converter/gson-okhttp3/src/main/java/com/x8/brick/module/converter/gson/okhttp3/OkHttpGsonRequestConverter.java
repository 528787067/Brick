package com.x8.brick.module.converter.gson.okhttp3;

import android.support.annotation.NonNull;

import com.x8.brick.converter.Converter;
import com.google.gson.Gson;
import com.x8.brick.module.converter.gson.GsonRequestConverter;

import java.lang.reflect.Type;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class OkHttpGsonRequestConverter<T> extends GsonRequestConverter<T, RequestBody> {

    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");

    public OkHttpGsonRequestConverter() {
        this(new Gson());
    }

    public OkHttpGsonRequestConverter(@NonNull Gson gson) {
        super(gson, new Converter<String, RequestBody>() {
            @Override
            public RequestBody convert(String value, Type type) {
                return RequestBody.create(MEDIA_TYPE, value);
            }
        });
    }
}
