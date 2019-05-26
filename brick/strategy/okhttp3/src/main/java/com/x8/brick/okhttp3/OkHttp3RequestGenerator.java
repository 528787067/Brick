package com.x8.brick.okhttp3;

import android.support.annotation.NonNull;

import com.x8.brick.core.HttpManager;
import com.x8.brick.core.RequestModel;
import com.x8.brick.task.TaskModel;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Objects;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;

final class OkHttp3RequestGenerator implements TaskModel.RequestGenerator<OkHttp3Request> {

    private HttpManager<OkHttp3Request, OkHttp3Response> httpManager;

    private Request.Builder requestBuilder;
    private String method;
    private boolean hasBody;
    private HttpUrl baseUrl;
    private String relativeUrl;
    private HttpUrl.Builder urlBuilder;
    private MediaType contentType;
    private boolean streaming;
    private RequestBody body;
    private boolean isFormEncoded;
    private FormBody.Builder formBuilder;
    private boolean isMultipart;
    private MultipartBody.Builder multipartBuilder;

    OkHttp3RequestGenerator(@NonNull HttpManager<OkHttp3Request, OkHttp3Response> httpManager) {
        this.httpManager = httpManager;
    }

    @Override
    public OkHttp3Request generateRequest(RequestModel requestModel) {
        new ParameterHandler.RequestBuilder().apply(this, requestModel);
        new ParameterHandler.MethodPath().apply(this, requestModel);
        new ParameterHandler.BaseUrl(httpManager).apply(this, requestModel);
        new ParameterHandler.RelativeUrl().apply(this, requestModel);
        new ParameterHandler.UrlBuilder(baseUrl, relativeUrl).apply(this, requestModel);
        new ParameterHandler.Path(relativeUrl).apply(this, requestModel);
        new ParameterHandler.Query(urlBuilder).apply(this, requestModel);
        new ParameterHandler.MediaType().apply(this, requestModel);
        new ParameterHandler.Header(requestBuilder).apply(this, requestModel);
        new ParameterHandler.Body().apply(this, requestModel);
        new ParameterHandler.FormEncoded().apply(this, requestModel);
        new ParameterHandler.Field(isFormEncoded).apply(this, requestModel);
        new ParameterHandler.Multipart().apply(this, requestModel);
        new ParameterHandler.Part(isMultipart).apply(this, requestModel);
        new ParameterHandler.Streaming().apply(this, requestModel);
        return generate();
    }

    private OkHttp3Request generate() {
        HttpUrl url;

        if (urlBuilder != null) {
            url = urlBuilder.build();
        } else {
            url = baseUrl.resolve(relativeUrl);
            if (url == null) {
                throw new IllegalArgumentException(
                        "Malformed URL. Base: " + baseUrl + ", Relative: " + relativeUrl);
            }
        }
        if (body == null) {
            if (formBuilder != null) {
                body = formBuilder.build();
            } else if (multipartBuilder != null) {
                body = multipartBuilder.build();
            } else if (hasBody) {
                body = RequestBody.create(null, new byte[0]);
            }
        }
        if (contentType != null) {
            if (body != null) {
                body = new ContentTypeOverridingRequestBody(body, contentType);
            } else {
                requestBuilder.addHeader("Content-Type", contentType.toString());
            }
        }
        Request request = requestBuilder
                .url(url)
                .method(method, body)
                .build();

        return new OkHttp3Request(request, streaming);
    }

    private abstract static class ParameterHandler {

        abstract void apply(@NonNull OkHttp3RequestGenerator builder, @NonNull RequestModel requestModel);

        static final class RequestBuilder extends ParameterHandler {
            @Override
            void apply(@NonNull OkHttp3RequestGenerator builder, @NonNull RequestModel requestModel) {
                builder.requestBuilder = new Request.Builder();
            }
        }

        static final class MethodPath extends ParameterHandler {
            @Override
            void apply(@NonNull OkHttp3RequestGenerator builder, @NonNull RequestModel requestModel) {
                RequestModel.MethodPath methodPath = requestModel.methodPaths().get(0);
                builder.method = methodPath.method;
                builder.hasBody = methodPath.hasBody;
            }
        }

        static final class BaseUrl extends ParameterHandler {

            private HttpManager<OkHttp3Request, OkHttp3Response> httpManager;

            BaseUrl(@NonNull HttpManager<OkHttp3Request, OkHttp3Response> httpManager) {
                this.httpManager = httpManager;
            }

            @Override
            void apply(@NonNull OkHttp3RequestGenerator builder, @NonNull RequestModel requestModel) {
                String hostName = httpManager.hostName();
                if (hostName == null && requestModel.hostNames().size() > 0) {
                    hostName = requestModel.hostNames().get(0);
                }
                if (hostName == null) {
                    hostName = RequestModel.HostName.DEFAULT;
                }
                String baseUrl = null;
                if (httpManager.hosts() != null) {
                    for (RequestModel.Host host : httpManager.hosts()) {
                        if (Objects.equals(hostName, host.name)) {
                            baseUrl = host.url;
                            break;
                        }
                    }
                }
                if (baseUrl == null) {
                    for (RequestModel.Host host : requestModel.hosts()) {
                        if (Objects.equals(hostName, host.name)) {
                            baseUrl = host.url;
                            break;
                        }
                    }
                }
                if (baseUrl == null) {
                    throw new IllegalArgumentException("The host name \"" + hostName + "\" is undefine.");
                }
                if (baseUrl.isEmpty()) {
                    throw new IllegalArgumentException("Base url is empty.");
                }
                HttpUrl httpUrl = HttpUrl.parse(baseUrl);
                if (httpUrl == null) {
                    throw new IllegalArgumentException("Illegal URL: " + baseUrl);
                }
                List<String> pathSegments = httpUrl.pathSegments();
                if (!"".equals(pathSegments.get(pathSegments.size() - 1))) {
                    throw new IllegalArgumentException("baseUrl must end in /: " + baseUrl);
                }
                builder.baseUrl = httpUrl;
            }
        }

        static final class RelativeUrl extends ParameterHandler {
            @Override
            void apply(@NonNull OkHttp3RequestGenerator builder, @NonNull RequestModel requestModel) {
                builder.relativeUrl = requestModel.urls().size() > 0
                        ? requestModel.urls().get(0) : requestModel.methodPaths().get(0).path;
            }
        }

        static final class UrlBuilder extends ParameterHandler {

            private HttpUrl baseUrl;
            private String relativeUrl;

            UrlBuilder(HttpUrl baseUrl, String relativeUrl) {
                this.baseUrl = baseUrl;
                this.relativeUrl = relativeUrl;
            }

            @Override
            void apply(@NonNull OkHttp3RequestGenerator builder, @NonNull RequestModel requestModel) {
                HttpUrl.Builder urlBuilder = baseUrl.newBuilder(relativeUrl);
                if (urlBuilder == null) {
                    throw new IllegalArgumentException(
                            "Malformed URL. Base: " + baseUrl + ", Relative: " + relativeUrl);
                }
                builder.urlBuilder = urlBuilder;
            }
        }

        static final class Path extends ParameterHandler {

            private String relativeUrl;

            Path(String relativeUrl) {
                this.relativeUrl = relativeUrl;
            }

            @Override
            void apply(@NonNull OkHttp3RequestGenerator builder, @NonNull RequestModel requestModel) {
                for (RequestModel.Path path : requestModel.paths()) {
                    if (path.value == null) {
                        throw new IllegalArgumentException(
                                "Path parameter \"" + path.name + "\" value must not be null.");
                    }
                    if (relativeUrl == null) {
                        throw new AssertionError();
                    }
                    String pathValue = Encoder.canonicalizeForPath(path.value, path.encoded);
                    relativeUrl = relativeUrl.replace("{" + path.name + "}", pathValue);
                }
                builder.relativeUrl = relativeUrl;
            }
        }

        static final class Query extends ParameterHandler {

            private HttpUrl.Builder urlBuilder;

            Query(HttpUrl.Builder urlBuilder) {
                this.urlBuilder = urlBuilder;
            }

            @Override
            void apply(@NonNull OkHttp3RequestGenerator builder, @NonNull RequestModel requestModel) {
                for (RequestModel.Query query : requestModel.querys()) {
                    if (query.encoded) {
                        urlBuilder.addEncodedQueryParameter(query.name, query.value);
                    } else {
                        urlBuilder.addQueryParameter(query.name, query.value);
                    }
                }
            }
        }

        static final class MediaType extends ParameterHandler {
            @Override
            void apply(@NonNull OkHttp3RequestGenerator builder, @NonNull RequestModel requestModel) {
                for (RequestModel.Header header : requestModel.headers()) {
                    if ("Content-Type".equalsIgnoreCase(header.name)) {
                        builder.contentType = okhttp3.MediaType.parse(header.value);
                    }
                }
            }
        }

        static final class Header extends ParameterHandler {

            private Request.Builder requestBuilder;

            Header(Request.Builder requestBuilder) {
                this.requestBuilder = requestBuilder;
            }

            @Override
            void apply(@NonNull OkHttp3RequestGenerator builder, @NonNull RequestModel requestModel) {

                for (RequestModel.Header header : requestModel.headers()) {
                    requestBuilder.addHeader(header.name, header.value);
                }
            }
        }

        static final class Body extends ParameterHandler {
            @Override
            void apply(@NonNull OkHttp3RequestGenerator builder, @NonNull RequestModel requestModel) {
                if (requestModel.bodys().size() > 0) {
                    builder.body = (RequestBody) requestModel.bodys().get(0);
                }
            }
        }

        static final class Field extends ParameterHandler {

            private boolean isFormEncoded;

            Field(boolean isFormEncoded) {
                this.isFormEncoded = isFormEncoded;
            }

            @Override
            void apply(@NonNull OkHttp3RequestGenerator builder, @NonNull RequestModel requestModel) {
                if (isFormEncoded) {
                    FormBody.Builder formBuilder = new FormBody.Builder();
                    for (RequestModel.Field field : requestModel.fields()) {
                        if (field.encoded) {
                            formBuilder.addEncoded(field.name, field.value);
                        } else {
                            formBuilder.add(field.name, field.value);
                        }
                    }
                    builder.formBuilder = formBuilder;
                }
            }
        }

        static final class Multipart extends ParameterHandler {
            @Override
            void apply(@NonNull OkHttp3RequestGenerator builder, @NonNull RequestModel requestModel) {
                builder.isMultipart = requestModel.multiparts().size() > 0;
            }
        }

        static final class Part extends ParameterHandler {

            private boolean isMultipart;

            Part(boolean isMultipart) {
                this.isMultipart = isMultipart;
            }

            @Override
            void apply(@NonNull OkHttp3RequestGenerator builder, @NonNull RequestModel requestModel) {
                if (isMultipart) {
                    MultipartBody.Builder multipartBuilder = new MultipartBody.Builder();
                    multipartBuilder.setType(MultipartBody.FORM);
                    for (RequestModel.Part part : requestModel.parts()) {
                        if (part.name.isEmpty()) {
                            multipartBuilder.addPart((MultipartBody.Part) part.data);
                        } else {
                            Headers headers = Headers.of("Content-Disposition", "form-data; name=\"" + part.name + "\"",
                                    "Content-Transfer-Encoding", part.encoding);
                            multipartBuilder.addPart(headers, (RequestBody) part.data);
                        }
                    }
                    builder.multipartBuilder = multipartBuilder;
                }
            }
        }

        static final class FormEncoded extends ParameterHandler {
            @Override
            void apply(@NonNull OkHttp3RequestGenerator builder, @NonNull RequestModel requestModel) {
                builder.isFormEncoded = requestModel.formUrlEncodeds().size() > 0;
            }
        }

        static final class Streaming extends ParameterHandler {
            @Override
            void apply(@NonNull OkHttp3RequestGenerator builder, @NonNull RequestModel requestModel) {
                builder.streaming = requestModel.streamings().size() > 0;
            }
        }
    }

    private static class ContentTypeOverridingRequestBody extends RequestBody {

        private final RequestBody delegate;
        private final MediaType contentType;

        ContentTypeOverridingRequestBody(RequestBody delegate, MediaType contentType) {
            this.delegate = delegate;
            this.contentType = contentType;
        }

        @Override public MediaType contentType() {
            return contentType;
        }

        @Override public long contentLength() throws IOException {
            return delegate.contentLength();
        }

        @Override public void writeTo(BufferedSink sink) throws IOException {
            delegate.writeTo(sink);
        }
    }

    private static class Encoder {

        private static final char[] HEX_DIGITS = {
                '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        private static final String PATH_SEGMENT_ALWAYS_ENCODE_SET = " \"<>^`{}|\\?#";

        Encoder() {
            throw new UnsupportedOperationException("u can't instantiate me...");
        }

        static String urlEncode(String url) {
            return urlEncode(url, "UTF-8");
        }

        static String urlEncode(String url, String charsetName) {
            if (url == null) {
                url = "";
            }
            try {
                url = URLEncoder.encode(url, charsetName);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return url;
        }

        static String canonicalizeForPath(String input, boolean alreadyEncoded) {
            int codePoint;
            for (int i = 0, limit = input.length(); i < limit; i += Character.charCount(codePoint)) {
                codePoint = input.codePointAt(i);
                if (codePoint < 0x20 || codePoint >= 0x7f
                        || PATH_SEGMENT_ALWAYS_ENCODE_SET.indexOf(codePoint) != -1
                        || (!alreadyEncoded && (codePoint == '/' || codePoint == '%'))) {
                    Buffer out = new Buffer();
                    out.writeUtf8(input, 0, i);
                    canonicalizeForPath(out, input, i, limit, alreadyEncoded);
                    return out.readUtf8();
                }
            }
            return input;
        }

        static void canonicalizeForPath(Buffer out, String input, int pos, int limit, boolean alreadyEncoded) {
            Buffer utf8Buffer = null;
            int codePoint;
            for (int i = pos; i < limit; i += Character.charCount(codePoint)) {
                codePoint = input.codePointAt(i);
                if (alreadyEncoded && (codePoint == '\t' || codePoint == '\n' || codePoint == '\f' || codePoint == '\r')) {
                    // noinspection UnnecessaryContinue
                    continue;
                } else if (codePoint < 0x20 || codePoint >= 0x7f
                        || PATH_SEGMENT_ALWAYS_ENCODE_SET.indexOf(codePoint) != -1
                        || (!alreadyEncoded && (codePoint == '/' || codePoint == '%'))) {
                    if (utf8Buffer == null) {
                        utf8Buffer = new Buffer();
                    }
                    utf8Buffer.writeUtf8CodePoint(codePoint);
                    while (!utf8Buffer.exhausted()) {
                        int b = utf8Buffer.readByte() & 0xff;
                        out.writeByte('%');
                        out.writeByte(HEX_DIGITS[(b >> 4) & 0xf]);
                        out.writeByte(HEX_DIGITS[b & 0xf]);
                    }
                } else {
                    out.writeUtf8CodePoint(codePoint);
                }
            }
        }
    }
}
