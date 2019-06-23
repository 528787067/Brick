package com.x8.brick.okhttp3;

import android.support.annotation.NonNull;

import com.x8.brick.parameter.Request;

import java.net.URL;
import java.util.List;

import okhttp3.CacheControl;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.RequestBody;

public class HttpRequest implements Request {

    public static HttpRequest request(@NonNull okhttp3.Request request) {
        return new HttpRequest(request);
    }

    public static HttpRequest request(@NonNull okhttp3.Request request, boolean isStreaming) {
        return new HttpRequest(request, isStreaming);
    }

    private boolean isStreaming;
    private okhttp3.Request rawRequest;

    private HttpRequest(@NonNull Builder builder) {
        this(builder.rawBuilder.build(), builder.isStreaming);
    }

    private HttpRequest(@NonNull okhttp3.Request rawRequest) {
        this(rawRequest, false);
    }

    private HttpRequest(@NonNull okhttp3.Request rawRequest, boolean isStreaming) {
        this.rawRequest = rawRequest;
        this.isStreaming = isStreaming;
    }

    public HttpUrl url() {
        return rawRequest.url();
    }

    public String method() {
        return rawRequest.method();
    }

    public Headers headers() {
        return rawRequest.headers();
    }

    public String header(String name) {
        return rawRequest.header(name);
    }

    public List<String> headers(String name) {
        return rawRequest.headers(name);
    }

    public RequestBody body() {
        return rawRequest.body();
    }

    public Object tag() {
        return rawRequest.tag();
    }

    public CacheControl cacheControl() {
        return rawRequest.cacheControl();
    }

    public boolean isHttps() {
        return rawRequest.isHttps();
    }

    public okhttp3.Request raw() {
        return rawRequest;
    }

    public boolean isStreaming() {
        return isStreaming;
    }

    public Builder newBuilder() {
        return new Builder(this);
    }

    @Override
    public String toString() {
        return rawRequest.toString();
    }

    public static class Builder {

        private boolean isStreaming;
        private okhttp3.Request.Builder rawBuilder;

        public Builder() {
            this(new okhttp3.Request.Builder());
        }

        public Builder(@NonNull okhttp3.Request request) {
            this(request.newBuilder());
        }

        public Builder(@NonNull okhttp3.Request.Builder builder) {
            this.rawBuilder = builder;
        }

        public Builder(@NonNull HttpRequest request) {
            this(request.raw());
            isStreaming(request.isStreaming());
        }

        public Builder(@NonNull Builder builder) {
            this.rawBuilder = builder.rawBuilder;
            this.isStreaming = builder.isStreaming;
        }

        public Builder isStreaming(boolean isStreaming) {
            this.isStreaming = isStreaming;
            return this;
        }

        public Builder url(HttpUrl url) {
            rawBuilder.url(url);
            return this;
        }

        public Builder url(String url) {
            rawBuilder.url(url);
            return this;
        }

        public Builder url(URL url) {
            rawBuilder.url(url);
            return this;
        }

        public Builder header(String name, String value) {
            rawBuilder.header(name, value);
            return this;
        }

        public Builder addHeader(String name, String value) {
            rawBuilder.addHeader(name, value);
            return this;
        }

        public Builder removeHeader(String name) {
            rawBuilder.removeHeader(name);
            return this;
        }

        public Builder headers(Headers headers) {
            rawBuilder.headers(headers);
            return this;
        }

        public Builder cacheControl(CacheControl cacheControl) {
            rawBuilder.cacheControl(cacheControl);
            return this;
        }

        public Builder get() {
            rawBuilder.get();
            return this;
        }

        public Builder head() {
            rawBuilder.head();
            return this;
        }

        public Builder post(RequestBody body) {
            rawBuilder.post(body);
            return this;
        }

        public Builder delete(RequestBody body) {
            rawBuilder.delete(body);
            return this;
        }

        public Builder delete() {
            rawBuilder.delete();
            return this;
        }

        public Builder put(RequestBody body) {
            rawBuilder.put(body);
            return this;
        }

        public Builder patch(RequestBody body) {
            rawBuilder.patch(body);
            return this;
        }

        public Builder method(String method, RequestBody body) {
            rawBuilder.method(method, body);
            return this;
        }

        public Builder tag(Object tag) {
            rawBuilder.tag(tag);
            return this;
        }

        public HttpRequest build() {
            return new HttpRequest(this);
        }
    }
}
