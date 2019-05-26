package com.x8.brick.module.converter.gson.okhttp3;

import android.support.annotation.NonNull;

import com.google.gson.Gson;
import com.x8.brick.converter.Converter;
import com.x8.brick.module.converter.gson.GsonResponseConverter;
import com.x8.brick.strategy.okhttp3.OkHttp3Response;

import java.io.IOException;
import java.lang.reflect.Type;

public class OkHttpGsonResponseConverter<T> extends GsonResponseConverter<OkHttp3Response, T> {

    public OkHttpGsonResponseConverter() {
        this(new Gson());
    }

    public OkHttpGsonResponseConverter(@NonNull Gson gson) {
        super(gson, new Converter<OkHttp3Response, String>() {
            @Override
            public String convert(OkHttp3Response value, Type type) {
                try (okhttp3.Response response = value.response) {
                    return response.body().string();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }
}
