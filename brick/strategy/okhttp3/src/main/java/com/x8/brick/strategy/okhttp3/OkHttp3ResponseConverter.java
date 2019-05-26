package com.x8.brick.strategy.okhttp3;

import com.x8.brick.converter.ResponseConverter;

import java.lang.reflect.Type;

import okhttp3.Response;

public class OkHttp3ResponseConverter implements ResponseConverter<OkHttp3Response, Response> {
    @Override
    public Response convert(OkHttp3Response response, Type responseType) {
        if (responseType == Response.class) {
            return response.response;
        }
        return null;
    }
}
