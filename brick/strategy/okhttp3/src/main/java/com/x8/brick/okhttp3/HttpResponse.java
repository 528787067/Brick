package com.x8.brick.okhttp3;

import android.support.annotation.NonNull;

import com.x8.brick.parameter.Response;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

import okhttp3.CacheControl;
import okhttp3.Challenge;
import okhttp3.Handshake;
import okhttp3.Headers;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.ResponseBody;

public class HttpResponse implements Response, Closeable {

    public static HttpResponse response(@NonNull okhttp3.Response response) {
        return new HttpResponse(response);
    }

    private okhttp3.Response rawResponse;

    private HttpResponse(@NonNull Builder builder) {
        this(builder.rawBuilder.build());
    }

    private HttpResponse(@NonNull okhttp3.Response rawResponse) {
        this.rawResponse = rawResponse;
    }

    public okhttp3.Response raw() {
        return rawResponse;
    }

    public Request request() {
        return rawResponse.request();
    }

    public Protocol protocol() {
        return rawResponse.protocol();
    }

    public int code() {
        return rawResponse.code();
    }

    public boolean isSuccessful() {
        return rawResponse.isSuccessful();
    }

    public String message() {
        return rawResponse.message();
    }

    public Handshake handshake() {
        return rawResponse.handshake();
    }

    public List<String> headers(String name) {
        return rawResponse.headers(name);
    }

    public String header(String name) {
        return rawResponse.header(name);
    }

    public String header(String name, String defaultValue) {
        return rawResponse.header(name, defaultValue);
    }

    public Headers headers() {
        return rawResponse.headers();
    }

    public ResponseBody peekBody(long byteCount) throws IOException {
        return rawResponse.peekBody(byteCount);
    }

    public ResponseBody body() {
        return rawResponse.body();
    }

    public boolean isRedirect() {
        return rawResponse.isRedirect();
    }

    public okhttp3.Response networkResponse() {
        return rawResponse.networkResponse();
    }

    public okhttp3.Response cacheResponse() {
        return rawResponse.cacheResponse();
    }

    public okhttp3.Response priorResponse() {
        return rawResponse.priorResponse();
    }

    public List<Challenge> challenges() {
        return rawResponse.challenges();
    }

    public CacheControl cacheControl() {
        return rawResponse.cacheControl();
    }

    public long sentRequestAtMillis() {
        return rawResponse.sentRequestAtMillis();
    }

    public long receivedResponseAtMillis() {
        return rawResponse.receivedResponseAtMillis();
    }

    @Override
    public void close() {
        rawResponse.close();
    }

    public Builder newBuilder() {
        return new Builder(this);
    }

    @Override
    public String toString() {
        return rawResponse.toString();
    }

    public static class Builder {

        private okhttp3.Response.Builder rawBuilder;

        public Builder() {
            this(new okhttp3.Response.Builder());
        }

        public Builder(@NonNull okhttp3.Response response) {
            this(response.newBuilder());
        }

        public Builder(@NonNull okhttp3.Response.Builder builder) {
            this.rawBuilder = builder;
        }

        public Builder(@NonNull HttpResponse response) {
            this(response.raw());
        }

        public Builder(@NonNull Builder builder) {
            this(builder.rawBuilder);
        }

        public Builder request(Request request) {
            rawBuilder.request(request);
            return this;
        }

        public Builder request(HttpRequest request) {
            return request(request == null ? null : request.raw());
        }

        public Builder protocol(Protocol protocol) {
            rawBuilder.protocol(protocol);
            return this;
        }

        public Builder code(int code) {
            rawBuilder.code(code);
            return this;
        }

        public Builder message(String message) {
            rawBuilder.message(message);
            return this;
        }

        public Builder handshake(Handshake handshake) {
            rawBuilder.handshake(handshake);
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

        public Builder body(ResponseBody body) {
            rawBuilder.body(body);
            return this;
        }

        public Builder networkResponse(okhttp3.Response networkResponse) {
            rawBuilder.networkResponse(networkResponse);
            return this;
        }

        public Builder networkResponse(HttpResponse networkResponse) {
            return networkResponse(networkResponse == null ? null : networkResponse.raw());
        }

        public Builder cacheResponse(okhttp3.Response cacheResponse) {
            rawBuilder.cacheResponse(cacheResponse);
            return this;
        }

        public Builder cacheResponse(HttpResponse cacheResponse) {
            return cacheResponse(cacheResponse == null ? null : cacheResponse.raw());
        }

        public Builder priorResponse(okhttp3.Response priorResponse) {
            rawBuilder.priorResponse(priorResponse);
            return this;
        }

        public Builder priorResponse(HttpResponse priorResponse) {
            return priorResponse(priorResponse == null ? null : priorResponse.raw());
        }

        public Builder sentRequestAtMillis(long sentRequestAtMillis) {
            rawBuilder.sentRequestAtMillis(sentRequestAtMillis);
            return this;
        }

        public Builder receivedResponseAtMillis(long receivedResponseAtMillis) {
            rawBuilder.receivedResponseAtMillis(receivedResponseAtMillis);
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(this);
        }
    }
}
