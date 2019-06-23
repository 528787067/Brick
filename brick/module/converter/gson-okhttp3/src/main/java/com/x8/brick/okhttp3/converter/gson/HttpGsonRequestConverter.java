package com.x8.brick.okhttp3.converter.gson;

import android.support.annotation.NonNull;

import com.x8.brick.converter.Converter;
import com.google.gson.Gson;
import com.x8.brick.converter.gson.GsonRequestConverter;

import java.lang.reflect.Type;

import okhttp3.MediaType;
import okhttp3.RequestBody;

public class HttpGsonRequestConverter<T> extends GsonRequestConverter<T, RequestBody> {

    private static final MediaType MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8");

    public HttpGsonRequestConverter() {
        this(new Gson());
    }

    public HttpGsonRequestConverter(@NonNull Gson gson) {
        super(gson, new Converter<String, RequestBody>() {
            @Override
            public RequestBody convert(String value, Type type) {
                return RequestBody.create(MEDIA_TYPE, value);
            }
        });
    }
}
