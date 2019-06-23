package com.x8.brick.okhttp3.converter.gson;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.x8.brick.converter.Converter;
import com.x8.brick.converter.gson.GsonResponseConverter;
import com.x8.brick.okhttp3.HttpResponse;

import java.io.IOException;
import java.lang.reflect.Type;

public class HttpGsonResponseConverter<T> extends GsonResponseConverter<HttpResponse, T> {

    public HttpGsonResponseConverter() {
        this(new Gson());
    }

    public HttpGsonResponseConverter(@NonNull Gson gson) {
        super(gson, new Converter<HttpResponse, String>() {
            @Override
            public String convert(HttpResponse value, Type type) {
                try {
                    return value.body().string();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } finally {
                    value.close();
                }
            }
        });
    }
}
