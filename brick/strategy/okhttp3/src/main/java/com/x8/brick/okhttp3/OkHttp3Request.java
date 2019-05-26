package com.x8.brick.okhttp3;

import okhttp3.Request;

public class OkHttp3Request implements com.x8.brick.parameter.Request {

    public final Request request;
    public final boolean streaming;

    public OkHttp3Request(Request request) {
        this(request, false);
    }

    public OkHttp3Request(Request request, boolean streaming) {
        this.request = request;
        this.streaming = streaming;
    }
}
