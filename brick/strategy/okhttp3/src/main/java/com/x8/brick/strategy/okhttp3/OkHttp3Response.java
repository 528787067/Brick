package com.x8.brick.strategy.okhttp3;

import okhttp3.Response;

public class OkHttp3Response implements com.x8.brick.parameter.Response {

    public final Response response;

    public OkHttp3Response(Response response) {
        this.response = response;
    }
}
